package com.yangtzelsl;

import azkaban.ServiceProvider;
import azkaban.alert.Alerter;
import azkaban.executor.*;
import azkaban.sla.SlaOption;
import azkaban.utils.Props;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * @Description: WeChatAlerter
 * @Author darren.qiu
 * @Date: 2021/08/26 11:38
 * @Version 1.0
 */
public class RobotAlerter implements Alerter {
    private static final Logger logger = Logger.getLogger(RobotAlerter.class);

    private final String wechatKey;

    public RobotAlerter(Props props) {
        wechatKey = props.getString("alert.wechatKey", "");
        logger.info("wechatKey: " + wechatKey);
    }

    /**
     * 成功的通知
     *
     * @param exFlow
     * @throws Exception
     */
    @Override
    public void alertOnSuccess(ExecutableFlow exFlow) throws Exception {
        logger.info("任务成功了，恭喜您！");
    }

    /**
     * 出现问题的通知
     *
     * @param exFlow
     * @param extraReasons
     * @throws Exception
     */
    @Override
    public void alertOnError(ExecutableFlow exFlow, String... extraReasons) throws Exception {

        // 企业微信API: https://work.weixin.qq.com/api/doc/90000/90136/91770

        //一般来说网络电话服务都是通过HTTP请求发送的，这里可以调用shell发送HTTP请求

        logger.info("任务失败了，请及时处理...");

        String flowName = exFlow.getFlowName();
        String projectName = exFlow.getProjectName();
        String executionId = String.valueOf(exFlow.getExecutionId());
        String failTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss"));

        String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=" + wechatKey;
        String markdownMessage = "{\n" +
                "    \"msgtype\": \"markdown\",\n" +
                "    \"markdown\": {\n" +
                "        \"content\": \"<font color=\\\"warning\\\">任务失败请相关同事注意!</font>\n" +
                "         >项目名称: <font color=\\\"comment\\\">"+projectName+"</font>\n" +
                "         >工作流名称: <font color=\\\"comment\\\">"+flowName+"</font>\n" +
                "         >执行序号: <font color=\\\"comment\\\">"+executionId+"</font>\n" +
                "         >任务失败时间: <font color=\\\"comment\\\">"+failTime+"</font>\"\n" +
                "    }\n" +
                "}";

        logger.info("发送的消息体内容为：" + markdownMessage);

        JsonObject alert = new JsonObject();
        String[] cmd = new String[8];
        cmd[0] = "curl";
        cmd[1] = "-H";
        cmd[2] = "Content-type: application/json";
        cmd[3] = "-X";
        cmd[4] = "POST";
        cmd[5] = "-d";
        cmd[6] = markdownMessage;
        cmd[7] = url;

        logger.info("发送请求的消息体是："+cmd.toString());
        Runtime.getRuntime().exec(cmd);

        // 注意：目前使用HTTP方式会发生故障，具体体现为：第一次失败可以发送告警，后续job运行状态一直为preparing状态，会跳过执行
        // 使用HTTP的方式发送请求

        /*
        String responseMessage = HttpClient.builder(url, HttpMethod.POST, String.class)
                .body(markdownMessage)
                .execute()
                .get();

        logger.info("返回的结果集内容为：" + responseMessage);
        */
    }

    /**
     * 首次出现问题的通知
     *
     * @param exFlow
     * @throws Exception
     */
    @Override
    public void alertOnFirstError(ExecutableFlow exFlow) throws Exception {
        logger.info("任务首次出现问题了，请及时处理...");
    }


    @Override
    public void alertOnSla(SlaOption slaOption, String slaMessage) throws Exception {

        logger.info("任务出现延迟了，请及时处理...");

        int execId = slaOption.getExecId();
        ExecutorLoader executorLoader = ServiceProvider.SERVICE_PROVIDER.getInstance(ExecutorLoader.class);
        ExecutableFlow exFlow = executorLoader.fetchExecutableFlow(execId);

        String flowName = exFlow.getFlowName();
        String projectName = exFlow.getProjectName();
        String executionId = String.valueOf(exFlow.getExecutionId());
        Duration duration = slaOption.getDuration();
        String durationStr = Long.toString(duration.toMinutes());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String execTime = formatter.format(exFlow.getStartTime());
        Status status = exFlow.getStatus();
        String lateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss"));


        String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=" + wechatKey;
        String markdownMessage = "{\n" +
                "    \"msgtype\": \"markdown\",\n" +
                "    \"markdown\": {\n" +
                "        \"content\": \"<font color=\\\"warning\\\">任务超时请相关同事注意!</font>\n" +
                "         >项目名称: <font color=\\\"comment\\\">"+projectName+"</font>\n" +
                "         >工作流名称: <font color=\\\"comment\\\">"+flowName+"</font>\n" +
                "         >执行序号: <font color=\\\"comment\\\">"+executionId+"</font>\n" +
                "         >超时告警时间: <font color=\\\"comment\\\">"+lateTime+"</font>\n" +
                "         >超时详细信息: <font color=\\\"comment\\\">执行中的任务预计将在"+execTime+"后的"+durationStr+"m 内完成,实际状态为:"+status+"</font>\"\n" +
                "    }\n" +
                "}";

        logger.info("发送的消息体内容为：" + markdownMessage);

        JsonObject alert = new JsonObject();
        String[] cmd = new String[8];
        cmd[0] = "curl";
        cmd[1] = "-H";
        cmd[2] = "Content-type: application/json";
        cmd[3] = "-X";
        cmd[4] = "POST";
        cmd[5] = "-d";
        cmd[6] = markdownMessage;
        cmd[7] = url;

        logger.info("发送请求的消息体是："+cmd.toString());
        Runtime.getRuntime().exec(cmd);
    }

    @Override
    public void alertOnFailedUpdate(Executor executor, List<ExecutableFlow> executions, ExecutorManagerException e) {
        logger.info("alertOnFailedUpdate.........");

    }

    @Override
    public void alertOnFailedExecutorHealthCheck(Executor executor, List<ExecutableFlow> list, ExecutorManagerException e, List<String> list1) {
        logger.info("alertOnFailedExecutorHealthCheck.........");
    }

}

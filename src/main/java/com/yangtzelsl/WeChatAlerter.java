package com.yangtzelsl;

import azkaban.alert.Alerter;
import azkaban.executor.ExecutableFlow;
import azkaban.executor.Executor;
import azkaban.executor.ExecutorManagerException;
import azkaban.sla.SlaOption;
import azkaban.utils.Props;
import cn.gjing.http.HttpClient;
import cn.gjing.http.HttpMethod;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * @Description: WeChatAlerter
 * @Author luis.liu
 * @Date: 2021/7/7 19:38
 * @Version 1.0
 */
public class WeChatAlerter implements Alerter {
    private static final Logger logger = Logger.getLogger(WeChatAlerter.class);

    private final String wechatKey;

    public WeChatAlerter(Props props) {
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

    }

    @Override
    public void alertOnSla(SlaOption slaOption, String slaMessage) throws Exception {

    }

    @Override
    public void alertOnFailedUpdate(Executor executor, List<ExecutableFlow> executions, ExecutorManagerException e) {

    }

    @Override
    public void alertOnFailedExecutorHealthCheck(Executor executor, List<ExecutableFlow> list, ExecutorManagerException e, List<String> list1) {

    }
}

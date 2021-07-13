package com.yangtzelsl;

import azkaban.alert.Alerter;
import azkaban.executor.ExecutableFlow;
import azkaban.executor.Executor;
import azkaban.executor.ExecutorManagerException;
import azkaban.sla.SlaOption;
import azkaban.utils.AbstractMailer;
import azkaban.utils.EmailMessageCreator;
import azkaban.utils.Props;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import java.util.List;


/**
 * @Description: WeChatAlerter
 * @Author luis.liu
 * @Date: 2021/7/7 19:38
 * @Version 1.0
 */
public class WeChatAlerter extends AbstractMailer implements Alerter {
    private static final Logger logger = Logger.getLogger(WeChatAlerter.class);

    private final String appKey;
    private final String url;

    public WeChatAlerter(Props props, EmailMessageCreator messageCreator) {
        super(props, messageCreator);
        appKey = props.getString("my.alert.appKey", "");
        url = props.getString("my.alert.url", "");
        logger.info("Appkey: " + appKey);
        logger.info("URL: " + url);
    }

    /**
     * 成功的通知
     *
     * @param exflow
     * @throws Exception
     */
    @Override
    public void alertOnSuccess(ExecutableFlow exflow) throws Exception {

    }

    /**
     * 出现问题的通知
     *
     * @param exflow
     * @param extraReasons
     * @throws Exception
     */
    @Override
    public void alertOnError(ExecutableFlow exflow, String... extraReasons) throws Exception {

        // 企业微信API: https://work.weixin.qq.com/api/doc/90000/90136/91770

        //一般来说网络电话服务都是通过HTTP请求发送的，这里可以调用shell发送HTTP请求
        JsonObject alert = new JsonObject();
        alert.addProperty("app", appKey);
        alert.addProperty("eventId", exflow.getId());
        alert.addProperty("eventType", "trigger");
        alert.addProperty("alarmContent", exflow.getId() + " fails!");
        alert.addProperty("priority", "2");
        String[] cmd = new String[8];
        cmd[0] = "curl";
        cmd[1] = "-H";
        cmd[2] = "Content-type: application/json";
        cmd[3] = "-X";
        cmd[4] = "POST";
        cmd[5] = "-d";
        cmd[6] = alert.toString();
        cmd[7] = url;
        logger.info("Sending wechat robot alert!");
        Runtime.getRuntime().exec(cmd);

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

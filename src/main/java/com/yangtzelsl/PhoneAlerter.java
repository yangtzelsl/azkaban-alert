package com.yangtzelsl;

import azkaban.alert.Alerter;
import azkaban.executor.ExecutableFlow;
import azkaban.executor.Executor;
import azkaban.executor.ExecutorManagerException;
import azkaban.sla.SlaOption;
import azkaban.utils.Props;
import com.yangtzelsl.utils.HttpUtils;

import java.util.List;

/**
 * @Description: PhoneAlerter
 * @Author luis.liu
 * @Date: 2021/7/16 10:32
 * @Version 1.0
 */
public class PhoneAlerter implements Alerter {
    private String url;
    private String content = "hello azkaban";
    private String app_key;

    //http://api.aiops.com/alert/api/event?app=a2db6c6083c144759e6fe8c3acd7781e&eventId=xxx&eventType=trigger&alarmName=xxx&priority=2
    //http://api.aiops.com/alert/api/event?app=%s&eventId=xxx&eventType=trigger&alarmName=xxx&priority=2
    public PhoneAlerter(Props props){
        url = props.getString("my.url");
        app_key = props.getString("my.app.key");
    }

    //todo 成功发通知调用
    @Override
    public void alertOnSuccess(ExecutableFlow executableFlow) throws Exception {
        HttpUtils.post(String.format(url,app_key), content);
    }

    //todo 失败发通知调用
    @Override
    public void alertOnError(ExecutableFlow executableFlow, String... strings) throws Exception {
        HttpUtils.post(String.format(url,app_key), content);
    }

    //todo  第一次失败的时候调用
    @Override
    public void alertOnFirstError(ExecutableFlow executableFlow) throws Exception {

    }

    @Override
    public void alertOnSla(SlaOption slaOption, String s) throws Exception {

    }

    @Override
    public void alertOnFailedUpdate(Executor executor, List<ExecutableFlow> list, ExecutorManagerException e) {

    }

    @Override
    public void alertOnFailedExecutorHealthCheck(Executor executor, List<ExecutableFlow> list, ExecutorManagerException e, List<String> list1) {

    }
}

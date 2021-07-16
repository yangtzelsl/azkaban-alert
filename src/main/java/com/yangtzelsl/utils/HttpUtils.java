package com.yangtzelsl.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
 * @Description: HttpUtils
 * @Author luis.liu
 * @Date: 2021/7/16 10:33
 * @Version 1.0
 */
public class HttpUtils {
    public static void get(String url) throws Exception {
        //todo 1、创建HttpClient
        HttpClient httpClient = new HttpClient();

        //todo 2、创建Method方法
        GetMethod getMethod = new GetMethod(url);

        //todo 3、发起请求
        int code = httpClient.executeMethod(getMethod);

        //todo 4、判断请求是否成功
        if (code == 200) {
            //todo 5、打印结果
            System.out.println(getMethod.getResponseBodyAsString());
        }

    }

    public static void post(String url, String content) throws Exception {

        // todo 1、创建HttpClient
        HttpClient httpClient = new HttpClient();

        // todo 2、创建Method
        PostMethod method = new PostMethod(url);

        // todo 3、设置body参数
        StringRequestEntity entity = new StringRequestEntity(content, "application/json", "utf-8");
        method.setRequestEntity(entity);

        // todo 4、发起请求
        int code = httpClient.executeMethod(method);

        // todo 5、判断请求是否成功
        if(code==200){
            System.out.println(method.getResponseBodyAsString());
        }
    }
}

import cn.gjing.http.HttpClient;
import cn.gjing.http.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: Test
 * @Author luis.liu
 * @Date: 2021/7/14 17:15
 * @Version 1.0
 */
public class Test {

    @org.junit.Test
    public static String execCurl(String[] cmds) {
        ProcessBuilder process = new ProcessBuilder(cmds);
        Process p;
        try {
            p = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty(","));
            }
            return builder.toString();

        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        }
        return null;

    }

    @org.junit.Test
    public static void testHttp() {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=abc335a5-xxxx-xxxx-bb8b-61c1b72a79b7";
        Map<String, Object> param = new HashMap<>();
        param.put("msgtype", "text");
        param.put("text", "content");

        String ss = "{\n" +
                "    \"msgtype\": \"text\",\n" +
                "    \"text\": {\n" +
                "        \"content\": \"广州今日天气：29度，大部分多云，降雨概率：60%\",\n" +
                "        \"mentioned_list\":[\"wangqing\",\"@all\"],\n" +
                "        \"mentioned_mobile_list\":[\"13800001111\",\"@all\"]\n" +
                "    }\n" +
                "}\n";

        String str = HttpClient.builder(url, HttpMethod.POST, String.class)
                .body(ss)
                .execute()
                .get();
        System.out.println(str);
    }
}

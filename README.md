# 项目说明

本项目为azkaban的自定义报警功能

- 邮箱报警(默认)
- 企业微信报警
- 钉钉报警
- 电话报警

# 引用外部lib包

![img.png](img.png)

# 部署方式

- 将项目打jar包
- 注册睿象云账号：https://newuser.aiops.com/ 个人亲测，免费有几条的额度，可用，如果是企业生产，就买吧
- 可以使用PostMan测试一下发送post请求，会打电话，会发短信，发邮件等
- 新建/opt/module/azkaban/azkaban-web-server-3.84.4/plugin/alerter/phone-alerter文件夹，并在内部新建conf和lib两个目录
```shell
mkdir -p /opt/module/azkaban/azkaban-web-server-3.84.4/plugins/alerter/phone-alerter/conf /opt/module/azkaban/azkaban-web-server-3.84.4/plugins/alerter/phone-alerter/lib
```
- 在新建的phone-alerter/conf目录里，新建plugin.properties
```shell
#name一定要设置email，用以覆盖默认的邮件报警
alerter.name=email
alerter.external.classpaths=lib
alerter.class=com.atguigu.PhoneAlterter
#这两个参数和你使用的AlertAPI有关系!!!!!!
# appKey为自己睿象云申请的key
# url为睿象云提供的url地址
# 发送的主题内容在代码的部分有体现
my.alert.appKey=f10c90ac-c768-f2db-xxxx-a056f922350d
my.alert.url=http://http://api.aiops.com/alert/api/event
```
- 代码打jar包后，上传到/opt/module/azkaban/azkaban-web-server-3.84.4/lib文件夹
- 重启web服务(假定生产环境已经启动了azkaban，且之前没有集成自定义告警)
- azkaban告警处设置通知，随便设置点内容，实际会采用睿象云覆盖
![img_2.png](img_2.png)
  
- 大功告成，检查告警信息即可

# 结尾
以上项目本人亲测可用，唯一就是依赖第三方告警平台，要花钱！
如果想使用企业微信或钉钉机器人，睿象云免费版不支持，需要收费版

# 其它思路

- 假如具有企业微信或钉钉机器人的创建权限(没有权限找有权限的人创建即可)，可以创建一个机器人

- 企业微信机器人API参考文档：https://work.weixin.qq.com/api/doc/90000/90136/91770
- 钉钉机器人API参考文档：https://developers.dingtalk.com/document/app/custom-robot-access

- 注意保护好自己的webhook地址(防止信息轰炸)

- 使用PostMan测试能否接收消息

- 将对应的配置文件的appKey替换为webhook的key

- 将对应的url的地址替换为企业微信api地址

- 由于该方式本人没有测试，不能保证成功，可自行尝试

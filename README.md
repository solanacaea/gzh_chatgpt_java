# gzh_chatgpt_java
java服务对接微信公众号消息接口

## 使用方法
修改 /src/main/resources/application.properties里的信息:
```
openai.api-key1: sk-你的api-key
wx_jszaz_app_id: 你的appid
wx_jszaz_app_secret: 你的appsecret
```

打包：```mvn package ```

启动: ```./start start ```

请求接口：http://localhost:8080/gzh/wx

需要把公众号地址映射到内网地址，比如nginx。

## 说明
公众号被动回复消息官方解释：
```
假如服务器无法保证在五秒内处理并回复，必须做出下述回复，这样微信服务器才不会对此作任何处理，并且不会发起重试（这种情况下，可以使用客服消息接口进行异步回复），否则，将出现严重的错误提示。详见下面说明：

1、直接回复success（推荐方式） 2、直接回复空串（指字节长度为0的空字符串，而不是XML结构体中content字段的内容为空）

一旦遇到以下情况，微信都会在公众号会话中，向用户下发系统提示“该公众号暂时无法提供服务，请稍后再试”：

1、开发者在5秒内未回复任何内容 2、开发者回复了异常数据，比如JSON数据等
```
由于ChatGPT接口耗时一般超过5秒，所以先回复“success”，然后异步等待OpenAI API结果返回，再将结果推送给用户。
AskController.java
```
String respText;
try {
    userService.plus();
    CompletableFuture.runAsync(() -> {
        askService.ask(map);
        userService.minus();
    });
    respText = "success";
} catch (Exception e) {
    respText = wxService.processRequest(ERROR_RESP_MSG, map);
} finally {
    logger.info("当前并行请求数量：" + userService.get());
}
out.print(respText);
out.flush();
```

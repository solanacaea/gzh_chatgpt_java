package com.pandaai.openai;

import com.pandaai.util.WechatMessageUtil;
import com.pandaai.wechat.entity.vo.wechat.message.customer.CustomerMsgRequest;
import com.pandaai.wechat.entity.vo.wechat.message.customer.CustomerMsgResponse;
import com.pandaai.wechat.entity.vo.wechat.message.customer.TextContent;
import com.pandaai.wechat.entity.vo.wechat.token.AccessToken;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class WxServiceTest {
    public static void main(String[] args) {
        accessToken();
//        send2User();
    }

    public static void accessToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/stable_token";
        Map<String, String> param = new HashMap<>();
        param.put("grant_type", "client_credential");
        param.put("appid", "");
        param.put("secret", "");
        AccessToken result = new RestTemplate().postForObject(url, param, AccessToken.class);
        System.out.println(result);
    }

    public static void send2User() {
        String token = "";
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + token;
        String openid = "";
        CustomerMsgRequest message = new CustomerMsgRequest();
        message.setMsgtype(WechatMessageUtil.MESSAGE_TEXT);
        message.setTouser(openid);
        TextContent content = new TextContent();
        content.setContent("你的消息我已经收到！");
        message.setText(content);

        CustomerMsgResponse response = new RestTemplate().postForObject(
                url, message, CustomerMsgResponse.class);
        System.out.println("发送客服消息返回信息:" + response);

    }
}

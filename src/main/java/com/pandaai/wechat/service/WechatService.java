package com.pandaai.wechat.service;

import com.pandaai.util.WechatMessageUtil;
import com.pandaai.wechat.entity.vo.wechat.message.customer.CustomerMsgRequest;
import com.pandaai.wechat.entity.vo.wechat.message.customer.CustomerMsgResponse;
import com.pandaai.wechat.entity.vo.wechat.message.TextMessage;
import com.pandaai.wechat.entity.vo.wechat.message.customer.TextContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class WechatService {

    @Autowired
    private AccessTokenService tokenService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wx.url.host}")
    private String serverHost;

    @Value("${wx.url.send}")
    private String sendUrl;

    public String processRequest(String message, Map<String, String> map) {
        // 发送方帐号（一个OpenID）
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        // 对消息进行处理
        TextMessage textMessage = new TextMessage();
        textMessage.setMsgType(WechatMessageUtil.MESSAGE_TEXT);
        textMessage.setToUserName(fromUserName);
        textMessage.setFromUserName(toUserName);
        textMessage.setCreateTime(System.currentTimeMillis());
        textMessage.setContent(message);
        return WechatMessageUtil.textMessageToXml(textMessage);
    }

    public boolean send2User(String openid, String result) {
        String accessToken = tokenService.obtainAccessToken();
        String url = serverHost + sendUrl + accessToken;
        CustomerMsgRequest message = new CustomerMsgRequest();
        message.setMsgtype(WechatMessageUtil.MESSAGE_TEXT);
        message.setTouser(openid);
        TextContent content = new TextContent();
        content.setContent(result);
        message.setText(content);
        int i = 1;
        while (i <= 3) { //循环发送3次
            CustomerMsgResponse response = restTemplate.postForObject(
                    url, message, CustomerMsgResponse.class);
            assert response != null;
            if (response.getErrcode() == 40001) {
                tokenService.cacheEvict();
                url = serverHost + sendUrl + tokenService.obtainAccessToken();
            }
            log.info("发送客服消息返回信息:" + response.toString());
            if(response.getErrcode() == 0) { //发送成功-退出循环发送
                i = 4;
                return true;
            }else{
                i++; //发送失败-继续循环发送
            }
        }
        return false;
    }

}

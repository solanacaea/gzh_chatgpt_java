package com.pandaai.wechat.service;

import com.pandaai.util.WechatMessageUtil;
import com.pandaai.wechat.entity.vo.wechat.message.customer.CustomerMsgRequest;
import com.pandaai.wechat.entity.vo.wechat.message.customer.CustomerMsgResponse;
import com.pandaai.wechat.entity.vo.wechat.message.TextMessage;
import com.pandaai.wechat.entity.vo.wechat.message.customer.TextContent;
import com.pandaai.wechat.entity.vo.wechat.token.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@CacheConfig(cacheNames = WechatService.CACHE_NAME)
public class WechatService {
    protected static final String CACHE_NAME = "WechatService";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wx_jszaz_app_id}")
    private String appId;

    @Value("${wx_jszaz_app_secret}")
    private String appSec;

    @Value("${wx.url.host}")
    private String serverHost;

    @Value("${wx.url.token.stable}")
    private String stableTokenUrl;

    @Value("${wx.url.send}")
    private String sendUrl;

    public String processRequest(String message, Map<String, String> map) {
        // 发送方帐号（一个OpenID）
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        // 消息类型
        String msgType = map.get("MsgType");
        // 默认回复一个"success"
        String responseMessage = "success";
        // 对消息进行处理
        if (WechatMessageUtil.MESSAGE_TEXT.equals(msgType)) {// 文本消息
            TextMessage textMessage = new TextMessage();
            textMessage.setMsgType(WechatMessageUtil.MESSAGE_TEXT);
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(System.currentTimeMillis());
            textMessage.setContent(message);
            responseMessage = WechatMessageUtil.textMessageToXml(textMessage);
        }
        log.info(responseMessage);
        return responseMessage;
    }

    @Cacheable(cacheManager = "expire2hManager", unless = "#result == null")
    public String obtainAccessToken() {
        String url = serverHost + stableTokenUrl;

        Map<String, Object> param = new HashMap<>();
        param.put("grant_type", "client_credential");
        param.put("appid", appId);
        param.put("secret", appSec);
        param.put("force_refresh", true);
        log.info("WechatService.obtainAccessToken ...");

        AccessToken result = new RestTemplate().postForObject(url, param, AccessToken.class);
        assert result != null;
        return result.getAccess_token();
    }

    public boolean send2User(String openid, String result) {
        String accessToken = obtainAccessToken();
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
                cacheEvict();
                url = serverHost + sendUrl + obtainAccessToken();
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

    @CacheEvict(allEntries = true)
    public void cacheEvict() {
        log.info("cacheEvict cacheNames: {} ...", CACHE_NAME);
    }

}

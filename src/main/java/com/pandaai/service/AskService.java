package com.pandaai.service;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.pandaai.wechat.service.WechatService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.pandaai.util.AppConstants.ERROR_RESP_MSG;

@Service
public class AskService {

    private static final Logger logger = LoggerFactory.getLogger(AskService.class);

    @Value("${openai.model.chat}")
    private String chatModel;

    @Autowired
    private OpenAiClient client;

    @Autowired
    private WechatService wxService;

    public static final int maxLength = 200;
    public static final String REQUEST_MSG_APPEND = ", 请回答以上内容，并且返回最多280个字符。";

    public String ask(Map<String, String> map) {
        try {
            String q = map.get("Content");
            String from = map.get("FromUserName");
            q += REQUEST_MSG_APPEND;
            Message message = Message.builder().role(Message.Role.USER).content(q).build();
            List<Message> msgList = Collections.singletonList(message);
            ChatCompletion chatCompletion = ChatCompletion.builder()
                    .maxTokens(maxLength)
                    //                .model(chatModel)
                    .messages(msgList)
                    .user(from)
                    .build();
            ChatCompletionResponse response = client.chatCompletion(chatCompletion);
            logger.info(response.toString());
            ChatChoice result = response.getChoices().get(0);
            String respText = result.getMessage().getContent();
//            long timeCost = (System.currentTimeMillis() / 1000 - response.getCreated());
            push2User(map, respText);
            return respText;
        } catch (Exception e) {
            logger.error("ask异常："+ ExceptionUtils.getStackTrace(e));
            return ERROR_RESP_MSG;
        }
    }

    public void push2User(Map<String, String> map, String content) {
        String openid = map.get("FromUserName");
        boolean result = wxService.send2User(openid, content);

    }
}

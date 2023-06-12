package com.pandaai.service;

import com.alibaba.fastjson.JSONObject;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import com.pandaai.util.UserContextUtils;
import com.pandaai.wechat.service.WechatService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.pandaai.util.AppConstants.ERROR_RESP_MSG;

@Service
public class AskService {

    private static final Logger logger = LoggerFactory.getLogger(AskService.class);

    private static Map<String, List<ChatMessage>> USER_CACHE = new ConcurrentHashMap<>();

    @Value("${openai.model.chat}")
    private String chatModel;

    @Value("${openai.gpt35.deploymentId}")
    private String deploymentOrModelId;

    @Autowired
    private OpenAIClient client;

    @Autowired
    private WechatService wxService;

    public static final int maxLength = 200;
//    public static final String REQUEST_MSG_APPEND = ", please answer above question and return at most 280 characters.";
    public static final String SYSTEM_MESSAGE = "你是胖达AI小助手，请回答用户问题，并且每次返回280个字符以内的内容。";

    public String ask(Map<String, String> map) {
        try {
            String q = map.get("Content");
            String from = map.get("FromUserName");

            List<ChatMessage> history = UserContextUtils.get(from);
            if (history == null) {
                history = new ArrayList<>();
                history.add(new ChatMessage(ChatRole.SYSTEM).setContent(SYSTEM_MESSAGE));
                history.add(new ChatMessage(ChatRole.USER).setContent(q));
                UserContextUtils.put(from, history);
            } else {
                history.add(new ChatMessage(ChatRole.USER).setContent(q));
                int historySize = CollectionUtils.size(history);
                if (historySize > 5) {
                    history = history.subList(historySize - 5, historySize);
                }
            }

            ChatCompletionsOptions opt = new ChatCompletionsOptions(history);
            opt.setMaxTokens(maxLength);
            opt.setModel(chatModel);
            opt.setN(1);

            ChatCompletions response = client.getChatCompletions(deploymentOrModelId, opt);
            logger.info(JSONObject.toJSONString(response));

            ChatChoice result = response.getChoices().get(0);
            String resultText = result.getMessage().getContent();

            push2User(map, resultText);
            return resultText;
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

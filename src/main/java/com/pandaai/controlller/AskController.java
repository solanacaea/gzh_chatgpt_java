package com.pandaai.controlller;

import com.pandaai.service.AskService;
import com.pandaai.service.UserService;
import com.pandaai.wechat.service.WechatService;
import com.pandaai.util.CheckUtil;
import com.pandaai.util.WechatMessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.pandaai.util.AppConstants.ERROR_RESP_MSG;
import static com.pandaai.util.AppConstants.WELCOME_MSG;


@RestController
@RequestMapping("/gzh")
public class AskController {
    private static final String SUCCESS = "success";

    @Autowired
    private AskService askService;

    @Autowired
    private UserService userService;

    @Autowired
    private WechatService wxService;

    private static final Logger logger = LoggerFactory.getLogger(AskController.class);

    @GetMapping("/wx")
    public void wechatService(PrintWriter out, HttpServletResponse response,
                              @RequestParam(value = "signature", required = false) String signature,
                              @RequestParam String timestamp,
                              @RequestParam String nonce,
                              @RequestParam String echostr) {
        if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
            out.print(echostr);
        }
    }

    public void ask(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, String> map = WechatMessageUtil.xmlToMap(request);
        logger.info("请求参数: " + map.toString());
        String msgType = map.get("MsgType");
        if (WechatMessageUtil.MESSAGE_EVENT.equals(msgType) &&
                WechatMessageUtil.MESSAGE_EVENT_SUBSCRIBE.equals(map.get("Event"))) {
            String respText = wxService.processRequest(WELCOME_MSG, map);
            out.print(respText);
            out.flush();
            return;
        }
        if (!WechatMessageUtil.MESSAGE_TEXT.equals(msgType)) {
            out.print(SUCCESS);
            out.flush();
            return;
        }

        String overloaded = userService.checkUserDosage();
        if (overloaded != null) {
            String respText = wxService.processRequest(overloaded, map);
            out.print(respText);
            out.flush();
            return;
        }

        String respText;
        try {
            userService.plus();
            CompletableFuture.runAsync(() -> {
                askService.ask(map);
                userService.minus();
            });
            respText = SUCCESS;
        } catch (Exception e) {
            respText = wxService.processRequest(ERROR_RESP_MSG, map);
        } finally {
            logger.info("当前并行请求数量：" + userService.get());
        }
        out.print(respText);
        out.flush();
    }


    /**
     * 接收来自微信发来的消息
     *
     * @param out
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/wx", method = RequestMethod.POST)
    public void wechatServicePost(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        try {
            ask(request, response);
        } catch (IOException e) {
            out.print(SUCCESS);
            out.flush();
        }
    }

}

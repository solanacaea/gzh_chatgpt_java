package com.pandaai.controlller;

import com.pandaai.service.AskService;
import com.pandaai.service.UserService;
import com.wechat.service.WechatService;
import com.pandaai.util.CheckUtil;
import com.pandaai.util.WechatMessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

import static com.pandaai.util.AppConstants.ERROR_RESP_MSG;


@RestController
@RequestMapping("/gzh")
public class AskController {

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

    public void ask(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
        logger.info("请求: " + request);
        Map<String, String> map = WechatMessageUtil.xmlToMap(request);
        logger.info("请求参数: " + map.toString());

        String overloaded = userService.checkUserDosage();
        if (overloaded != null) {
            String respText = wxService.processRequest(overloaded, map);
            out.print(respText);
            out.flush();
        }

        String respText;
        try {
            int currReq = userService.plus();
            String resp = askService.ask(map);
            if (StringUtils.equals("", resp)) {
                respText = "success";
            } else {
                respText = wxService.processRequest(resp, map);
            }
        } catch (Exception e) {
            respText = wxService.processRequest(ERROR_RESP_MSG, map);
        } finally {
            int currReq = userService.minus();
            logger.info("当前并行请求数量：" + currReq);
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
        ask(out, request, response);
    }

}
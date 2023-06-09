package com.pandaai.wechat.entity.vo.wechat.token;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = { "access_token" })
public class AccessToken {
    private String errcode;
    private String errmsg;
    private String access_token;
    private int expires_in;
}

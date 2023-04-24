package com.wechat.entity.vo.wechat.message.customer;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CustomerMsgResponse {
    private String msgid;
    private String code;
    private int errcode;
    private String errmsg;

}

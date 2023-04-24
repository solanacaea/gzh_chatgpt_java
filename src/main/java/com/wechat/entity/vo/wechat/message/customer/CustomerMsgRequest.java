package com.wechat.entity.vo.wechat.message.customer;

public class CustomerMsgRequest {
    private String touser;
    private String msgtype;
    private TextContent text;

    public String getTouser() {
        return touser;
    }
    public void setTouser(String touser) {
        this.touser = touser;
    }
    public String getMsgtype() {
        return msgtype;
    }
    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }
    public TextContent getText() {
        return text;
    }
    public void setText(TextContent text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Message [touser=" + touser + ", msgtype=" + msgtype + ", text="
                + text + "]";
    }
}

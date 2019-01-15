package com.duanglink.huaweipush;

public class HuaweiContent {
    public static boolean finishStart = false;

    public String title="";
    public String msg_id="";
    public String content="";
    public String msg_sub_type = "";


    public String getMsg_sub_type() {
        return msg_sub_type;
    }

    public void setMsg_sub_type(String msg_sub_type) {
        this.msg_sub_type = msg_sub_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

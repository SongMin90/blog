package com.my.blog.website.modal.Vo;

import java.io.Serializable;

/**
 * @author SongM
 * @description 聊天
 * @date 2018/4/7 14:37
 */
public class ChatVo implements Serializable {
    private int id;
    private String userId;
    private String datetime;
    private String username;
    private String message;

    public ChatVo(String userId, String datetime, String username, String message) {
        this.userId = userId;
        this.datetime = datetime;
        this.username = username;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

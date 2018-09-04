package com.my.blog.website.modal.Bo;

import java.io.Serializable;

/**
 * 后台统计对象
 */
public class StatisticsBo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long articles;
    private Long comments;
    private Long links;
    private Long attachs;
    private Long visitors;
    private Long logs;
    private Long chats;

    public Long getArticles() {
        return articles;
    }

    public void setArticles(Long articles) {
        this.articles = articles;
    }

    public Long getComments() {
        return comments;
    }

    public void setComments(Long comments) {
        this.comments = comments;
    }

    public Long getLinks() {
        return links;
    }

    public void setLinks(Long links) {
        this.links = links;
    }

    public Long getAttachs() {
        return attachs;
    }

    public void setAttachs(Long attachs) {
        this.attachs = attachs;
    }

    public Long getVisitors() {
		return visitors;
	}

	public void setVisitors(Long visitors) {
		this.visitors = visitors;
	}

    public Long getLogs() {
        return logs;
    }

    public void setLogs(Long logs) {
        this.logs = logs;
    }

    public Long getChats() {
        return chats;
    }

    public void setChats(Long chats) {
        this.chats = chats;
    }
}

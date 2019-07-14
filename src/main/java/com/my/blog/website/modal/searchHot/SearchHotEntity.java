package com.my.blog.website.modal.searchHot;

/**
 * @author songm
 * @datetime 2019/6/15 18:27
 */
public class SearchHotEntity {

    private Long id;
    private Integer sort;
    private Long num;
    private String tab;
    private String title;
    private String linkUrl;
    private String channel;
    private String date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "SearchHotEntity{" +
                "id=" + id +
                ", sort=" + sort +
                ", num=" + num +
                ", tab='" + tab + '\'' +
                ", title='" + title + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", channel='" + channel + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}

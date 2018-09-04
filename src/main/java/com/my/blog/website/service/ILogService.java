package com.my.blog.website.service;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.modal.Vo.LogVo;

import java.util.List;

/**
 * Created by BlueT on 2017/3/4.
 */
public interface ILogService {

    /**
     * 保存操作日志
     *
     * @param logVo
     */
    void insertLog(LogVo logVo);

    /**
     *  保存
     * @param action
     * @param data
     * @param ip
     * @param authorId
     */
    void insertLog(String action, String data, String ip, Integer authorId);

    /**
     * 获取日志分页
     * @param page 当前页
     * @param limit 每页条数
     * @return 日志
     */
    List<LogVo> getLogs(int page,int limit);

    /**
     * 分页查询所有日志
     * @param page
     * @param limit
     * @return
     */
    PageInfo<LogVo> getLogsWithpage(int page, int limit);
}

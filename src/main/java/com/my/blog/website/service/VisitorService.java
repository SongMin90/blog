package com.my.blog.website.service;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.modal.Vo.VisitorVo;

/**
 * 访客Service
 * @author SongM
 * @date 2018年1月5日 下午7:17:42
 */
public interface VisitorService {

	/**
	 * 添加访客信息
	 * @param request
	 */
	void save(HttpServletRequest request);

	/**
	 * 分页查询所有访客信息
	 * @param page
	 * @param limit
	 * @return
	 */
	PageInfo<VisitorVo> getVisitorsWithpage(int page, int limit);

	/**
	 * 通过id拦截该ip
	 * @param id
	 * @return
	 */
	VisitorVo interceptById(String id);

	/**
	 * 统计时间段访客
	 * @param type
	 * @return
	 */
    JSONObject visitorsStatistics(int type);
}

package com.my.blog.website.service.impl;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.my.blog.website.utils.AddressUtils;
import com.my.blog.website.utils.MapCache;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.dao.VisitorVoMapper;
import com.my.blog.website.modal.Vo.VisitorVo;
import com.my.blog.website.service.VisitorService;

import org.apache.commons.collections.MapUtils;

@Service
public class VisitorServiceImpl implements VisitorService {

	@Resource
	private VisitorVoMapper visitorDao;
	
	@Override
	public void save(HttpServletRequest request, String id, String ip, String terminalInfo) {
		//request.getSession(false)==null可以近似的判断是否过期：如果已经过期，那么返回的是null，但是当起一次请求，刚刚建立一个session的时候，上述方法也返回null
		//所以应该这个做
		if(null == request.getSession(false)) {
			if(true == request.getSession(true).isNew()) {
				// 取到访客信息
				String addr = AddressUtils.getIpAddress(ip);
				Date datetime = new Date();
				// 信息存入bean
				VisitorVo visitorVo = new VisitorVo();
				visitorVo.setId(id);
				visitorVo.setIp(ip);
				visitorVo.setAddr(addr);
				visitorVo.setTerminal(terminalInfo);
				visitorVo.setDatetime(datetime);
				// insertOrUpdate
				visitorDao.insertOrUpdate(visitorVo);
			}
		}
	}

	@Override
	public PageInfo<VisitorVo> getVisitorsWithpage(int page, int limit) {
		PageHelper.startPage(page, limit);
		List<VisitorVo> visitors = visitorDao.selectAll();
		return new PageInfo<>(visitors);
	}

	@Override
	public VisitorVo interceptById(String id) {
		// 通过id查询ip信息
		VisitorVo visitorVo = visitorDao.selectById(id);
		// 改变intercept
		boolean isIntercept = visitorVo.isIntercept() != true;
		visitorVo.setIntercept(isIntercept);
		// 更新
		visitorDao.updateById(visitorVo);
		// 删除缓存
		MapCache.single().del(id);
		return visitorVo;
	}

	@Override
	public JSONObject visitorsStatistics(int type) {
		JSONObject json = new JSONObject();
		HashMap<String, Object> map = new HashMap<>();
		map.put("p_data", "");
		map.put("p_labels", "");
		// 判断type，0为最近7天，1为最近15天，2为最近30天
		if (type == 0) {
			map.put("p_count", 7);
		} else if (type == 1) {
			map.put("p_count", 15);
		} else if (type == 2) {
			map.put("p_count", 30);
		}
		// 执行存储过程
		try {
			visitorDao.visitorsStatistics(map);
		} catch (Exception e) {}
		// 获取数据
		String data = MapUtils.getString(map, "p_data", "[]");
        json.put("data", data.split(","));

		String labels = MapUtils.getString(map, "p_labels", "[]");
        json.put("labels", labels.split(","));

        return json;
	}

}

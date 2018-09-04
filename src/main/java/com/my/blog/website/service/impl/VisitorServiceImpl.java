package com.my.blog.website.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.my.blog.website.utils.AddressUtils;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.dao.VisitorVoMapper;
import com.my.blog.website.modal.Vo.VisitorVo;
import com.my.blog.website.service.VisitorService;
import com.my.blog.website.utils.TaleUtils;
import com.my.blog.website.utils.TerminalUtil;

@Service
public class VisitorServiceImpl implements VisitorService {

	@Resource
	private VisitorVoMapper visitorDao;
	
	@Override
	public void save(HttpServletRequest request) {
		//request.getSession(false)==null可以近似的判断是否过期：如果已经过期，那么返回的是null，但是当起一次请求，刚刚建立一个session的时候，上述方法也返回null
		//所以应该这个做
		if(null == request.getSession(false)) {
			if(true == request.getSession(true).isNew()) {
				// 取到访客信息
				String ip = TerminalUtil.getIp(request);
				String addr = AddressUtils.getIpAddress(ip);
				String terminal = TerminalUtil.getTerminalInfo(request);
				Date datetime = new Date();
				String id = TaleUtils.MD5encode(ip + terminal);
				// 信息存入bean
				VisitorVo visitorVo = new VisitorVo();
				visitorVo.setId(id);
				visitorVo.setIp(ip);
				visitorVo.setAddr(addr);
				visitorVo.setTerminal(terminal);
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
		return visitorVo;
	}

}

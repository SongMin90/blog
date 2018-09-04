package com.my.blog.website.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.my.blog.website.modal.Vo.VisitorVo;

@Component
public interface VisitorVoMapper {

	long insert(VisitorVo visitorVo);

	VisitorVo selectById(@Param("id") String id);

	void updateById(VisitorVo visitorVo);
	
	List<VisitorVo> selectAll();

	Long selectAllCount();

	String[] interceptIds();

	Long insertOrUpdate(VisitorVo visitorVo);
}

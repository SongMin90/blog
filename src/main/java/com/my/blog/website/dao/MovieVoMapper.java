package com.my.blog.website.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MovieVoMapper {

    @Insert("insert into `t_movie`(`id`, `name`, `url`, `type`) values(#{id}, #{name}, #{url}, #{type})")
    long insert(@Param("id") String id, @Param("name") String name, @Param("url") String url, @Param("type") String type);

}

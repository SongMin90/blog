package com.my.blog.website.dao;

import com.my.blog.website.modal.Vo.MovieVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MovieVoMapper {

//    @Insert("insert into `t_movie`(`id`, `name`, `url`, `type`) values(#{id}, #{name}, #{url}, #{type})")
//    long insert(@Param("id") String id, @Param("name") String name, @Param("url") String url, @Param("type") String type);

    @Select("SELECT * FROM `t_movie` WHERE `full_name` LIKE concat('%',#{type,jdbcType=VARCHAR},'%') LIMIT #{position,jdbcType=INTEGER}, #{size,jdbcType=INTEGER}")
    List<MovieVo> get(@Param("type") String type, @Param("position") int position, @Param("size") int size);

    @Select("SELECT COUNT(1) FROM `t_movie` WHERE `full_name` LIKE concat('%',#{type,jdbcType=VARCHAR},'%')")
    int getSize(@Param("type") String type);

}

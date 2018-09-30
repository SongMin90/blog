package com.my.blog.website.dao;

import com.my.blog.website.modal.douban.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * DouBanDao
 *
 * @author SongM
 * @date 2018/9/30
 */
@Mapper
public interface DouBanDao {

    @Insert(
            "INSERT INTO `d_subject` " +
            "(`id`, `title`, `collect_count`, `original_title`, `subtype`, `year`, `alt`, `genres`, `type`) " +
            "VALUES " +
            "(#{id,jdbcType=VARCHAR}, #{title,jdbcType=VARCHAR}, #{collect_count,jdbcType=INTEGER}, #{original_title,jdbcType=VARCHAR}, #{subtype,jdbcType=VARCHAR}, #{year,jdbcType=VARCHAR}, #{alt,jdbcType=VARCHAR}, #{genres,jdbcType=VARCHAR}, #{type,jdbcType=VARCHAR}) " +
            "ON DUPLICATE KEY UPDATE `version`=`version`+1, `title`=#{title,jdbcType=VARCHAR}, `collect_count`=#{collect_count,jdbcType=INTEGER}, `original_title`=#{original_title,jdbcType=VARCHAR}, `subtype`= #{subtype,jdbcType=VARCHAR}, `year`=#{year,jdbcType=VARCHAR}, `alt`=#{alt,jdbcType=VARCHAR}, `genres`=#{genres,jdbcType=VARCHAR}, `type`=#{type,jdbcType=VARCHAR}"
    )
    int insertSubject(Subject subject);

    @Insert(
            "INSERT INTO `d_rating` " +
            "(`id`, `max`, `average`, `stars`, `min`) " +
            "VALUES " +
            "(#{id,jdbcType=VARCHAR}, #{max,jdbcType=INTEGER}, #{average,jdbcType=INTEGER}, #{stars,jdbcType=VARCHAR}, #{min,jdbcType=INTEGER}) " +
             "ON DUPLICATE KEY UPDATE `max`=#{max,jdbcType=INTEGER}, `average`=#{average,jdbcType=INTEGER}, `stars`=#{stars,jdbcType=VARCHAR}, `min`=#{min,jdbcType=INTEGER}"
    )
    int insertRating(Rating rating);

    @Insert(
            "INSERT INTO `d_image` " +
                    "(`id`, `small`, `large`, `medium`) " +
                    "VALUES " +
                    "(#{id,jdbcType=VARCHAR}, #{small,jdbcType=VARCHAR}, #{large,jdbcType=VARCHAR}, #{medium,jdbcType=VARCHAR}) " +
                    "ON DUPLICATE KEY UPDATE `small`=#{small,jdbcType=VARCHAR}, `large`=#{large,jdbcType=VARCHAR}, `medium`=#{medium,jdbcType=VARCHAR}"
    )
    int insertImage(Image image);

    @Insert(
            "INSERT INTO `d_cast` " +
                    "(`id`, `alt`, `name`, `subject_id`) " +
                    "VALUES " +
                    "(#{id,jdbcType=VARCHAR}, #{alt,jdbcType=VARCHAR}, #{name,jdbcType=VARCHAR}, #{subject_id,jdbcType=VARCHAR}) " +
                    "ON DUPLICATE KEY UPDATE `alt`=#{alt,jdbcType=VARCHAR}, `name`=#{name,jdbcType=VARCHAR}"
    )
    int insertCast(Cast cast);

    @Insert(
            "INSERT INTO `d_director` " +
                    "(`id`, `alt`, `name`, `subject_id`) " +
                    "VALUES " +
                    "(#{id,jdbcType=VARCHAR}, #{alt,jdbcType=VARCHAR}, #{name,jdbcType=VARCHAR}, #{subject_id,jdbcType=VARCHAR}) " +
                    "ON DUPLICATE KEY UPDATE `alt`=#{alt,jdbcType=VARCHAR}, `name`=#{name,jdbcType=VARCHAR}"
    )
    int insertDirector(Director director);

    @Insert(
            "INSERT INTO `d_avatar` " +
                    "(`id`, `small`, `large`, `medium`) " +
                    "VALUES " +
                    "(#{id,jdbcType=VARCHAR}, #{small,jdbcType=VARCHAR}, #{large,jdbcType=VARCHAR}, #{medium,jdbcType=VARCHAR}) " +
                    "ON DUPLICATE KEY UPDATE `small`=#{small,jdbcType=VARCHAR}, `large`=#{large,jdbcType=VARCHAR}, `medium`=#{medium,jdbcType=VARCHAR}"
    )
    int insertAvatar(Avatar avatar);

}

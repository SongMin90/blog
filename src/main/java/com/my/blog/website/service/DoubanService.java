package com.my.blog.website.service;

import com.my.blog.website.modal.douban.Subject;

import java.util.List;

/**
 * DoubanService
 *
 * @author SongM
 * @date 2018/10/2
 */
public interface DoubanService {

    List<Subject> findByType(String type);

}

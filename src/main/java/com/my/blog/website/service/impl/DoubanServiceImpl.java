package com.my.blog.website.service.impl;

import com.my.blog.website.dao.DouBanDao;
import com.my.blog.website.modal.douban.Subject;
import com.my.blog.website.service.DoubanService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * DoubanServiceImpl
 *
 * @author SongM
 * @date 2018/10/2
 */
@Service
public class DoubanServiceImpl implements DoubanService {

    @Resource
    private DouBanDao douBanDao;

    @Override
    public List<Subject> findByType(String type) {
        return douBanDao.findByType(type);
    }

}

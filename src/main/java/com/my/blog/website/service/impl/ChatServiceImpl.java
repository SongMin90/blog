package com.my.blog.website.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.dao.ChatVoMapper;
import com.my.blog.website.modal.Vo.ChatVo;
import com.my.blog.website.service.ChatService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author SongM
 * @description
 * @date 2018/4/7 16:24
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatVoMapper chatDao;

    @Override
    public PageInfo<ChatVo> getChatsWithpage(int page, int limit) {
        PageHelper.startPage(page, limit);
        List<ChatVo> chats = chatDao.findAllDESC();
        return new PageInfo<>(chats);
    }

    @Override
    public void deleteAll() {
        chatDao.deleteAll();
    }

}

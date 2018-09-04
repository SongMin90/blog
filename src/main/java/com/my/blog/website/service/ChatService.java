package com.my.blog.website.service;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.modal.Vo.ChatVo;

/**
 * @author SongM
 * @description
 * @date 2018/4/7 16:24
 */
public interface ChatService {

    PageInfo<ChatVo> getChatsWithpage(int page, int limit);

    void deleteAll();
}

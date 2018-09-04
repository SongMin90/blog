package com.my.blog.website.dao;

import com.my.blog.website.modal.Vo.ChatVo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author SongM
 * @description 聊天Mapper
 * @date 2018/4/7 14:35
 */
@Component
public interface ChatVoMapper {

    long insert(ChatVo chat);

    List<ChatVo> findAll();

    long findAllCount();

    long deleteAll();

    List<ChatVo> findAllDESC();
}

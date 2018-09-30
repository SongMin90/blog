package com.my.blog.website.modal.douban;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 主演
 *
 * @author SongM
 * @date 2018/9/30
 */
@Getter
@Setter
public class Cast implements Serializable {

    private String id;
    private String alt;
    private String name;

    private String subject_id;

    private Avatar avatars;

    public Cast(String id, String alt, String name, String subject_id) {
        this.id = id;
        this.alt = alt;
        this.name = name;
        this.subject_id = subject_id;
    }
}

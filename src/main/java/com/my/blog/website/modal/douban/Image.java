package com.my.blog.website.modal.douban;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Image
 *
 * @author SongM
 * @date 2018/9/30
 */
@Getter
@Setter
@AllArgsConstructor
public class Image implements Serializable {

    private String id;

    private String small;
    private String large;
    private String medium;

}

package com.my.blog.website.modal.douban;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 评分
 *
 * @author SongM
 * @date 2018/9/30
 */
@Getter
@Setter
@AllArgsConstructor
public class Rating implements Serializable {

    private String id;

    private int max;
    private int average;
    private String stars;
    private int min;

}

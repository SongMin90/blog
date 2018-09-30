package com.my.blog.website.modal.douban;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Movie
 *
 * @author SongM
 * @date 2018/9/30
 */
@Getter
@Setter
public class Movie implements Serializable {

    private int count;
    private int start;
    private int total;
    private String title;
    private List<Subject> subject;

}

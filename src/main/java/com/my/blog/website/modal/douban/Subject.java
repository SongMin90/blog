package com.my.blog.website.modal.douban;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Subject
 *
 * @author SongM
 * @date 2018/9/30
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subject implements Serializable {

    private String id;

    private String title;
    private int collect_count;
    private String original_title;
    private String subtype;
    private String year;
    private String alt;
    private String genres;

    private Image image;
    private Rating rating;

    /**
     * in_theaters | top250 ..
     */
    private String type;

    private List<Cast> cast;
    private List<Director> director;

    public Subject(String id, String title, int collect_count, String original_title, String subtype, String year, String alt, String genres, String type) {
        this.id = id;
        this.title = title;
        this.collect_count = collect_count;
        this.original_title = original_title;
        this.subtype = subtype;
        this.year = year;
        this.alt = alt;
        this.genres = genres;
        this.type = type;
    }

}

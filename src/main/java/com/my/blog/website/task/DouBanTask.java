package com.my.blog.website.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.my.blog.website.dao.DouBanDao;
import com.my.blog.website.modal.douban.*;
import com.my.blog.website.utils.TaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时任务：爬取豆瓣电影api
 *
 * @author SongM
 * @date 2018/9/25
 */
@Component
public class DouBanTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(DouBanTask.class);

    @Resource
    private DouBanDao douBanDao;

    /**
     * 每天12点执行一遍
     */
    //@Scheduled(fixedRate = 1000000)
    @Scheduled(cron = "0 0 12 * * ?")
    public void print() {
        LOGGER.info("----------------------- Task：DouBanTask Is Start -------------------------------");
        insertMovie("https://api.douban.com/v2/movie/in_theaters?count=100&city=深圳", "in_theaters");
        insertMovie("https://api.douban.com/v2/movie/top250?count=100&start=0", "top250");
        insertMovie("https://api.douban.com/v2/movie/top250?count=100&start=100", "top250");
        insertMovie("https://api.douban.com/v2/movie/top250?count=100&start=200", "top250");
        LOGGER.info("----------------------- Task：DouBanTask Is End -------------------------------");
    }

    /**
     * 插入电影
     * @param uri
     * @param type
     */
    private void insertMovie(String uri, String type) {
        String body = TaleUtils.httpRequest(uri);
        JSONObject movie = JSONObject.parseObject(body);
        JSONArray subjects = movie.getJSONArray("subjects");
        for (int i=0; i<subjects.size(); i++) {
            JSONObject subject = subjects.getJSONObject(i);
            String id = subject.getString("id");
            if(id != null) {
                //subject
                Subject s = new Subject(id, subject.getString("title"), subject.getInteger("collect_count"), subject.getString("original_title"), subject.getString("subtype"),
                        subject.getString("year"), subject.getString("alt"), subject.getJSONArray("genres").toJSONString(), type);
                douBanDao.insertSubject(s);

                //rating
                JSONObject r = subject.getJSONObject("rating");
                Rating rating = new Rating(id, r.getInteger("max"), r.getString("average"), r.getString("stars"), r.getInteger("min"));
                douBanDao.insertRating(rating);

                //image
                JSONObject images = subject.getJSONObject("images");
                Image image = new Image(id, images.getString("small"), images.getString("large"), images.getString("medium"));
                douBanDao.insertImage(image);

                //cast
                JSONArray casts = subject.getJSONArray("casts");
                for (int j=0; j<casts.size(); j++) {
                    insertCast(id, casts.getJSONObject(j));
                }

                //director
                JSONArray directors = subject.getJSONArray("directors");
                for (int j=0; j<directors.size(); j++) {
                    insertDirector(id, directors.getJSONObject(j));
                }
            }
        }
    }

    /**
     * 插入导演
     * @param subject_id
     * @param d
     */
    private void insertDirector(String subject_id, JSONObject d) {
        String id = d.getString("id");
        if(id != null) {
            //director
            Director director = new Director(id, d.getString("alt"), d.getString("name"), subject_id);
            douBanDao.insertDirector(director);
            //avatars
            JSONObject a = d.getJSONObject("avatars");
            Avatar avatar = new Avatar(id, a.getString("small"), a.getString("large"), a.getString("medium"));
            douBanDao.insertAvatar(avatar);
        }
    }

    /**
     * 插入主演
     * @param subject_id
     * @param c
     */
    private void insertCast(String subject_id, JSONObject c) {
        String id = c.getString("id");
        if(id != null) {
            //cast
            Cast cast = new Cast(id, c.getString("alt"), c.getString("name"), subject_id);
            douBanDao.insertCast(cast);
            //avatars
            JSONObject a = c.getJSONObject("avatars");
            Avatar avatar = new Avatar(id, a.getString("small"), a.getString("large"), a.getString("medium"));
            douBanDao.insertAvatar(avatar);
        }
    }

}

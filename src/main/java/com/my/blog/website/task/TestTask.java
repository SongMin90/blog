package com.my.blog.website.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Scheduled测试
 *
 * @author SongM
 * @date 2018/9/25
 */
@Component
public class TestTask {

    private int count = 1;

//    @Scheduled(fixedRate = 1000*2)
//    public void print() {
//        System.out.println("定时任务执行了" + (count++) + "次");
//    }

}

package com.my.blog.website.controller.douban;

import com.my.blog.website.controller.BaseController;
import com.my.blog.website.modal.douban.Subject;
import com.my.blog.website.service.DoubanService;
import com.my.blog.website.utils.MapCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * DoubanController
 *
 * @author SongM
 * @date 2018/10/2
 */
@Controller
@RequestMapping("/douban")
public class DoubanController extends BaseController {

    @Resource
    private DoubanService doubanService;

    @GetMapping("/{type}")
    public String in_theaters(@PathVariable String type, HttpServletRequest request) {
        // 从缓存取
        List<Subject> subjects = MapCache.single().get("douban_" + type);
        if (subjects == null) {
            // 从数据库取
            subjects = doubanService.findByType(type);
            // 存入缓存1天
            MapCache.single().set("douban_" + type, subjects,3600 * 24);
        }
        request.setAttribute("subjects", subjects);
        return render("douban_" + type);
    }

}

package com.my.blog.website.controller.mv;

import com.alibaba.fastjson.JSONObject;
import com.my.blog.website.controller.BaseController;
import com.mzlion.easyokhttp.HttpClient;
import com.mzlion.easyokhttp.request.GetRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MVController
 *
 * @author SongM
 * @date 2018/11/15
 */
@Controller
@RequestMapping("/mv")
public class MVController extends BaseController {

    /**
     * 列表
     * @param type
     * @param request
     * @return
     */
    @GetMapping("/{type}")
    public String getList(@PathVariable String type, HttpServletRequest request) {
        request.setAttribute("type", type);
        request.setAttribute("title", "每日电影");
        if ("tvTop10".equals(type)) {
            request.setAttribute("title", "每日热剧");
        }
        return render("mv");
    }

    /**
     * mv详情
     * @param videoInfoId
     * @param request
     * @return
     */
    @GetMapping("/mvInfo/{videoInfoId}")
    public String mvInfo(@PathVariable String videoInfoId, HttpServletRequest request) {
        request.setAttribute("videoInfoId", videoInfoId);
        request.setAttribute("title", "详情");
        return render("mv_info");
    }

    /**
     * 解析m3u8
     * @param url
     * @return
     */
    @GetMapping("/formatM3U8")
    @ResponseBody
    public Object formatM3U8(@RequestParam("url") String url, @RequestParam("host") String host, HttpServletRequest request, HttpServletResponse response) {
        if (url.contains(".m3u8")) {
            // host存入Cookie
            String hh = url.substring(0, url.lastIndexOf("/")) + "/";
            Cookie cookie = new Cookie(request.getSession().getId(), hh);
            cookie.setMaxAge(60 * 60 * 24 * 7);
            cookie.setPath("/");
            response.addCookie(cookie);
        } else {
            // 从Cookie中取出host
            Cookie[] cookies = request.getCookies();
            if (null != cookies) {
                for (Cookie cookie : cookies) {
                    if (request.getSession().getId().equals(cookie.getName())) {
                        url = url.replace(host, cookie.getValue());
                    }
                }
            }
        }
        Object responseData;
        try {
            GetRequest r = HttpClient.get(url);
            if (url.contains(".m3u8")) {
                responseData = r.asString();
            } else {
                responseData = r.asByteData();
            }
        } catch (Exception e) {
            responseData = "{\"success\":false, \"description\":\""+ e.getMessage() +"\"}";
        }
        return responseData;
    }

    /**
     * 每日电影热搜TOP10
     */
    @PostMapping("/movieTop10")
    @ResponseBody
    public Object movieTop10() {
        String responseData;
        try {
            responseData = HttpClient
                    // 请求方式和请求url
                    .get("http://api.hbzjmf.com/api/app/video/ver2/video/columnContentMore/2/7")
                    // 设置请求参数
                    .queryString("columnId","224")
                    // 设置请求头
                    .header("Content-Type","application/json")
                    .header("X-Client-NonceStr","IMZuLBet5j")
                    .header("X-Client-TimeStamp","1542015880275")
                    .header("X-Client-Version","2.1.0")
                    .header("X-Client-Sign","c0c6763bdd42c504037cdd16774ba03d6108e69a033c6b28701a85253ff09a8d")
                    .header("X-Auth-Token","mb_token:18182247:a74c60504a173d5e8d8070323aaaa37e")
                    .asString();
        } catch (Exception e) {
            responseData = "{\"success\":false, \"description\":\""+ e.getMessage() +"\"}";
        }
        return JSONObject.parseObject(responseData);
    }

    /**
     * 电影&热剧详情
     * @param videoInfoId
     * @return
     */
    @PostMapping("/mvInfo/{videoInfoId}")
    @ResponseBody
    public Object mvInfo(@PathVariable String videoInfoId) {
        System.out.println(videoInfoId);
        String responseData;
        try {
            responseData = HttpClient
                    // 请求方式和请求url
                    .get("http://api.hbzjmf.com/api/app/video/ver2/video/searchVideoInfoDetail_v2_1/2/7")
                    // 设置请求参数
                    .queryString("videoInfoId", videoInfoId)
                    // 设置请求头
                    .header("Content-Type","application/json")
                    .header("X-Client-NonceStr","WXhRUam8J4")
                    .header("X-Client-TimeStamp","1542003450801")
                    .header("X-Client-Version","2.1.0")
                    .header("X-Client-Sign","747d4b1f7c5e5ebc7bf37bda33488dd0e1faff08be7490b6b127e1ee4c5c52ce")
                    .header("X-Auth-Token","mb_token:18182247:3585e97d7cb3681b0514e99201ce0dac")
                    .asString();
        } catch (Exception e) {
            responseData = "{\"success\":false, \"description\":\""+ e.getMessage() +"\"}";
        }
        return JSONObject.parseObject(responseData);
    }

    /**
     * 每日电视剧热搜TOP10
     */
    @PostMapping("/tvTop10")
    @ResponseBody
    public Object tvTop10() {
        String responseData;
        try {
            responseData = HttpClient
                    // 请求方式和请求url
                    .get("http://api.hbzjmf.com/api/app/video/ver2/video/columnContentMore/2/7")
                    // 设置请求参数
                    .queryString("columnId","227")
                    // 设置请求头
                    .header("Content-Type","application/json")
                    .header("X-Client-NonceStr","IMZuLBet5j")
                    .header("X-Client-TimeStamp","1542015880275")
                    .header("X-Client-Version","2.1.0")
                    .header("X-Client-Sign","c0c6763bdd42c504037cdd16774ba03d6108e69a033c6b28701a85253ff09a8d")
                    .header("X-Auth-Token","mb_token:18182247:a74c60504a173d5e8d8070323aaaa37e")
                    .asString();
        } catch (Exception e) {
            responseData = "{\"success\":false, \"description\":\""+ e.getMessage() +"\"}";
        }
        return JSONObject.parseObject(responseData);
    }

}

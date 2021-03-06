package com.my.blog.website.controller.mv;

import com.alibaba.fastjson.JSONObject;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.utils.TaleUtils;
import com.mzlion.easyokhttp.HttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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
            Cookie cookie = new Cookie("songm_mv_host", hh);
            cookie.setMaxAge(60 * 60 * 24 * 7);
            cookie.setPath("/");
            response.addCookie(cookie);
        } else {
            // 从Cookie中取出host
            Cookie[] cookies = request.getCookies();
            if (null != cookies) {
                for (Cookie cookie : cookies) {
                    if ("songm_mv_host".equals(cookie.getName())) {
                        url = url.replace(host, cookie.getValue());
                    }
                }
            }
        }
        Object responseData;
        try {
            if (url.contains(".m3u8")) {
                responseData = HttpClient.get(url).asString();
            } else {
                // TODO responseData = getTS(url); 不好用，暂时舍去
                responseData = HttpClient.get(url).asByteData();
            }
        } catch (Exception e) {
            responseData = "{\"success\":false, \"description\":\""+ e.getMessage() +"\"}";
        }
        return responseData;
    }

    /**
     * 读取ts，并缓存到/upload/mv/目录
     * @param url
     * @return
     */
    private byte[] getTS(String url) {
        String filePath = TaleUtils.getUplodFilePath() + "/mvfile/";
        if (!new File(filePath).exists()) {
            new File(filePath).mkdirs();
        }
        String fileName = filePath + TaleUtils.MD5encode(url) + ".ts";
        File file = new File(fileName);

        byte[] bytes = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        FileOutputStream fos = null;

        try {
            if (file.exists()) {
                fis = new FileInputStream(file);
                bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024 * 4];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    bos.write(buffer,0, len);
                }
                bytes = bos.toByteArray();
            } else {
                bytes = HttpClient.get(url).asByteData();
                fos = new FileOutputStream(new File(fileName));
                fos.write(bytes);
            }
        } catch (Exception e) {
        } finally {
            try {if (bos != null) {bos.close();}} catch (Exception e) {}
            try {if (fis != null) {fis.close();}} catch (Exception e) {}
            try {if (fos != null) {fos.close();}} catch (Exception e) {}
        }

        return bytes;
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
                    .get("http://api.hbzjmf.com/api/app/video/ver2/video/queryVideoInfoPage/2/7")
                    // 设置请求参数
                    .queryString("flag","1")
                    .queryString("pageSize","9")
                    .queryString("currentPage","1")
                    .queryString("type","1")
                    // 设置请求头
                    .header("Content-Type","application/json")
                    .header("X-Client-NonceStr","82qqkn6RRS")
                    .header("X-Client-TimeStamp","1544444670024")
                    .header("X-Client-Version","2.3.0")
                    .header("X-Client-Sign","62983cf13f9a55081e1ec2689307c5d20289a4f0842303106544687f83e3db98")
                    .header("X-Auth-Token","mb_token:18182247:84fe024ba21c711a3d058e143bbba7aa")
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
                    .get("http://api.hbzjmf.com/api/app/video/ver2/video/queryVideoInfoPage/2/7")
                    // 设置请求参数
                    .queryString("flag","1")
                    .queryString("pageSize","9")
                    .queryString("currentPage","1")
                    .queryString("type","2")
                    // 设置请求头
                    .header("Content-Type","application/json")
                    .header("X-Client-NonceStr","82qqkn6RRS")
                    .header("X-Client-TimeStamp","1544444670024")
                    .header("X-Client-Version","2.3.0")
                    .header("X-Client-Sign","62983cf13f9a55081e1ec2689307c5d20289a4f0842303106544687f83e3db98")
                    .header("X-Auth-Token","mb_token:18182247:84fe024ba21c711a3d058e143bbba7aa")
                    .asString();
        } catch (Exception e) {
            responseData = "{\"success\":false, \"description\":\""+ e.getMessage() +"\"}";
        }
        return JSONObject.parseObject(responseData);
    }

    /**
     * 视频搜索
     * @param currentPage
     * @param pageSize
     * @param searchContent
     * @return
     */
    @PostMapping("/searchVideo/{searchContent}")
    @ResponseBody
    public Object searchVideo(@RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
                              @RequestParam(value = "pageSize", defaultValue = "50") int pageSize,
                              @PathVariable("searchContent") String searchContent) {
        String responseData;
        try {
            responseData = HttpClient
                    // 请求方式和请求url
                    .get("http://api.hbzjmf.com/api/app/video/ver2/video/searchVideoInfo/2/7")
                    // 设置请求参数
                    .queryString("currentPage", currentPage)
                    .queryString("pageSize", pageSize)
                    .queryString("searchContent", searchContent)
                    .queryString("entry", 1)
                    // 设置请求头
                    .header("Content-Type","application/json")
                    .header("X-Client-NonceStr","AtDOYHS4oe")
                    .header("X-Client-TimeStamp","1542596952314")
                    .header("X-Client-Version","2.1.0")
                    .header("X-Client-Sign","8e3d760bf27fc46f0b372dfb2cea4585267e5765ca7ee4818b9c5816583f9da6")
                    .header("X-Auth-Token","mb_token:18182247:c580defcedc7b2679e9b0552a635f2b1")
                    .asString();
        } catch (Exception e) {
            responseData = "{\"success\":false, \"description\":\""+ e.getMessage() +"\"}";
        }
        return JSONObject.parseObject(responseData);
    }

    public static void main(String[] args) {
        String responseData;
        try {
            responseData = HttpClient
                    // 请求方式和请求url
                    .get("http://api.hbzjmf.com/api/app/video/ver2/video/queryVideoInfoPage/2/7")
                    // 设置请求参数
                    .queryString("flag","1")
                    .queryString("pageSize","9")
                    .queryString("currentPage","1")
                    .queryString("type","2")
                    // 设置请求头
                    .header("Content-Type","application/json")
                    .header("X-Client-NonceStr","82qqkn6RRS")
                    .header("X-Client-TimeStamp","1544444670024")
                    .header("X-Client-Version","2.3.0")
                    .header("X-Client-Sign","62983cf13f9a55081e1ec2689307c5d20289a4f0842303106544687f83e3db98")
                    .header("X-Auth-Token","mb_token:18182247:84fe024ba21c711a3d058e143bbba7aa")
                    .asString();
        } catch (Exception e) {
            responseData = "{\"success\":false, \"description\":\""+ e.getMessage() +"\"}";
        }
        System.out.println(responseData);
    }

}

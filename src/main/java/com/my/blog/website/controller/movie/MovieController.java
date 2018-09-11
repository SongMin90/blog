package com.my.blog.website.controller.movie;

import com.alibaba.fastjson.JSONObject;
import com.my.blog.website.dao.MovieVoMapper;
import com.my.blog.website.modal.Vo.MovieVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/movie")
public class MovieController {

    private JSONObject json = new JSONObject();

    @Resource
    private MovieVoMapper movieVoMapper;

    private void getData(String u) {
        try {
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            if(code == 200) {
                InputStream in = conn.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int len = -1;
                while ((len = in.read(b)) != -1) {
                    bos.write(b, 0, len);
                }
                String body = new String(bos.toByteArray(), "UTF-8");
                json = JSONObject.parseObject(body);
                bos.close();
            }
        } catch (Exception e) {

        }
    }

    /**
     * 解密钥，失败返回true
     * @param token
     * @return
     */
    private boolean checkToken(String token) {
        if("songm_token".equals(token)) {
            return false;
        }
        return true;
    }

    /**
     * 获取电影数据
     * @param type 电影类型
     * @param pageCount 第几页
     * @return
     */
    @GetMapping(value = "/newMovies")
    public JSONObject newMovies(
            @RequestParam(name = "type", defaultValue = "") String type,
            @RequestParam(name = "pageCount", defaultValue = "1") int pageCount,
            @RequestParam(name = "pageSize", defaultValue = "15") int pageSize,
            @RequestParam(name = "token", defaultValue = "") String token
    ) {
        //清空json缓存
        json.clear();

        //验证token
        if(checkToken(token)) {
            json.put("state", 0);
            json.put("msg", "token error!");
            return json;
        }

        //获取总页数
        int allSize = movieVoMapper.getSize(type);
        int pageAllCount = (allSize / pageSize);
        if(allSize % pageSize > 0) {
            pageAllCount++;
        }
        //获取起点
        int position = (pageCount - 1) * pageSize;
        //查询数据
        List<MovieVo> movieList = movieVoMapper.get(type, position, pageSize);
        //存入json
        json.put("state", 1);
        json.put("msg", "ok");
        json.put("pageAllCount", pageAllCount);
        json.put("data", movieList);
        return json;
    }

}

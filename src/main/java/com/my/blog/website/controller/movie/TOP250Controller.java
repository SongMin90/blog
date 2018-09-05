package com.my.blog.website.controller.movie;

import com.alibaba.fastjson.JSONObject;
import com.my.blog.website.dao.MovieVoMapper;
import com.my.blog.website.utils.TaleUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
@RequestMapping("/movie")
public class TOP250Controller {

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
     * 百度位置
     * @return
     */
    @GetMapping(value = "/location")
    @ResponseBody
    public JSONObject location(String locationParam) {
        json.clear();
        getData("https://api.map.baidu.com/geocoder/v2/?ak=7VENmCeC4aaAfx3CKbSjT1K3oRucOgDK&location=" + locationParam + "1&output=json&pois=1");
        return json;
    }

    /**
     * top250
     * @return
     */
    @GetMapping(value = "/top250")
    @ResponseBody
    public JSONObject top250() {
        json.clear();
        getData("https://api.douban.com/v2/movie/top250");
        return json;
    }

    /**
     * 即将上映
     * @return
     */
    @GetMapping(value = "/coming_soon")
    @ResponseBody
    public JSONObject coming_soon() {
        json.clear();
        getData("https://api.douban.com/v2/movie/coming_soon");
        return json;
    }

    /**
     * 正在热映
     * @return
     */
    @GetMapping(value = "/in_theaters")
    @ResponseBody
    public JSONObject in_theaters() {
        json.clear();
        getData("https://api.douban.com/v2/movie/in_theaters");
        return json;
    }

    /**
     * 电影详情
     * @return
     */
    @GetMapping(value = "/subject/{id}")
    @ResponseBody
    public JSONObject in_theaters(@PathVariable String id) {
        json.clear();
        getData("https://api.douban.com/v2/movie/subject/" + id);
        return json;
    }

    /**
     * 存入电影链接
     * @return
     */
//    @GetMapping(value = "/get/{start}")
//    @ResponseBody
//    public String get(@PathVariable int start) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for(int i=start;i<=177;i++){
//                    try {
//                        getUrl("http://www.ygdy8.net/html/gndy/dyzz/list_23_"+i+".html");
//                    } catch (Exception e) {
//                        new File("C:\\Users\\10444\\Desktop\\aa\\" + i).mkdir();
//                    }
//                }
//            }
//        }).start();
//
//        return "ok";
//    }


    public void  getUrl(String uri) throws Exception {
        HttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri);
        HttpResponse response = client.execute(get);
        String result = EntityUtils.toString(response.getEntity(),"gb2312");
        Document doc= Jsoup.parse(result);
        //css选择器
        Elements elements=  doc.select("table.tbspan ");
        for (Element element : elements) {
            element.setBaseUri("http://www.ygdy8.net");
            //getDownloadUrl(element.select("tr").get(1).select("a").text(), element.select("tr").get(1).select("a").attr("abs:href"));
            String name = element.select("tr").get(1).select("a").text();
            get = new HttpGet(element.select("tr").get(1).select("a").attr("abs:href"));
            response = client.execute(get);
            result = EntityUtils.toString(response.getEntity(),"gb2312");
            doc = Jsoup.parse(result);
            elements = doc.select("div#Zoom table tr td a ");
            String url = "";
            if(elements.get(0).text().equals("磁力链下载点击这里")) {
                url = elements.get(1).text();
            } else {
                url = elements.get(0).text();
            }
            try {
                movieVoMapper.insert(TaleUtils.MD5encode(name + url), name, url, "最新电影");
            }catch (Exception e) {
                System.out.println("已经存在：" + name);
            }
            Thread.sleep(1000);
        }
    }

}

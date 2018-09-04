package com.my.blog.website.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Dyttdownload {

    static int id=1;
    public static HttpClient client=null;

    public static void main(String[] args) {
        //ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        Map<Integer,String> map=new HashMap<>();
        for(int i=1;i<50;i++){
        //    http://www.ygdy8.net/html/gndy/dyzz/list_23_2.html
            map.put(i,"http://www.ygdy8.net/html/gndy/dyzz/list_23_"+i+".html" );
        }
        for (String string : map.values()) {
            getUrl(string);
        //  Thread.currentThread().sleep(2000);

        }

        //  getDownloadUrl("http://www.ygdy8.net/html/gndy/dyzz/20170926/55094.html");
    }

    public static void  getUrl(String uri){
        //JDBCUtils utils=new JDBCUtils();

        try {
            client=HttpClients.createDefault();
            //  RequestConfig config=RequestConfig.custom().setProxy(new HttpHost("110.73.14.161",8123)).build();
            HttpGet get=new HttpGet(uri);
            //  get.setConfig(config);
            HttpResponse response=client.execute(get);
            String result =EntityUtils.toString(response.getEntity(),"gb2312");
            Document doc=Jsoup.parse(result);
            //css选择器
            Elements elements=  doc.select("table.tbspan ");
            for (Element element : elements) {
                element.setBaseUri("http://www.ygdy8.net");
                getDownloadUrl(element.select("tr").get(1).select("a").text(),element.select("tr").get(1).select("a").attr("abs:href"));
                //DyUrl dy = getDownloadUrl(element.select("tr").get(1).select("a").text(),element.select("tr").get(1).select("a").attr("abs:href"));
                //dy.setId(id);
                //utils.insert(dy);
                //id++;
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static DyUrl  getDownloadUrl(String name,String dyurl){
        //DyUrl dy=new DyUrl();
        //  RequestConfig config=RequestConfig.custom().setProxy(new HttpHost("110.73.14.161",8123)).build();
        try {
            client=HttpClients.createDefault();
            HttpGet get =new HttpGet(dyurl);
            //      get.setConfig(config);
            HttpResponse response=client.execute(get);
            String result =EntityUtils.toString(response.getEntity(),"gb2312");
            Document doc=Jsoup.parse(result);
            Elements elements=doc.select("div#Zoom table tr td a ");
            //dy.setDyname(name);
            String url = "";
            if(elements.get(0).text().equals("磁力链下载点击这里")) {
                //dy.setDyUrl(elements.get(1).text());
                url = elements.get(1).text();
            } else {
                //dy.setDyUrl(elements.get(0).text());
                url = elements.get(0).text();
            }

            //System.out.println(dy.getDyname() + "|" + dy.getDyUrl());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}

class DyUrl {
    private int id;
    private String dyname;
    private String dyUrl;

    public String getDyname() {
        return dyname;
    }

    public void setDyname(String dyname) {
        this.dyname = dyname;
    }

    public String getDyUrl() {
        return dyUrl;
    }

    public void setDyUrl(String dyUrl) {
        this.dyUrl = dyUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
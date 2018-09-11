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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SongM on 2018/9/6
 */
public class GetMovieTest {

    private static ConcurrentHashMap<Object, Object> movieItems = new ConcurrentHashMap<>();

    public static void main(String[] args) throws UnsupportedEncodingException {
        String url1 = "http://s.ygdy8.com/plus/so.php?pagesize=999999&keyword=";
        String url2 = "http://www.ygdy8.com/html/gndy/dyzz/20171002/55168.html";

        //获取电影Items
        Scanner scanner = new Scanner(System.in);
        System.out.print("关键词：");
        url1 = url1 + URLEncoder.encode(scanner.next(), "GBK");
        getMovieItems(url1);

        //获取下载链接
        //getMovieDownloadUrl(url2);
    }

    private static void getMovieDownloadUrl(String url2) {
        HttpClient client = null;
        try {
            client = HttpClients.createDefault();
            HttpGet get = new HttpGet(url2);
            HttpResponse response = client.execute(get);
            String result = EntityUtils.toString(response.getEntity(),"gb2312");
            Document doc = Jsoup.parse(result);
            Elements elements = doc.select("div#Zoom table tr td a ");
            String url = "";
            if(elements.get(0).text().equals("磁力链下载点击这里")) {
                url = elements.get(1).text();
            } else {
                url = elements.get(0).text();
             }
             System.out.println(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getMovieItems(String url1) {
        HttpClient client = null;
        try {
            client = HttpClients.createDefault();
            HttpGet get = new HttpGet(url1);
            HttpResponse response = client.execute(get);
            String result = EntityUtils.toString(response.getEntity(),"gb2312");
            Document doc = Jsoup.parse(result);
            //css选择器
            Elements elements =  doc.select("div.co_content8 ul table");
            for (Element element: elements) {
                element.setBaseUri("http://www.ygdy8.com");
                String name = element.select("a").text();
                String url = element.select("a").attr("abs:href");
                System.out.print(name + "|" + url + "|");
                getMovieDownloadUrl(url);
                //movieItems.put(name, url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

/**
 * Created by SongM on 2018/9/5
 */
public class WebsiteTest {
    
    public static void main(String[] args) {
        for (int i=0; i<10000; i++) {
            new WebsiteTest().压力测试();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void 压力测试() {
        Thread2 t = new Thread2();
        for (int i=0; i<1000; i++) {
            new Thread(t).start();
        }
    }
    
}

class Thread1 implements Runnable {

    volatile int count = 0;

    final String URL = "http://119.29.19.127";

    @Override
    public void run() {
        try {
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1000 * 10);
            long l = System.currentTimeMillis();
            conn.connect();
            int code = conn.getResponseCode();
            if(code == 200) {
                count++;
                System.out.println(count + "连接成功" + " | " + (System.currentTimeMillis() - l));
                conn.disconnect();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
    
}

class Thread2 implements Runnable {

    final String URL = "http://localhost:8080/sendMsg";

    private volatile int count = 1;

    @Override
    public void run() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(URL);
            conn = (HttpURLConnection) url.openConnection();
            // http正文内，因此需要设为true
            conn.setDoOutput(true);
            // Read from the connection. Default is true.
            conn.setDoInput(true);
            // 默认是 GET方式
            conn.setRequestMethod("POST");
            // Post 请求不能使用缓存
            conn.setUseCaches(false);
            //设置本次连接是否自动重定向
            conn.setInstanceFollowRedirects(true);
            // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
            // 意思是正文是urlencoded编码过的form参数
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
            // 要注意的是connection.getOutputStream会隐含的进行connect。
            conn.connect();
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            // 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
            String content = "msg=" + URLEncoder.encode("你好啊 " + count++, "utf-8");
            // DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
            out.writeBytes(content);
            //流用完记得关
            out.flush();
            out.close();
            //获取响应
            conn.getResponseCode();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

}

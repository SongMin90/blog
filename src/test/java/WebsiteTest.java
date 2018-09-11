import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SongM on 2018/9/5
 */
public class WebsiteTest {
    
    public static void main(String[] args) {
        new WebsiteTest().压力测试();
    }
    
    public void 压力测试() {
        Thread1 t = new Thread1();
        for(int i=0; i<99999; i++) {
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

import java.util.Enumeration;
import java.util.Hashtable;

public class MyThread implements Runnable {

    private volatile boolean temp;

    long count = 100;

    private Hashtable ht = new Hashtable<String, Long>();

    public void stop() {
        temp = false;
    }

    public void start() {
        if(!temp) temp = !temp;
    }

    public void query() {
        System.out.println();
        System.out.println("------------抢票结果-------------");
        System.out.println();
        Enumeration e = ht.keys();
        while(e.hasMoreElements()) {
            Object o = e.nextElement();
            System.out.println(o + "：" + ht.get(o) + "张");
        }
    }

    @Override
    public void run() {
        while(temp && count > 0) {
            try {
                count--;
                Thread t = Thread.currentThread();
                String tName = t.getName();
                if(ht.get(tName) == null) {
                    ht.put(tName, 1);
                } else {
                    ht.put(tName, Long.valueOf(String.valueOf(ht.get(tName))) + 1);
                }
                System.out.println(t.getName() + "：抢到" + ht.get(tName) + "张，剩余：" + count + "张");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

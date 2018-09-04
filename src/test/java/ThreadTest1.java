import java.util.Scanner;

public class ThreadTest1 {

    private static volatile boolean temp = true;

    public static void main(String[] args) throws InterruptedException {
        MyThread myThread = new MyThread();
        Scanner scanner = new Scanner(System.in);
        while(temp) {
            int i = scanner.nextInt();
            switch (i) {
                case 1 :
                    myThread.start();
                    Thread t = new Thread(myThread);
                    t.start();
                    System.out.println(t.getName() + "开始抢票。");
                    Thread t1 = new Thread(myThread);
                    t1.start();
                    System.out.println(t1.getName() + "开始抢票。");
                    Thread.sleep(3000);
                    Thread t2 = new Thread(myThread);
                    t2.start();
                    System.out.println(t2.getName() + "开始抢票。");
                    break;
                case 2 :
                    myThread.stop();
                    System.out.println("暂停。");
                    break;
                case 3 :
                    myThread.query();
                    break;
                case 0 :
                    temp = false;
                    break;
            }
        }
    }

}
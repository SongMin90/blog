public class ThreadTest2 {

    public static void main(String[] args) {
        abc();
        System.out.println(count);
    }

    private static int count = 100;

    public static void abc() {
        if(count > 0) {
            count--;
            abc();
        }
    }

}

package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class UI implements KeyListener {

    private JFrame jFrame;
    private JButton jButton;

    public static void main(String[] args) {
        UI ui = new UI();
        ui.jFrame = new JFrame();

        ui.jFrame.setTitle("game");
        ui.jFrame.setSize(600,600);
        ui.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.jFrame.setVisible(true);

        ui.jButton = new JButton();
        ui.jButton.setText("O~O");
        ui.jButton.setSize(60,60);
        ui.jFrame.add(ui.jButton);

        // 键盘监听
        ui.jFrame.addKeyListener(ui);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Point point = jButton.getLocation();
        int x = point.x;
        int y = point.y;
        int i = 10;

        System.out.println("x:"+x+" y:"+y);

        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP:
                y -= i;
                if(y < 0) {
                    y += i;
                }
                break;
            case KeyEvent.VK_DOWN:
                y += i;
                if(y > 500) {
                    y -= i;
                }
                break;
            case KeyEvent.VK_LEFT:
                x -= i;
                if(x < 0) {
                    x += i;
                }
                break;
            case KeyEvent.VK_RIGHT:
                x += i;
                if(x > 520) {
                    x -= i;
                }
                break;
        }

        jButton.setLocation(x, y);
        jFrame.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

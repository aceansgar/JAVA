package src;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

public class Welcome implements ActionListener {
    JFrame jframe;
    JPanel jpanel;

    public void actionPerformed(ActionEvent e) {
        String action_command = e.getActionCommand();
        if (action_command == "start") {
            jframe.remove(jpanel);
            Account account = new Account(jframe);
        } else if (action_command == "exit") {
            System.exit(0);
        } else if (action_command == "Calculator") {
            Calculator calculator = new Calculator();
        }
    }

    public static int width = 800, height = 600;

    Welcome() {
        // ---------------init--------------
        jframe = new JFrame("Settle Account");
        jpanel = new JPanel();

        // --------------add text --------------
        JLabel title = new JLabel("欢迎使用自助结账！");
        title.setForeground(Color.black);
        title.setBounds(50, 100, 500, 50);
        title.setFont(new Font("黑体", Font.PLAIN, 36));
        jpanel.add(title);

        // --------------add button------------
        int but_w = 100, but_h = 50;
        JButton b_go = new JButton("开始使用");
        b_go.setFont(new Font("宋体", Font.BOLD, 14));
        b_go.setBounds(475, height * 3 / 4 - but_h, 100, 50);
        b_go.addActionListener(this);
        b_go.setActionCommand("start");
        jpanel.add(b_go);

        JButton b_quit = new JButton("退出");
        b_quit.setFont(new Font("宋体", Font.BOLD, 16));
        b_quit.setBounds(600, height * 3 / 4 - but_h, 100, 50);
        b_quit.addActionListener(this);
        b_quit.setActionCommand("exit");
        jpanel.add(b_quit);
        // -----------add background------------
        ImageIcon imgicon_1 = new ImageIcon("imgs/bg.jpg");
        JLabel label_1 = new JLabel(imgicon_1);
        jpanel.add(label_1, new Integer(Integer.MIN_VALUE));
        label_1.setBounds(width / 2, 0, width / 2, height / 2);
        ImageIcon imgicon_2 = new ImageIcon("imgs/bg2.jpg");
        JLabel label_2 = new JLabel(imgicon_2);
        jpanel.add(label_2, new Integer(Integer.MIN_VALUE));
        label_2.setBounds(0, height / 2, width / 2, height / 2);

        JMenuBar menubar = new JMenuBar();
        jframe.setJMenuBar(menubar);

        JMenu menu_tools = new JMenu("Tools");
        JMenuItem menuitem_calc = new JMenuItem("Calculator");
        menu_tools.add(menuitem_calc);
        menuitem_calc.addActionListener(this);
        menuitem_calc.setActionCommand("Calculator");

        menubar.add(menu_tools);

        JMenu menu_help = new JMenu("Help");
        menubar.add(menu_help);

        jpanel.setOpaque(false);
        jpanel.setSize(width, height);
        jpanel.setLocation(0, 0);
        jpanel.setLayout(null);

        jframe.setSize(width, height);
        jframe.setLocation(0, 0);
        jframe.setLayout(null);

        jframe.add(jpanel);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setVisible(true);
    }

    public static void main(String[] args) {
        new Welcome();
    }

}

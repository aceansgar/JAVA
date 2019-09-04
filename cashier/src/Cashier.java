package src;

import java.awt.Font;
import javax.swing.*;

class Cashier {
	JFrame jframe;
	JPanel jpanel;

	Cashier() {
		JFrame jf = new JFrame();
		new Cashier(jf, 0);
	}

	Cashier(JFrame _jframe, int _total_price) {
		jframe = _jframe;

		int int_price = _total_price;
		String qr_path = "imgs/qr_code.jpg";
		String str_cashier = "<html><body>";
		str_cashier += "请确认支付";
		str_cashier += String.valueOf(int_price) + "元";
		str_cashier += "</body></html>";

		ImageIcon imageicon = new ImageIcon(qr_path);
		JLabel label_img = new JLabel(imageicon);

		Font font = new Font("Serief", Font.PLAIN + Font.BOLD, 30);

		JLabel label_cashier = new JLabel(str_cashier);
		label_cashier.setFont(font);

		label_img.setBounds(400, 100, 300, 300);
		label_cashier.setBounds(50, 200, 250, 100);

		jpanel = new JPanel();
		jpanel.add(label_cashier);
		jpanel.add(label_img);
		jpanel.setLayout(null);

		jpanel.setSize(800, 600);
		jpanel.setLocation(0, 0);

		jframe.add(jpanel);
		jframe.setTitle("Cashier");
		jframe.setSize(800, 600);
		jframe.setLocation(0, 0);
		jframe.setVisible(true);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SwingUtilities.updateComponentTreeUI(jframe);

	}
	// public static void main(String[] args)
	// {
	//
	// }
}

package src;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public class Account implements ActionListener {
	public static int width = 800, height = 600, num_plate = 7, plate_w = 100, plate_h = 100;
	public static String[] colors = { "blue", "green", "purple", "yellow", "orange", "red", "coffee" };
	public static String[] colors_CN = { "蓝色", "绿色", "紫色", "黄色", "橙色", "红色", "咖啡" };
	public static int[] prices = { 8, 10, 12, 16, 18, 20, 24 };
	JFrame f;
	JPanel jp;

	public void actionPerformed(ActionEvent e) {
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setFileFilter(new FileFilter() {
			public String getDescription() {
				return ".jpg,.png";
			}

			public boolean accept(File f) {
				boolean end_jpg = f.getName().toLowerCase().endsWith(".jpg");
				boolean end_png = f.getName().toLowerCase().endsWith(".png");
				return f.isDirectory() || end_jpg || end_png;
			}
		});

		String action_command = e.getActionCommand();
		if (action_command == "exit") {
			System.exit(0);
		} else if (action_command == "select_file")
			;
		{
			int returnVal = fc.showOpenDialog(f);
			File file = fc.getSelectedFile();
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String file_path = file.getAbsolutePath();
				f.remove(jp);
				Recognize recognize = new Recognize(f, file_path);
			} else {

			}

		}

	}

	Account(JFrame fff) {

		f = fff;
		// -------------init------------
		jp = new JPanel();
		jp.setSize(width, height);
		jp.setLocation(0, 0);
		jp.setLayout(null);
		jp.setOpaque(false);
		f.add(jp);

		// -------------add prices--------------
		JLabel[] lab_pr = new JLabel[num_plate];
		for (int i = 0; i < 4; i++) {
			lab_pr[i] = new JLabel(colors_CN[i] + ":" + prices[i] + "元");
			lab_pr[i].setForeground(Color.black);
			lab_pr[i].setBounds(width / 2 + (i - 2) * (plate_w * 3 / 2) + 15, plate_h + 15, plate_w, plate_h);
			lab_pr[i].setFont(new Font("黑体", Font.PLAIN, 18));
			jp.add(lab_pr[i]);
		}
		for (int i = 4; i < 7; i++) {
			lab_pr[i] = new JLabel(colors_CN[i] + ":" + prices[i] + "元");
			lab_pr[i].setForeground(Color.black);
			lab_pr[i].setBounds(width / 2 - plate_w / 2 + (i - 5) * (plate_w * 3 / 2) + 15, 3 * plate_h + 15, plate_w,
					plate_h);
			lab_pr[i].setFont(new Font("黑体", Font.PLAIN, 18));
			jp.add(lab_pr[i]);
		}
		jp.repaint();
		// --------------add plates-------------
		ImageIcon[] im_p = new ImageIcon[num_plate];
		JLabel[] lab_p = new JLabel[num_plate];
		for (int i = 0; i < 4; i++) {
			im_p[i] = new ImageIcon("imgs/" + colors[i] + ".jpg");
			lab_p[i] = new JLabel(im_p[i]);
			jp.add(lab_p[i], new Integer(Integer.MIN_VALUE));
			lab_p[i].setBounds(width / 2 + (i - 2) * (plate_w * 3 / 2), plate_h / 2, plate_w, plate_h);
		}
		for (int i = 4; i < 7; i++) {
			im_p[i] = new ImageIcon("imgs/" + colors[i] + ".jpg");
			lab_p[i] = new JLabel(im_p[i]);
			jp.add(lab_p[i], new Integer(Integer.MIN_VALUE));
			lab_p[i].setBounds(width / 2 - plate_w / 2 + (i - 5) * (plate_w * 3 / 2), 5 * plate_h / 2, plate_w,
					plate_h);
		}
		// ---------------add buttons---------------
		JButton b_quit = new JButton("退出");
		b_quit.setFont(new Font("宋体", Font.BOLD, 16));
		b_quit.setBounds(600, height * 3 / 4, 100, 50);
		b_quit.addActionListener(this);
		b_quit.setActionCommand("exit");
		jp.add(b_quit);
		JButton b_open = new JButton("选择图片");
		b_open.setFont(new Font("宋体", Font.BOLD, 14));
		b_open.setBounds(475, height * 3 / 4, 100, 50);
		b_open.addActionListener(this);
		b_open.setActionCommand("select_file");
		jp.add(b_open);
	}

	public static void main(String[] args) {

	}
}

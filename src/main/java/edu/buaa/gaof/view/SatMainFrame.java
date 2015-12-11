package edu.buaa.gaof.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.jfree.ui.RefineryUtilities;

public class SatMainFrame extends JFrame {

	JMenuBar menubar = new JMenuBar();
	JMenu LoginMenu = new JMenu("系统登录");
	JMenu UserMangeMenu = new JMenu("用户管理");
	JMenu SchoolMangeMenu = new JMenu("学籍管理");
	JMenu HelpMenu = new JMenu("关于");

	public SatMainFrame() {
		setTitle("SAT_LA_CMP静态错误定位系统");
		setSize(800, 600);
		getContentPane().setLayout(new BorderLayout(5, 10));

		// 菜单项
		JMenuItem userLoginMenu = new JMenuItem("用户登录");
		// userLoginMenu.addActionListener(new LoginActionListener());
		JMenuItem exitLoginMenu = new JMenuItem("退出");
		LoginMenu.add(userLoginMenu);
		LoginMenu.add(exitLoginMenu);
		menubar.add(LoginMenu);
		menubar.add(UserMangeMenu);
		menubar.add(SchoolMangeMenu);
		menubar.add(HelpMenu);
		getContentPane().add(menubar, BorderLayout.NORTH);

		// 文本区，显示源代码
		JTextArea srcArea = new JTextArea();
		srcArea.setLineWrap(true);// 设置自动换行
		JScrollPane jsp = new JScrollPane(srcArea);// 给文本区添加滚动条
		Integer[] lines = new Integer[4000];
		for (int i = 0; i < 4000; i++)
			lines[i] = i + 1;
		JList<Integer> lineList = new JList<Integer>(lines);
		lineList.setBounds(0, 5, 30, -1);
		lineList.setFixedCellHeight(18);
		lineList.setBackground(Color.LIGHT_GRAY);
		// lineList.setSelectedIndex(3);
		jsp.setRowHeaderView(lineList);
		// jsp.setColumnHeaderView(new JList<Integer>(new Integer[] { 1, 2, 3
		// }));
		getContentPane().add(jsp, BorderLayout.CENTER);

	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		SatMainFrame frame = new SatMainFrame();
//		frame.pack();
		frame.setResizable(false);
		// frame.setResizable(false);
		RefineryUtilities.centerFrameOnScreen(frame);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
}

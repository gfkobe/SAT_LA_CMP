package edu.buaa.gaof.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.jfree.ui.RefineryUtilities;

public class SatMainFrame extends JFrame {

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
		setJMenuBar(menubar);
		// getContentPane().add(menubar, BorderLayout.NORTH);

		// 上侧区域（操作按钮）
		JPanel northPanel = new JPanel(new GridBagLayout());
		srcButton = new JButton("选择源代码");
		specButton = new JButton("选择规则文件");
		startButton = new JButton("开始分析");
		
		northPanel.add(srcButton, new GBC(0, 0));
		northPanel.add(specButton, new GBC(1, 0));
		northPanel.add(startButton, new GBC(2, 0));
		getContentPane().add(northPanel, BorderLayout.NORTH);

		// 左侧区域（配置项，规则文件）
		JPanel westPanel = new JPanel(new BorderLayout());
		// 配置项
		JPanel configPanel = new JPanel(new GridBagLayout());
		configPanel.setBorder(BorderFactory.createTitledBorder("配置项"));
		JLabel timeLimitLabel = new JLabel("时间限制：");
		configPanel.add(timeLimitLabel, new GBC(0, 0).setInset(4));
		JTextField timeLimitField = new JTextField(6);
		configPanel.add(timeLimitField, new GBC(1, 0));
		JLabel secLabel = new JLabel(" 秒");
		configPanel.add(secLabel, new GBC(2, 0));
		westPanel.add(configPanel, BorderLayout.NORTH);
		// 规则文件
		JPanel specPanel = new JPanel(new BorderLayout());
		specPanel.setBorder(BorderFactory.createTitledBorder("规则文件"));
		specArea = new JTextArea();
		JScrollPane specJsp = new JScrollPane(specArea);// 给文本区添加滚动条
		specPanel.add(specJsp, BorderLayout.CENTER);
		westPanel.add(specPanel, BorderLayout.CENTER);

		getContentPane().add(westPanel, BorderLayout.WEST);

		// 中间区域，文本区，显示源代码
		JPanel centerPanel = new JPanel(new BorderLayout());
		// 行号
		final JTextArea lineTextArea = new JTextArea();// 行号区域是个JTextArea
		lineTextArea.setBackground(Color.LIGHT_GRAY);
		StringBuilder lineText = new StringBuilder();
		String lineSep = System.getProperty("line.separator");
		for (int i = 0; i < 4000; i++) {
			lineText.append(i + 1).append(lineSep);
		}
		lineTextArea.setEditable(false);
		lineTextArea.setEnabled(false);
		lineTextArea.setText(lineText.toString());
		// textPanel.add(lineTextArea, BorderLayout.WEST);
		// 文本编辑区
		srcArea = new JTextArea();
		srcArea.setTabSize(4);
		// srcArea.setLineWrap(true);// 设置自动换行
		JScrollPane jsp = new JScrollPane(srcArea);// 给文本区添加滚动条
		// 行号显示在左侧
		jsp.setRowHeaderView(lineTextArea);
		centerPanel.add(jsp, BorderLayout.CENTER);
		getContentPane().add(centerPanel, BorderLayout.CENTER);

		// // 右侧区域（结果查看操作）
		// JPanel eastPanel = new JPanel(new GridBagLayout());
		// cfaButton = new JButton("查看CFA");
		// argButton = new JButton("查看ARG");
		//
		// eastPanel.add(cfaButton, new GBC(0, 0));
		// eastPanel.add(argButton, new GBC(0, 1));
		// getContentPane().add(eastPanel, BorderLayout.EAST);

		// 下侧区域（输出窗口）
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.setBorder(BorderFactory.createTitledBorder("结果输出"));
		// 结果查看操作
		JPanel resultPanel = new JPanel(new GridBagLayout());
		cfaButton = new JButton("查看CFA");
		argButton = new JButton("查看ARG");
		resultPanel.add(cfaButton, new GBC(0, 0));
		resultPanel.add(argButton, new GBC(1, 0));
		southPanel.add(resultPanel, BorderLayout.NORTH);
		// 控制台输出
		outputArea = new JTextArea(5, 1);// 设置为5行
		JScrollPane outJsp = new JScrollPane(outputArea);// 给文本区添加滚动条
		southPanel.add(outJsp, BorderLayout.CENTER);
		
		getContentPane().add(southPanel, BorderLayout.SOUTH);
		
		// 添加监听器
		satActionListener = new SatActionListener(this);
		srcButton.addActionListener(satActionListener);
		specButton.addActionListener(satActionListener);
		startButton.addActionListener(satActionListener);
		cfaButton.addActionListener(satActionListener);
		argButton.addActionListener(satActionListener);

	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		SatMainFrame frame = new SatMainFrame();
		// frame.pack();
		// frame.setResizable(false);
		RefineryUtilities.centerFrameOnScreen(frame);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}

	private JMenuBar menubar = new JMenuBar();
	private JMenu LoginMenu = new JMenu("系统登录");
	private JMenu UserMangeMenu = new JMenu("用户管理");
	private JMenu SchoolMangeMenu = new JMenu("学籍管理");
	private JMenu HelpMenu = new JMenu("关于");

	private JButton srcButton;// 选择源代码
	private JButton specButton;// 选择规则文件
	private JButton startButton;// 开始分析

	private JButton cfaButton;// 查看CFA
	private JButton argButton;// 查看ARG

	private JTextArea specArea;
	private JTextArea srcArea;
	private JTextArea outputArea;

	private SatActionListener satActionListener;

	public JMenuBar getMenubar() {
		return menubar;
	}

	public void setMenubar(JMenuBar menubar) {
		this.menubar = menubar;
	}

	public JMenu getLoginMenu() {
		return LoginMenu;
	}

	public void setLoginMenu(JMenu loginMenu) {
		LoginMenu = loginMenu;
	}

	public JMenu getUserMangeMenu() {
		return UserMangeMenu;
	}

	public void setUserMangeMenu(JMenu userMangeMenu) {
		UserMangeMenu = userMangeMenu;
	}

	public JMenu getSchoolMangeMenu() {
		return SchoolMangeMenu;
	}

	public void setSchoolMangeMenu(JMenu schoolMangeMenu) {
		SchoolMangeMenu = schoolMangeMenu;
	}

	public JMenu getHelpMenu() {
		return HelpMenu;
	}

	public void setHelpMenu(JMenu helpMenu) {
		HelpMenu = helpMenu;
	}

	public JButton getSrcButton() {
		return srcButton;
	}

	public void setSrcButton(JButton srcButton) {
		this.srcButton = srcButton;
	}

	public JButton getSpecButton() {
		return specButton;
	}

	public void setSpecButton(JButton specButton) {
		this.specButton = specButton;
	}

	public JButton getStartButton() {
		return startButton;
	}

	public void setStartButton(JButton startButton) {
		this.startButton = startButton;
	}

	public JButton getCfaButton() {
		return cfaButton;
	}

	public void setCfaButton(JButton cfaButton) {
		this.cfaButton = cfaButton;
	}

	public JButton getArgButton() {
		return argButton;
	}

	public void setArgButton(JButton argButton) {
		this.argButton = argButton;
	}

	public JTextArea getSpecArea() {
		return specArea;
	}

	public void setSpecArea(JTextArea specArea) {
		this.specArea = specArea;
	}

	public JTextArea getSrcArea() {
		return srcArea;
	}

	public void setSrcArea(JTextArea srcArea) {
		this.srcArea = srcArea;
	}

	public JTextArea getOutputArea() {
		return outputArea;
	}

	public void setOutputArea(JTextArea outputArea) {
		this.outputArea = outputArea;
	}

	public SatActionListener getSatActionListener() {
		return satActionListener;
	}

	public void setSatActionListener(SatActionListener satActionListener) {
		this.satActionListener = satActionListener;
	}

}

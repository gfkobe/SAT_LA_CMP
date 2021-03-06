package edu.buaa.satla.view;

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
		JMenuItem srcMenuItem = new JMenuItem("选择源代码");
		// userLoginMenu.addActionListener(new LoginActionListener());
		JMenuItem specMenuItem = new JMenuItem("选择规则文件");
		JMenuItem exitMenuItem = new JMenuItem("退出");
		fileMenu.add(srcMenuItem);
		fileMenu.add(specMenuItem);
		fileMenu.add(exitMenuItem);
		JMenuItem analysisMenuItem = new JMenuItem("开始分析");
		analysisMenu.add(analysisMenuItem);
		// menubar.add(SchoolMangeMenu);
		JMenuItem helpMenuItem = new JMenuItem("关于");
		helpMenu.add(helpMenuItem);

		menubar.add(fileMenu);
		menubar.add(analysisMenu);
		menubar.add(helpMenu);
		setJMenuBar(menubar);
		// getContentPane().add(menubar, BorderLayout.NORTH);

		// 上侧区域（操作按钮）
		JPanel northPanel = new JPanel(new GridBagLayout());
		srcButton = new JButton("选择源文件");
		specButton = new JButton("选择规则文件");
		startButton = new JButton("开始分析");
		
		northPanel.add(srcButton, new GBC(0, 0).setInset(3));
		northPanel.add(specButton, new GBC(1, 0).setInset(3));
		northPanel.add(startButton, new GBC(2, 0).setInset(3));
		getContentPane().add(northPanel, BorderLayout.NORTH);

		// 左侧区域（配置项，规则文件）
		JPanel westPanel = new JPanel(new BorderLayout());
		// 基本配置项
		JPanel basicConfigPanel = new JPanel(new GridBagLayout());
		basicConfigPanel.setBorder(BorderFactory.createTitledBorder("基本配置"));
		JLabel timeLimitLabel = new JLabel("时间限制：");
		basicConfigPanel.add(timeLimitLabel, new GBC(0, 0).setInset(3));
		timeLimitField = new JTextField(6);
		timeLimitField.setText("600");
		basicConfigPanel.add(timeLimitField, new GBC(1, 0).setInset(3));
		JLabel secLabel = new JLabel("秒");
		basicConfigPanel.add(secLabel, new GBC(2, 0).setInset(3));
		JLabel specLabel = new JLabel("规则文件：");
		basicConfigPanel.add(specLabel, new GBC(0, 1));
		specFileLabel = new JLabel("未选择");
		specFileLabel.setForeground(Color.RED);
		basicConfigPanel.add(specFileLabel, new GBC(1, 1));

		westPanel.add(basicConfigPanel, BorderLayout.NORTH);
		// 谓词相关配置
		JPanel predConfigPanel = new JPanel(new GridBagLayout());
		predConfigPanel.setBorder(BorderFactory.createTitledBorder("谓词配置"));
		JLabel predUpLimitLabel = new JLabel("谓词集合大小上限：");
		predConfigPanel.add(predUpLimitLabel, new GBC(0, 0).setInset(3));
		predUpLimitField = new JTextField(4);
		predUpLimitField.setText("50");// 默认50
		predConfigPanel.add(predUpLimitField, new GBC(1, 0).setInset(3));

		JLabel predFreUpLimitLabel = new JLabel("谓词频度上限：");
		predConfigPanel.add(predFreUpLimitLabel, new GBC(0, 1).setInset(3));
		predFreUpLimitField = new JTextField(4);
		predFreUpLimitField.setText("20");
		predConfigPanel.add(predFreUpLimitField, new GBC(1, 1).setInset(3));
		JLabel predFreDownLimitLabel = new JLabel("谓词频度下限：");
		predConfigPanel.add(predFreDownLimitLabel, new GBC(0, 2).setInset(3));
		predFreDownLimitField = new JTextField(4);
		predFreDownLimitField.setText("5");
		predConfigPanel.add(predFreDownLimitField, new GBC(1, 2).setInset(3));
		JLabel predRedUpLimitLabel = new JLabel("谓词冗余度上限：");
		predConfigPanel.add(predRedUpLimitLabel, new GBC(0, 3).setInset(3));
		predRedUpLimitField = new JTextField(4);
		predRedUpLimitField.setText("2");
		predConfigPanel.add(predRedUpLimitField, new GBC(1, 3).setInset(3));

		westPanel.add(predConfigPanel, BorderLayout.SOUTH);

		// JPanel specPanel = new JPanel(new BorderLayout());
		// specPanel.setBorder(BorderFactory.createTitledBorder("谓词配置"));
		// specArea = new JTextArea();
		// JScrollPane specJsp = new JScrollPane(specArea);// 给文本区添加滚动条
		// specPanel.add(specJsp, BorderLayout.CENTER);

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
		// cfaButton = new JButton("查看CFA");
		// argButton = new JButton("查看ARG");
		// openButton = new JButton("打开输出文件夹");
		// resultPanel.add(cfaButton, new GBC(0, 0).setInset(3));
		// resultPanel.add(argButton, new GBC(1, 0).setInset(3));
		// resultPanel.add(openButton, new GBC(0, 0).setInset(3));
		// southPanel.add(resultPanel, BorderLayout.NORTH);
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
		// cfaButton.addActionListener(satActionListener);
		// argButton.addActionListener(satActionListener);
		// openButton.addActionListener(satActionListener);

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
	private JMenu fileMenu = new JMenu("文件");
	private JMenu analysisMenu = new JMenu("分析");
	// private JMenu SchoolMangeMenu = new JMenu("学籍管理");
	private JMenu helpMenu = new JMenu("关于");

	private JButton srcButton;// 选择源代码
	private JButton specButton;// 选择规则文件
	private JButton startButton;// 开始分析

	// private JButton cfaButton;// 查看CFA
	// private JButton argButton;// 查看ARG
	// private JButton openButton;// 查看ARG

	private JTextArea specArea;
	private JTextArea srcArea;
	private JTextArea outputArea;

	private JTextField timeLimitField;
	private JTextField predUpLimitField;
	private JTextField predFreUpLimitField;
	private JTextField predFreDownLimitField;
	private JTextField predRedUpLimitField;

	private JLabel specFileLabel;
	private JLabel predFreUpLimitLabel;
	private JLabel predFreDownLimitLabel;

	private SatActionListener satActionListener;

	public JTextField getPredRedUpLimitField() {
		return predRedUpLimitField;
	}

	public void setPredRedUpLimitField(JTextField predRedUpLimitField) {
		this.predRedUpLimitField = predRedUpLimitField;
	}

	public JTextField getTimeLimitField() {
		return timeLimitField;
	}

	public void setTimeLimitField(JTextField timeLimitField) {
		this.timeLimitField = timeLimitField;
	}

	public JTextField getPredUpLimitField() {
		return predUpLimitField;
	}

	public void setPredUpLimitField(JTextField predUpLimitField) {
		this.predUpLimitField = predUpLimitField;
	}

	public JTextField getPredFreUpLimitField() {
		return predFreUpLimitField;
	}

	public void setPredFreUpLimitField(JTextField predFreUpLimitField) {
		this.predFreUpLimitField = predFreUpLimitField;
	}

	public JTextField getPredFreDownLimitField() {
		return predFreDownLimitField;
	}

	public void setPredFreDownLimitField(JTextField predFreDownLimitField) {
		this.predFreDownLimitField = predFreDownLimitField;
	}

	public JLabel getSpecFileLabel() {
		return specFileLabel;
	}

	public void setSpecFileLabel(JLabel specFileLabel) {
		this.specFileLabel = specFileLabel;
	}

	public JLabel getPredFreUpLimitLabel() {
		return predFreUpLimitLabel;
	}

	public void setPredFreUpLimitLabel(JLabel predFreUpLimitLabel) {
		this.predFreUpLimitLabel = predFreUpLimitLabel;
	}

	public JLabel getPredFreDownLimitLabel() {
		return predFreDownLimitLabel;
	}

	public void setPredFreDownLimitLabel(JLabel predFreDownLimitLabel) {
		this.predFreDownLimitLabel = predFreDownLimitLabel;
	}

	public JMenuBar getMenubar() {
		return menubar;
	}

	public void setMenubar(JMenuBar menubar) {
		this.menubar = menubar;
	}

	public JMenu getFileMenu() {
		return fileMenu;
	}

	public void setFileMenu(JMenu fileMenu) {
		this.fileMenu = fileMenu;
	}

	public JMenu getAnalysisMenu() {
		return analysisMenu;
	}

	public void setAnalysisMenu(JMenu analysisMenu) {
		this.analysisMenu = analysisMenu;
	}

	// public JMenu getSchoolMangeMenu() {
	// return SchoolMangeMenu;
	// }
	//
	// public void setSchoolMangeMenu(JMenu schoolMangeMenu) {
	// SchoolMangeMenu = schoolMangeMenu;
	// }

	public JMenu getHelpMenu() {
		return helpMenu;
	}

	public void setHelpMenu(JMenu helpMenu) {
		this.helpMenu = helpMenu;
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

	// public JButton getCfaButton() {
	// return cfaButton;
	// }
	//
	// public void setCfaButton(JButton cfaButton) {
	// this.cfaButton = cfaButton;
	// }
	//
	// public JButton getArgButton() {
	// return argButton;
	// }
	//
	// public void setArgButton(JButton argButton) {
	// this.argButton = argButton;
	// }

	public JTextArea getSpecArea() {
		return specArea;
	}

	// public JButton getOpenButton() {
	// return openButton;
	// }
	//
	// public void setOpenButton(JButton openButton) {
	// this.openButton = openButton;
	// }

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

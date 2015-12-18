package edu.buaa.satla.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.sosy_lab.cpachecker.cmdline.CPAMain;
import org.sosy_lab.cpachecker.util.globalinfo.GlobalInfo;

import com.sun.corba.se.spi.orb.StringPair;

public class SatActionListener implements ActionListener {

	private final SatMainFrame satMainFrame;

	private File specFile;// 规则文件

	private File srcFile;// 源代码文件

	private File outputDir;
	
	private JTextArea outputArea;
	
	int maxPredSetSize = 50;
	
	int predFreDownLimit = 5;
	
	int predFreUpLimit = 50;

	public SatActionListener(final SatMainFrame satMainFrame) {
		this.satMainFrame = satMainFrame;
		this.outputArea = satMainFrame.getOutputArea();
	}

	private File getChosenFile(String path, boolean src) {
		JFileChooser fileChooser = new JFileChooser(path);
		FileNameExtensionFilter filter = null;
		if (src) {
			filter = new FileNameExtensionFilter("C Source Files", "c", "txt");
		} else {
			filter = new FileNameExtensionFilter("Specification Files", "spc",
					"txt", "properties");
		}
		fileChooser.setFileFilter(filter);
		int returnVal = fileChooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			String output = null;
			if (src)
				output = "选择了源文件：";
			else
				output = "选择了规则文件：";
			outputArea.append(output + f.getAbsolutePath() + "\n");
			return f;
		}
		return null;
	}

	private void setTextArea(File f, JTextArea textArea) {
		if (f == null)
			return;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(f)));
			String lineStr;
			StringBuilder text = new StringBuilder();
			String lineSep = System.getProperty("line.separator");
			while ((lineStr = br.readLine()) != null) {
				text.append(lineStr).append(lineSep);
			}
			textArea.setText(text.toString());
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = e.getActionCommand();
		if (name.equals("选择源文件")) {
			srcFile = getChosenFile(SatConstants.TEST_DIR_PATH, true);
			setTextArea(srcFile, satMainFrame.getSrcArea());
		} else if (name.equals("选择规则文件")) {
			// setTextArea(getChosenFile(SatConstants.SPEC_DIR_PATH, false),
			// satMainFrame.getSpecArea());
			specFile = getChosenFile(SatConstants.SPEC_DIR_PATH, false);
			if (specFile == null)
				return;
			if (specFile.exists()) {
				String fileName = specFile.getName();
				satMainFrame.getSpecFileLabel().setText(fileName);
				satMainFrame.getSpecFileLabel().setForeground(Color.BLACK);
			}
		} else if (name.equals("开始分析")) {
//			if (specFile == null) {
//				JOptionPane.showMessageDialog(null, "请选择规则文件！");
//			}
//			if (srcFile == null) {
//				JOptionPane.showMessageDialog(null, "请选择源代码文件！");
//			}
			outputArea.append("系统开始分析...\n");
			// 三个谓词界限
			try {
				maxPredSetSize = Integer.parseInt(satMainFrame
						.getPredUpLimitField().getText());
				outputArea.append("谓词集合最大值：" + maxPredSetSize +"\n");
			} catch(Exception ex) {
				outputArea.append("谓词集合最大值未设置，默认值为：" + maxPredSetSize +"\n");
			}
			try {
				predFreDownLimit = Integer.parseInt(satMainFrame
						.getPredFreDownLimitField().getText());
				outputArea.append("谓词频度上限：" + predFreDownLimit +"\n");
			} catch(Exception ex) {
				outputArea.append("谓词频度上限未设置，默认值为：" + predFreDownLimit +"\n");
			}
			try {
				predFreUpLimit = Integer.parseInt(satMainFrame
						.getPredFreUpLimitField().getText());
				outputArea.append("谓词频度下限：" + predFreUpLimit +"\n");

			} catch(Exception ex) {
				outputArea.append("谓词频度下限未设置，默认值为：" + predFreUpLimit +"\n");
			}
			
			GlobalInfo.setOutputArea(outputArea);
			
			Properties config = new Properties();
			config.load(new FileInputStream("libs/config/predicateAnalysis.properties"));
			
			String[] args = new String[2];
			args[0] = "-predicateAnalysis";
//			args[1] = srcFile.getAbsolutePath();
			args[1] = "/home/gf/test/my/emp2-bug-15.c";
			CPAMain.main(args);
		} else if (name.equals("打开输出文件夹")) {
			if (outputDir == null) {
				return;
			}
			try {
				Runtime.getRuntime().exec(
						"cmd /c start explorer " + outputDir.getAbsolutePath());
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "打开失败！文件路径：");
			}
		}
	}

}

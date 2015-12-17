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

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SatActionListener implements ActionListener {

	private final SatMainFrame satMainFrame;

	private File specFile;// 规则文件

	private File srcFile;// 源代码文件

	public SatActionListener(final SatMainFrame satMainFrame) {
		this.satMainFrame = satMainFrame;
	}

	private File getChosenFile(String path, boolean src) {
		JFileChooser fileChooser = new JFileChooser(path);
		FileNameExtensionFilter filter = null;
		if (src) {
			filter = new FileNameExtensionFilter("C Source Files", "c", "txt");
		} else {
			filter = new FileNameExtensionFilter("Specification Files", "spec",
					"txt");
		}
		fileChooser.setFileFilter(filter);
		int returnVal = fileChooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: "
					+ fileChooser.getSelectedFile().getName());
			return fileChooser.getSelectedFile();
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
		if (name.equals("选择源代码")) {
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
			if (specFile == null) {
				JOptionPane.showMessageDialog(null, "请选择规则文件！");
			}
			if (srcFile == null) {
				JOptionPane.showMessageDialog(null, "请选择源代码文件！");
			}

			// 三个谓词界限
			int maxPredSetSize = Integer.parseInt(satMainFrame
					.getPredUpLimitField().getText());
			int predFreDownLimit = Integer.parseInt(satMainFrame
					.getPredFreDownLimitField().getText());
			int predFreUpLimit = Integer.parseInt(satMainFrame
					.getPredFreUpLimitField().getText());

		}
	}

}

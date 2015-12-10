package edu.buaa.gaof.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jfree.ui.RefineryUtilities;

public class SatMainFrame extends JFrame {

	public SatMainFrame() {
		setTitle("SAT_LA_CMP静态错误定位系统");
		setSize(800, 600);
		getContentPane().setLayout(new BorderLayout(5, 10));
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

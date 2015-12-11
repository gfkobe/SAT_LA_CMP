package edu.buaa.gaof.view;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * GridBagLayout的GridBagConstraints
 */
public class GBC extends GridBagConstraints {
	/**
	 * 
	 * 设置组件的位置。 gridx设置为GridBagConstraints.RELATIVE代表此组件位于之前所加入组件的右边。 　　　　
	 * gridy设置为GridBagConstraints.RELATIVE代表此组件位于以前所加入组件的下面。 　　　　
	 * 建议定义出gridx,gridy的位置以便以后维护程序。gridx=0,gridy=0时放在0行0列。
	 * 
	 * @param x
	 * @param y
	 */
	public GBC(int x, int y) {
		this.gridx = x;
		this.gridy = y;
		setIpad(5, 5);// default
		// setAnchor(GBC.WEST);
	}

	/**
	 * gridwidth, gridheight用来设置组件所占的单位长度与高度，默认值皆为1。 　　　　　　　　　　
	 * 你可以使用GridBagConstraints.REMAINDER常量，代表此组件为此行或此列的最后一个组件，而且会占据所有剩余的空间。
	 * 
	 * @param gridx
	 * @param gridy
	 * @param gridwidth
	 * @param gridheight
	 */
	public GBC(int gridx, int gridy, int gridwidth, int gridheight) {
		this.gridx = gridx;
		this.gridy = gridy;
		this.gridwidth = gridwidth;
		this.gridheight = gridheight;
		setIpad(5, 5);// default
		// setAnchor(GBC.WEST);
	}

	public GBC setGrid(int gridx, int gridy) {
		this.gridx = gridx;
		this.gridy = gridy;
		return this;
	}

	public GBC setGrid(int gridx, int gridy, int gridwidth, int gridheight) {
		this.gridx = gridx;
		this.gridy = gridy;
		this.gridwidth = gridwidth;
		this.gridheight = gridheight;
		return this;
	}

	/**
	 * 当组件空间大于组件本身时，要将组件置于何处。
	 * 　　　　有CENTER(默认值)、NORTH、NORTHEAST、EAST、SOUTHEAST、WEST、NORTHWEST选择。
	 * 
	 * @param anchor
	 * @return
	 */
	public GBC setAnchor(int anchor) {
		this.anchor = anchor;
		return this;
	}

	/**
	 * This field is used when the component's display area is larger than the
	 * component's requested size. It determines whether to resize the
	 * component, and if so, how. The following values are valid for fill:
	 * 
	 * 
	 * NONE: Do not resize the component. HORIZONTAL: Make the component wide
	 * enough to fill its display area horizontally, but do not change its
	 * height. VERTICAL: Make the component tall enough to fill its display area
	 * vertically, but do not change its width. BOTH: Make the component fill
	 * its display area entirely.
	 * 
	 * 
	 * @param fill
	 * @return
	 */
	public GBC setFill(int fill) {
		this.fill = fill;
		return this;
	}

	/**
	 * 用来设置窗口变大时，各组件跟着变大的比例。当数字越大，表示组件能得到更多的空间，默认值皆为0。
	 * 
	 * @param weightx
	 * @param weighy
	 * @return
	 */
	public GBC setWeight(double weightx, double weighty) {
		this.weightx = weightx;
		this.weighty = weighty;
		return this;
	}

	/**
	 * @param distance
	 * @return
	 */
	public GBC setInset(int distance) {
		this.insets = new Insets(distance, distance, distance, distance);
		return this;
	}

	/**
	 * 设置组件之间彼此的间距。它有四个参数，分别是上，左，下，右，默认为(0,0,0,0)。
	 * 
	 * @param distance
	 * @return
	 */
	public GBC setInset(int top, int left, int bottom, int right) {
		// this.insets = new Insets(top, left, bottom, right);
		this.insets.set(top, left, bottom, right);
		return this;
	}

	/**
	 * 设置组件间距，默认值为0。
	 * 
	 * @param ipadx
	 * @param ipady
	 * @return
	 */
	public GBC setIpad(int ipadx, int ipady) {
		this.ipadx = ipadx;
		this.ipady = ipady;
		return this;
	}
}

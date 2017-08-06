package net.m4christ.usb.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public class VerticalFlowLayout implements LayoutManager {
	public static final int CENTER = 0;
	public static final int RIGHT = 1;
	public static final int LEFT = 2;
	public static final int BOTH = 3;
	public static final int TOP = 1;
	public static final int BOTTOM = 2;
	private int vgap;
	private int alignment;
	private int anchor;

	public VerticalFlowLayout() {
		this(5, 3, 1);
	}

	public VerticalFlowLayout(int vgap) {
		this(vgap, 3, 1);
	}

	public VerticalFlowLayout(int vgap, int alignment) {
		this(vgap, alignment, 1);
	}

	public VerticalFlowLayout(int vgap, int alignment, int anchor) {
		this.vgap = vgap;
		this.alignment = alignment;
		this.anchor = anchor;
	}

	private Dimension layoutSize(Container parent, boolean minimum) {
		Dimension dim = new Dimension(0, 0);
		synchronized (parent.getTreeLock()) {
			int n = parent.getComponentCount();
			for (int i = 0; i < n; i++) {
				Component c = parent.getComponent(i);
				if (c.isVisible()) {
					Dimension d = minimum ? c.getMinimumSize() : c.getPreferredSize();
					dim.width = Math.max(dim.width, d.width);
					dim.height += d.height;
					if (i > 0) {
						dim.height += this.vgap;
					}
				}
			}
		}
		Insets insets = parent.getInsets();
		dim.width += insets.left + insets.right;
		dim.height += insets.top + insets.bottom + this.vgap + this.vgap;
		return dim;
	}

	public void layoutContainer(Container parent) {
		Insets insets = parent.getInsets();
		synchronized (parent.getTreeLock()) {
			int n = parent.getComponentCount();
			Dimension pd = parent.getSize();
			int y = 0;
			for (int i = 0; i < n; i++) {
				Component c = parent.getComponent(i);
				Dimension d = c.getPreferredSize();
				y += d.height + this.vgap;
			}
			y -= this.vgap;
			if (this.anchor == 1) {
				y = insets.top;
			} else if (this.anchor == 0) {
				y = (pd.height - y) / 2;
			} else {
				y = pd.height - y - insets.bottom;
			}
			for (int i = 0; i < n; i++) {
				Component c = parent.getComponent(i);
				Dimension d = c.getPreferredSize();
				int x = insets.left;
				int wid = d.width;
				if (this.alignment == 0) {
					x = (pd.width - d.width) / 2;
				} else if (this.alignment == 1) {
					x = pd.width - d.width - insets.right;
				} else if (this.alignment == 3) {
					wid = pd.width - insets.left - insets.right;
				}
				c.setBounds(x, y, wid, d.height);
				y += d.height + this.vgap;
			}
		}
	}

	public Dimension minimumLayoutSize(Container parent) {
		return layoutSize(parent, false);
	}

	public Dimension preferredLayoutSize(Container parent) {
		return layoutSize(parent, false);
	}

	public void addLayoutComponent(String name, Component comp) {
	}

	public void removeLayoutComponent(Component comp) {
	}

	public String toString() {
		return getClass().getName() + "[vgap=" + this.vgap + " align="
				+ this.alignment + " anchor=" + this.anchor + "]";
	}
}

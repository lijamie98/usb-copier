package net.m4christ.usb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import net.m4christ.usb.io.FileUtils;
import net.m4christ.usb.ui.DrivePanel;

public class GlobalStatus {
	private static final File persistFile = new File(
			System.getProperty("user.home"), "usb_copier.properties");
	private static USBMainFrame mainFrame;
	private static Properties p = new Properties();
	private static long sourceSize = Long.MIN_VALUE;
	private static List<DrivePanel> drivePanels;
	private static List<File> sourceFiles;

	public GlobalStatus() {
	}

	public static File getSourceDir() {
		String sourceDir = p.getProperty("sourceDir");
		if (sourceDir == null) {
			return null;
		}
		return new File(sourceDir);
	}

	public static void setSourceDir(File sd) {
		if (sd != null) {
			p.setProperty("sourceDir", sd.getAbsolutePath());
			sourceFiles = null;
		}
	}

	public static long getSourceSize() {
		return sourceSize;
	}

	public static void setSourceSize(long ss) {
		sourceSize = ss;
	}

	public static List<DrivePanel> getDrivePanels() {
		return drivePanels;
	}

	public static void setDrivePanels(List<DrivePanel> drivePanels) {
		GlobalStatus.drivePanels = drivePanels;
	}

	public static List<File> getSourceFiles() {
		if (sourceFiles == null) {
			if (getSourceDir() == null) {
				return null;
			}
			sourceFiles = FileUtils.searchFiles(getSourceDir(), false);
		}
		return sourceFiles;
	}

	public static USBMainFrame getMainFrame() {
		return mainFrame;
	}

	public static void setMainFrame(USBMainFrame mainFrame) {
		GlobalStatus.mainFrame = mainFrame;
	}

	public static String getDiskLabel() {
		return mainFrame.getDiskLabel();
	}

	public static void setDiskLabel(String label) {
		mainFrame.setDiskLabel(label);
	}

	public static void save() throws IOException {
		if (mainFrame != null) {
			p.setProperty("diskLabel", getDiskLabel());
		}
		FileOutputStream fos = new FileOutputStream(persistFile);
		p.store(fos, "");
		fos.close();
	}

	public static void load() throws IOException {
		try {
			FileInputStream fis = new FileInputStream(persistFile);
			p.load(fis);
			if (mainFrame != null) {
				setDiskLabel(p.getProperty("diskLabel"));
			}
			fis.close();
		} catch (FileNotFoundException localFileNotFoundException) {
		}
	}
}

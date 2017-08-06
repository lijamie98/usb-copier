package net.m4christ.usb.tasks;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.m4christ.usb.ui.DrivePanel;

public abstract class AbstractTask implements Runnable {
	protected DrivePanel dp;

	protected AbstractTask(DrivePanel dp) {
		this.dp = dp;
	}
	
	public void run() {
		try {
			dp.clearAlert();
			dp.setProcessing(true);
			dp.setProgress(0);
			dp.repaint();
			performTask();
		} catch (Exception e) {
			dp.setAlert();
			dp.setMessage(e.toString() + e.getMessage());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			e.printStackTrace(ps);
			log(new String(baos.toByteArray()));
		} finally {
			dp.clearInterrupt();
			dp.setProcessing(false);
			dp.repaint();
		}
	}
	
	public abstract void performTask() throws Exception;
	
	public void log(String message) {
		dp.log(message);
	}
}

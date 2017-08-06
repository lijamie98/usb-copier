package net.m4christ.usb.tasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.m4christ.usb.GlobalStatus;
import net.m4christ.usb.io.FileUtils;
import net.m4christ.usb.ui.DrivePanel;


public class CopyTask extends VerifyTask {
	private boolean bVerify = false; 

	public CopyTask(DrivePanel dp, boolean bVerify) {
		super(dp);
		this.bVerify = bVerify;
	}

	@Override
	public void performTask() throws Exception {
		copy();
		if (!dp.isInterrupted() && bVerify) {
			dp.setProgress(0);
			verify();
		}
	}

	int lastProgress = 0;
	long totalSize = 0L;
	long processed = 0L;
	long startTime = 0L;
	long time = 0;
	long speed = 0;
	double timeRemaining = 0;
	int progress = 0;

	private void copy() throws Exception {
		synchronized (dp) {
			this.startTime = System.currentTimeMillis();
			File srcFolder = GlobalStatus.getSourceDir();
			File dstFolder = new File(dp.getDrive().LD_DeviceID + "/");
	
			this.totalSize = GlobalStatus.getSourceSize();
			this.processed = 0L;
	
			copyFolder(srcFolder, dstFolder);
			
			if (dp.isInterrupted()) {
				showProgress("已取消！ ");
				dp.setAlert();
			} else {
				showProgress("拷貝完成！ ");
				dp.setSuccess();
			}
		}
	}

	private void copyFolder(File src, File dest) throws IOException {
		if (dp.isInterrupted()) {
			return;
		}
		
		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdirs();
			}
			String[] files = src.list();
			List<String> filenames = Arrays.asList(files);
			
			Collections.sort(filenames);
			
			for (String filename : filenames) {
				File srcFile = new File(src, filename);
				File destFile = new File(dest, filename);

				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream fis = new FileInputStream(src);
			OutputStream bos = new BufferedOutputStream(new FileOutputStream(dest), 40960);

			byte[] buffer = new byte[40960];

			int progress = 0;
			int length;
			
			while ((length = fis.read(buffer)) > 0) {
				if (dp.isInterrupted()) {
					log("Interrupted.");
					break;
				}
				bos.write(buffer, 0, length);
				this.processed += length;
				progress = (int) (this.processed * 100L / this.totalSize);
				if (progress != this.lastProgress) {
					time = System.currentTimeMillis() - this.startTime;
					speed = this.processed * 1000L / (time + 1L);
					timeRemaining = time * (this.totalSize - this.processed) / this.processed;
					
					showProgress("拷貝 " + src.getName());
					dp.setProgress(progress);
					dp.repaint();
					this.lastProgress = progress;
				}
			}
			
			log(src.getAbsolutePath() + " copied to " + dest.getAbsolutePath());
			
			fis.close();
			bos.flush();
			bos.close();
		}
	}
	
	
	private void showProgress(String message) {
		dp.setMessage(" 速度=" + FileUtils.readableFileSize(speed) + "/s. 剩餘時間="
				+ (int) Math.ceil(timeRemaining / 1000.0D) + "s. 已用時間=" + time / 1000L + "s. [" + message + "]");
	}
}


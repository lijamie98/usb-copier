package net.m4christ.usb.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.m4christ.usb.ui.DrivePanel;

public class BlinkTask extends AbstractTask {
	
	public BlinkTask(DrivePanel dp) {
		super(dp);
	}
	
	@Override
	public void performTask() throws Exception {
		blink();
	}
	
	private void blink() throws IOException {
		synchronized (dp) {
			File dstFolder = new File(dp.getDrive().LD_DeviceID + "/");
			File tmp = new File(dstFolder, "temp-" + System.currentTimeMillis());
			log("Writing to " + tmp);
	
			// initialize buffer
			byte[] buf = new byte[10240];
			for (int x = 0; x < buf.length; x++) {
				buf[x] = (byte) (x % 255);
			}
	
			for (int i = 0; i < 1; i++) {
				if (dp.isInterrupted()) {
					break;
				}
				FileOutputStream fos = new FileOutputStream(tmp);
				for (int j = 0; j < 200; j++) {
					fos.write(buf);
				}
				fos.close();
				tmp.delete();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			log("Delete " + tmp);
		}
	}
}


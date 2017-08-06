package net.m4christ.usb.tasks;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import net.m4christ.usb.GlobalStatus;
import net.m4christ.usb.io.FileUtils;
import net.m4christ.usb.ui.DrivePanel;

public class VerifyTask extends AbstractTask {
	public VerifyTask(DrivePanel dp) {
		super(dp);
	}
	
	@Override
	public void performTask() throws Exception {
		verify();
	}

	public void verify() {
		synchronized(dp) {
		
			File srcFolder = GlobalStatus.getSourceDir();
			File dstFolder = new File(dp.getDrive().LD_DeviceID + "/");
			
			List<File> srcFiles = FileUtils.searchFiles(srcFolder, false);
			List<File> dstFiles = FileUtils.searchFiles(dstFolder, false);
			
			HashMap<String, File> hmFiles = new HashMap<String, File>();
			for (File f : dstFiles) {
				hmFiles.put(f.getName(), f);
			}
			
			for (File srcFile : srcFiles) {
				File dstFile = hmFiles.get(srcFile.getName());
				if (dstFile == null) { // not found
					// error
					dp.setMessage("無法找到 " + srcFile.getName());
					log(srcFile.getName() + " not found.");
					dp.setAlert();
					return;
				} else if (dstFile.length() != srcFile.length()) {
					// error.
					dp.setMessage(srcFile.getName() + "  比對失敗！");
					log(srcFile.getAbsolutePath() + " size comparison failed. src_size = " + srcFile.length() + " dest_size=" + dstFile.length());
					dp.setAlert();
					return;
				} else {
					// ok.
					dp.setMessage(srcFile.getName() + "比對 OK.");
					log(srcFile.getName() + " verified ok.");
				}
			}
			
			dp.setMessage("比對成功");
			dp.setSuccess();
			log(dstFolder.getAbsolutePath() + " verified ok.");
		}
	}
}

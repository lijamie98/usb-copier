package net.m4christ.usb.tasks;

import java.io.File;
import java.util.List;

import net.m4christ.usb.io.FileUtils;
import net.m4christ.usb.ui.DrivePanel;
import net.m4christ.usb.ui.ProgressListener;

public class EraseTask extends AbstractTask implements ProgressListener {
	public EraseTask(DrivePanel dp) {
		super(dp);
	}

	@Override
	public void performTask() throws Exception {
		File dstFolder = new File(dp.getDrive().LD_DeviceID + "/");
		eraseFiles(dstFolder, this);
	}
	
	public void changed(int progress) {
		dp.setMessage("清除中... " + progress + "%");
		dp.setProgress(progress);
	}

	public void completed() {
		dp.setMessage("清除完成!");
		dp.setProgress(100);
		dp.setSuccess();
	}
	
	private void eraseFiles(File folder, ProgressListener progressListener) {
		synchronized (dp) {
			List<File> files = FileUtils.searchFiles(folder, true);
			int totalFiles = files.size();
			int fileErased = 0;
			int progress = 0;
			progressListener.changed(progress);
			for (File f : files) {
				if (dp.isInterrupted()) {
					break;
				}
				if (f.isDirectory()) {
					f.delete();
					log("Folder:" + f.getAbsolutePath() + " deleted.");
				} else {
					f.delete();
					log(f.getAbsolutePath() + " deleted.");
					fileErased++;
					progress = fileErased * 100 / totalFiles;
					progressListener.changed(progress);
				}
			}
			progressListener.completed();
		}
	}
}


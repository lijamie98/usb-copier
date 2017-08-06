package net.m4christ.usb.tasks;

import net.m4christ.usb.GlobalStatus;
import net.m4christ.usb.ui.DrivePanel;

public class LabelTask extends AbstractTask {
	public LabelTask(DrivePanel dp) {
		super(dp);
	}
	
	@Override
	public void performTask() throws Exception {
		dp.setMessage("寫入標籤" );
		label();
		dp.setMessage("標籤寫入完成" );
		dp.setSuccess();
	}	
	
	private synchronized int label() throws Exception {
		synchronized (dp) {
			String cmd = "label.exe " + dp.getDrive().LD_DeviceID + GlobalStatus.getDiskLabel();
			Process status = Runtime.getRuntime().exec(cmd);
			log(cmd);
			return status.waitFor();
		}
	}
}


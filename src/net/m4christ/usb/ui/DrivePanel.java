package net.m4christ.usb.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.swing.DefaultButtonModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EtchedBorder;

import net.m4christ.drives.Drive;
import net.m4christ.usb.GlobalStatus;
import net.m4christ.usb.USBMainFrame;
import net.m4christ.usb.USBThreadPool;
import net.m4christ.usb.io.FileUtils;
import net.m4christ.usb.tasks.BlinkTask;
import net.m4christ.usb.tasks.CopyTask;
import net.m4christ.usb.tasks.EraseTask;
import net.m4christ.usb.tasks.LabelTask;
import net.m4christ.usb.tasks.VerifyTask;

@SuppressWarnings("serial")
public class DrivePanel extends JPanel {
	public static final int BUFFER_SIZE = 4096;
	public static final Insets BUTTON_INSETS = new Insets(1, 5, 1, 5);
	private Drive drive;
	private JProgressBar progressBar;
	/**
	 * The flag that determines if a processing is on-going.
	 */
	private boolean processing = false;

	/**
	 * The flag that determines if interruption has happened.
	 */
	private boolean interrupted = false;
	
	private USBMainFrame usbMainFrame;
	private JButton btnBlink;
	private JLabel lblProgress;
	

	
	public DrivePanel(USBMainFrame frame, Drive drive) {
		this.usbMainFrame = frame;
		this.drive = drive;
		createPanel(drive);
	}

	private void createPanel(Drive drive) {
		GridBagLayout gbl = new GridBagLayout();
//		gbl.columnWidths = new int[1];
//		gbl.rowHeights = new int[2];
//		gbl.columnWeights = new double[] { Double.MIN_VALUE };
//		gbl.rowWeights = new double[] { Double.MIN_VALUE, 0.0D };

		setLayout(gbl);
		setBackground(Color.lightGray);

		// Blink Button
		btnBlink = new JButton(this.drive.LD_DeviceID);
		btnBlink.setPreferredSize(new Dimension(50, 45));
		GridBagConstraints gbc_btnBlink = new GridBagConstraints();
		gbc_btnBlink.fill = GridBagConstraints.VERTICAL;
		gbc_btnBlink.insets = new Insets(2, 2, 2, 2);
		gbc_btnBlink.gridheight = 2;
		gbc_btnBlink.gridx = 0;
		gbc_btnBlink.gridy = 0;
		add(btnBlink, gbc_btnBlink);

		btnBlink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Blink
				DrivePanel.this.startBlink();
			}
		});
		btnBlink.setModel(new DefaultButtonModel() {
			public boolean isEnabled() {
				return DrivePanel.this.isCopyReady();
			}
		});
		
		// Progress Bar
		this.progressBar = new JProgressBar();
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.gridwidth = 2;
		gbc_progressBar.insets = new Insets(0, 0, 0, 0);
		gbc_progressBar.gridx = 1;
		gbc_progressBar.gridy = 1;
		add(this.progressBar, gbc_progressBar);
		this.progressBar.setString("");
		this.progressBar.setMaximum(100);
		this.progressBar.setMinimum(0);
		this.progressBar.setStringPainted(true);
		this.progressBar.setForeground(new Color(150, 168, 210));
		this.progressBar.setLayout(new GridLayout(1, 1, 1, 1));
		this.lblProgress = new JLabel();
		this.progressBar.add(this.lblProgress);
		
		// Label
		JLabel lblDrive = new JLabel(" [" + drive.LD_VolumeName + "]-[" + drive.LD_FileSystem + "]");
		GridBagConstraints gbc_lblDrive = new GridBagConstraints();
		gbc_lblDrive.insets = new Insets(3, 0, 3, 3);
		gbc_lblDrive.anchor = 18;
		gbc_lblDrive.gridx = 1;
		gbc_lblDrive.gridy = 0;
		add(lblDrive, gbc_lblDrive);

		JLabel lblMessage = new JLabel(" 空間: " + FileUtils.readableFileSize(drive.LD_FreeSpace) + " | " + " 型號: "
				+ makeShorter(drive.DD_Caption, 15));
		GridBagConstraints gbc_lblFreeSpace = new GridBagConstraints();
		gbc_lblFreeSpace.anchor = 18;
		gbc_lblFreeSpace.weightx = 1.0D;
		gbc_lblFreeSpace.insets = new Insets(3, 0, 3, 3);
		gbc_lblFreeSpace.gridheight = 1;
		gbc_lblFreeSpace.gridx = 2;
		gbc_lblFreeSpace.gridy = 0;
		add(lblMessage, gbc_lblFreeSpace);

		// Copy button
		JButton btnCopy = new JButton("拷貝");
		btnCopy.setMargin(BUTTON_INSETS);
		GridBagConstraints gbc_btnStartDrive = new GridBagConstraints();
		gbc_btnStartDrive.fill = 0;
		gbc_btnStartDrive.insets = new Insets(1, 1, 0, 1);
		gbc_btnStartDrive.gridheight = 1;
		gbc_btnStartDrive.gridx = 3;
		gbc_btnStartDrive.gridy = 0;
		add(btnCopy, gbc_btnStartDrive);

		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				DrivePanel.this.startCopy(false);
			}
		});
		btnCopy.setModel(new DefaultButtonModel() {
			public boolean isEnabled() {
				return DrivePanel.this.isCopyReady();
			}
		});

		// Erase Button
		JButton btnErase = new JButton("清除");
		btnErase.setMargin(BUTTON_INSETS);
		GridBagConstraints gbc_btnErase = new GridBagConstraints();
		gbc_btnErase.fill = GridBagConstraints.NONE;
		gbc_btnErase.insets = new Insets(1, 1, 1, 1);
		gbc_btnErase.gridheight = 1;
		gbc_btnErase.gridx = 3;
		gbc_btnErase.gridy = 1;
		add(btnErase, gbc_btnErase);

		btnErase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				DrivePanel.this.startErase();
			}
		});
		btnErase.setModel(new DefaultButtonModel() {
			public boolean isEnabled() {
				return DrivePanel.this.isEraseReady();
			}
		});


		// Cancel Button
		JButton btnStop = new JButton("停止");
		btnStop.setMargin(BUTTON_INSETS);
		GridBagConstraints gbc_btnStop = new GridBagConstraints();
		gbc_btnStop.fill = 0;
		gbc_btnStop.insets = new Insets(1, 1, 0, 1);
		gbc_btnStop.gridheight = 1;
		gbc_btnStop.gridx = 4;
		gbc_btnStop.gridy = 0;
		add(btnStop, gbc_btnStop);

		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				DrivePanel.this.stop();
			}
		});
		btnStop.setModel(new DefaultButtonModel() {
			public boolean isEnabled() {
				return !(DrivePanel.this.isCopyReady());
			}
		});

		// Verify Button
		JButton btnVerify = new JButton("比對");
		btnVerify.setMargin(BUTTON_INSETS);
		GridBagConstraints gbc_btnVerify = new GridBagConstraints();
		gbc_btnVerify.fill = 0;
		gbc_btnVerify.insets = new Insets(1, 1, 1, 1);
		gbc_btnVerify.gridheight = 1;
		gbc_btnVerify.gridx = 4;
		gbc_btnVerify.gridy = 1;
		add(btnVerify, gbc_btnVerify);

		btnVerify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				startVerify();
			}
		});
		btnVerify.setModel(new DefaultButtonModel() {
			public boolean isEnabled() {
				return (DrivePanel.this.isCopyReady());
			}
		});


		// Open Location Button
		JButton btnOpenLocation = new JButton("內容");
		btnOpenLocation.setMargin(BUTTON_INSETS);
		GridBagConstraints gbc_btnOpenLocation = new GridBagConstraints();
		gbc_btnOpenLocation.fill = 0;
		gbc_btnOpenLocation.insets = new Insets(1, 1, 0, 1);
		gbc_btnOpenLocation.gridheight = 1;
		gbc_btnOpenLocation.gridx = 5;
		gbc_btnOpenLocation.gridy = 0;
		add(btnOpenLocation, gbc_btnOpenLocation);

		btnOpenLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// Open file location.
				File file = new File (DrivePanel.this.drive.LD_DeviceID);
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(file);
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		});
		btnOpenLocation.setModel(new DefaultButtonModel() {
			public boolean isEnabled() {
				return true;
			}
		});

		// Show Log Button
		JButton btnShowLog = new JButton("紀錄");
		btnShowLog.setMargin(BUTTON_INSETS);
		GridBagConstraints gbc_btnShowLog = new GridBagConstraints();
		gbc_btnShowLog.fill = 0;
		gbc_btnShowLog.insets = new Insets(1, 1, 1, 1);
		gbc_btnShowLog.gridheight = 1;
		gbc_btnShowLog.gridx = 5;
		gbc_btnShowLog.gridy = 1;
		add(btnShowLog, gbc_btnShowLog);

		btnShowLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// show log
				DrivePanel.this.usbMainFrame.showLog(sbLog.toString());
			}
		});
		btnShowLog.setModel(new DefaultButtonModel() {
			public boolean isEnabled() {
				return true;
			}
		});

		setBorder(new EtchedBorder(1, null, null));
	}

	public boolean isInterrupted() {
		return interrupted;
	}

	public void interrupt() {
		interrupted = true;
	}

	public void clearInterrupt() {
		interrupted = false;
	}

	public boolean isProcessing() {
		return processing;
	}
	
	public boolean isCopyReady() {
		if ((GlobalStatus.getSourceDir() == null) || (GlobalStatus.getSourceSize() <= 0L)) {
			return false;
		}
		if (this.drive.LD_FreeSpace < GlobalStatus.getSourceSize()) {
			return false;
		}
		if (this.processing) {
			return false;
		}
		return true;
	}

	public boolean isEraseReady() {
		if (this.processing) {
			return false;
		}
		return true;
	}

	public void startCopy(boolean bVerify) {
		USBThreadPool.getInstance().run(new CopyTask(this, bVerify));
	}

	public void startVerify() {
		USBThreadPool.getInstance().run(new VerifyTask(this));
	}

	public void startErase() {
		USBThreadPool.getInstance().run(new EraseTask(this));
	}

	public void startLabel() {
		USBThreadPool.getInstance().run(new LabelTask(this));
	}

	public void startBlink() {
		USBThreadPool.getInstance().run(new BlinkTask(this));
	}

	public void stop() {
		DrivePanel.this.repaint();
		this.interrupt();
	}


	public void setMessage(String message) {
		lblProgress.setText(message);
		progressBar.setString("");
	}
	
	public void setProgress(int progress) {
		progressBar.setValue(progress);
	}

	public void setProcessing(boolean processing) {
		this.processing = processing;
	}

	public Drive getDrive() {
		return drive;
	}

	public StringBuffer sbLog = new StringBuffer(); 
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	public void log(String message) {
		sbLog.append("[")
			.append(sdf.format(System.currentTimeMillis()))
			.append("] ")
			.append(message)
			.append("\n");
	}

	private int alertState = 0; // 0: default, 1: success, 2: error
	private Color bgColor = null;
	
	public void setAlert() {
		alertState = 2;
		updateAlertColor();
	}
	
	public void setSuccess() {
		alertState = 1;
		updateAlertColor();
	}
	
	public void clearAlert() {
		alertState = 0;
		updateAlertColor();
	}
	
	@Override
	public Dimension getMaximumSize() {
		return this.getPreferredSize();
	}
	
	private void updateAlertColor() {
		if (bgColor == null) {
			bgColor = this.getBackground();
		}
		switch (alertState) {
		case 0: 
			setBackground(bgColor);
			break;
		case 1: 
			setBackground(new Color(192, 255, 192));
			break;
		case 2: 
			setBackground(new Color(255, 128, 128));
			break;
		}
	}
	
	private static String makeShorter(String s, int length) {
		if (s.length() >= length) {
			s = s.substring(0, length - 3) + "..."; 
		}
		
		return s;
	}
}

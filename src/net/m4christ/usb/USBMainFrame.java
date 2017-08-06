package net.m4christ.usb;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import net.m4christ.drives.DiskDriveFactory;
import net.m4christ.drives.Drive;
import net.m4christ.usb.io.FileUtils;
import net.m4christ.usb.ui.DrivePanel;

@SuppressWarnings("serial")
public class USBMainFrame extends JFrame {
	private JPanel contentPane;
	private JFileChooser fc;
	private JTextField txtSelectedFolder;
	private JLabel txtTotalSize = new JLabel("");
	private JScrollPane scrollPane = new JScrollPane();
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
//	private JList<String> lstFiles = new JList<String>(this.listModel);
	private JPanel panel_3 = new JPanel();
	long totalSize;
	private JTextField txtDiskLabel;
	private JCheckBox chkVerify;
	private LogMessageDialog logDialog = new LogMessageDialog(this);

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					USBMainFrame frame = new USBMainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public USBMainFrame() {
		// set icon
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("title.png")));
		
		GlobalStatus.setMainFrame(this);

		Boolean old = Boolean.valueOf(UIManager.getBoolean("FileChooser.readOnly"));
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		this.fc = new JFileChooser();
		UIManager.put("FileChooser.readOnly", old);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {

			}

			public void windowOpened(WindowEvent arg0) {
				try {
					GlobalStatus.load();
					File f = GlobalStatus.getSourceDir();
					if (f != null) {
						USBMainFrame.this.setSourceDir(f);
						USBMainFrame.this.revalidate();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		setName("mainFrame");
		this.fc.setCurrentDirectory(new File("c:/"));
		this.fc.setPreferredSize(new Dimension(800, 600));

		setTitle("風中傳愛 U-盤拷貝軟件 v.2014.0.1");
		setDefaultCloseOperation(3);
		setBounds(100, 100, 1180, 800);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);

		this.panel_3 = new JPanel();
		this.contentPane.add(this.panel_3, "Center");
		this.panel_3.setLayout(new BorderLayout(3, 3));

//		JPanel pnlAttribute = new JPanel();
//		pnlAttribute.setMaximumSize(new Dimension(250, 32767));
//		pnlAttribute.setMinimumSize(new Dimension(20, 10));
//		pnlAttribute.setBorder(new EtchedBorder(1, null, null));
//		this.panel_3.add(pnlAttribute, "East");
//		pnlAttribute.setLayout(new GridLayout(0, 1, 0, 0));

//		JScrollPane scrollPane_1 = new JScrollPane();
//		pnlAttribute.add(scrollPane_1);
//
//		this.listModel = new DefaultListModel<String>();
//		this.lstFiles = new JList<String>(this.listModel);
//		this.lstFiles.setBorder(new EtchedBorder(1, null, null));
//		scrollPane_1.setViewportView(this.lstFiles);

		this.scrollPane = new JScrollPane();
		this.scrollPane.setBackground(SystemColor.inactiveCaption);
//		this.scrollPane.setBorder(new BevelBorder(1, Color.GRAY, Color.WHITE, null, null));
		this.panel_3.add(this.scrollPane, "Center");

		final JPanel pnlDrives = new JPanel();
		pnlDrives.setBackground(UIManager.getColor("InternalFrame.activeTitleBackground"));
		pnlDrives.setBorder(new EtchedBorder(1, null, null));
		this.scrollPane.setViewportView(pnlDrives);

//		pnlDrives.setLayout(new VerticalFlowLayout(1));

//		JPanel pnlScrollTitle = new JPanel();
//		pnlScrollTitle.setBackground(SystemColor.inactiveCaption);
//		pnlScrollTitle.setBorder(null);
//		this.scrollPane.setColumnHeaderView(pnlScrollTitle);
//		FlowLayout fl_pnlScrollTitle = (FlowLayout) pnlScrollTitle.getLayout();
//		fl_pnlScrollTitle.setAlignment(0);

		JPanel northPanel = new JPanel();
		this.contentPane.add(northPanel, "North");
		northPanel.setLayout(new GridLayout(2, 1, 0, 0));

		JPanel pnlRow1 = new JPanel();
		FlowLayout fl = (FlowLayout) pnlRow1.getLayout();
		fl.setAlignment(FlowLayout.LEFT);
		pnlRow1.setBorder(null);
		northPanel.add(pnlRow1);

		JLabel lblSrcFolder = new JLabel("來源目錄: ");
		pnlRow1.add(lblSrcFolder);
		
		this.txtSelectedFolder = new JTextField();
		this.txtSelectedFolder.setBorder(new EtchedBorder(1, null, null));
		this.txtSelectedFolder.setEditable(false);
		this.txtSelectedFolder.setPreferredSize(new Dimension(500, 25));
		pnlRow1.add(this.txtSelectedFolder);

		JButton btnBrowse = new JButton("選擇來源檔案夾");
		pnlRow1.add(btnBrowse);

		JButton btnShowSource = new JButton("顯示來源檔案夾");
		pnlRow1.add(btnShowSource);

		JLabel lblTotalSize = new JLabel("檔案容量:");
		pnlRow1.add(lblTotalSize);

		this.txtTotalSize = new JLabel("");
		pnlRow1.add(this.txtTotalSize);

		JPanel pnlRow2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pnlRow2.getLayout();
		flowLayout.setAlignment(0);
		northPanel.add(pnlRow2);
		

		JButton btnRefresh = new JButton("更新U盤列表");
		pnlRow2.add(btnRefresh);
		
		final JLabel lblDriveSummary = new JLabel("");
		pnlRow2.add(lblDriveSummary);

		JLabel lblDiskLabel = new JLabel("U盤標籤：");
		pnlRow2.add(lblDiskLabel);

		this.txtDiskLabel = new JTextField();
		pnlRow2.add(this.txtDiskLabel);
		this.txtDiskLabel.setPreferredSize(new Dimension(200, 25));

		JButton btnLabelAll = new JButton("寫入U盤標籤");
		btnLabelAll.setForeground(new Color(248, 248, 255));
		btnLabelAll.setBackground(new Color(0, 100, 0));
		pnlRow2.add(btnLabelAll);

		JButton btnCopyAll = new JButton("開始拷貝");
		btnCopyAll.setForeground(new Color(248, 248, 255));
		btnCopyAll.setBackground(new Color(0, 100, 0));
		pnlRow2.add(btnCopyAll);
		
		chkVerify = new JCheckBox("+比對");
		chkVerify.setSelected(true);
		pnlRow2.add(chkVerify);

		JButton btnVerifyAll = new JButton("開始比對");
		btnVerifyAll.setForeground(new Color(248, 248, 255));
		btnVerifyAll.setBackground(new Color(200, 150, 0));
		pnlRow2.add(btnVerifyAll);

		JButton btnEraseAll = new JButton("開始清除");
		btnEraseAll.setForeground(new Color(255, 248, 220));
		btnEraseAll.setBackground(new Color(128, 0, 0));
		pnlRow2.add(btnEraseAll);

		JButton btnStopAll = new JButton("全部停止");
		btnStopAll.setForeground(new Color(255, 248, 220));
		btnStopAll.setBackground(new Color(128, 0, 0));
		pnlRow2.add(btnStopAll);

		btnEraseAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<DrivePanel> drivePanels = GlobalStatus.getDrivePanels();
				if (drivePanels != null) {
					for (DrivePanel dp : drivePanels) {
						if (dp.isEraseReady()) {
							dp.startErase();
						}
					}
					USBMainFrame.this.startMonitor();
					USBMainFrame.this.revalidate();
					USBMainFrame.this.repaint();
				}
			}
		});
		
		btnCopyAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				List<DrivePanel> drivePanels = GlobalStatus.getDrivePanels();
				if (drivePanels != null) {
					for (DrivePanel dp : drivePanels) {
						if (dp.isCopyReady()) {
							dp.startCopy(chkVerify.isSelected());
						}
					}
					
					USBMainFrame.this.startMonitor();
					USBMainFrame.this.revalidate();
					USBMainFrame.this.repaint();
				}
			}
		});
		
		btnVerifyAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				List<DrivePanel> drivePanels = GlobalStatus.getDrivePanels();
				if (drivePanels != null) {
					for (DrivePanel dp : drivePanels) {
						if (dp.isCopyReady()) {
							dp.startVerify();
						}
					}
					USBMainFrame.this.startMonitor();
					USBMainFrame.this.revalidate();
					USBMainFrame.this.repaint();
				}
			}
			
		});
		
		btnLabelAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<DrivePanel> drivePanels = GlobalStatus.getDrivePanels();
				if (drivePanels != null) {
					for (DrivePanel dp : drivePanels) {
						if (dp.isCopyReady()) {
							dp.startLabel();
						}
					}
					USBMainFrame.this.startMonitor();
					USBMainFrame.this.revalidate();
					USBMainFrame.this.repaint();
				}
			}
		});
		
		btnStopAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<DrivePanel> drivePanels = GlobalStatus.getDrivePanels();
				if (drivePanels != null) {
					for (DrivePanel dp : drivePanels) {
						dp.stop();
					}
					USBMainFrame.this.startMonitor();
					USBMainFrame.this.revalidate();
					USBMainFrame.this.repaint();
				}
			}
		});
		
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				USBMainFrame.this.fc.setFileSelectionMode(1);
				USBMainFrame.this.fc.setSelectedFile(GlobalStatus.getSourceDir());

				int result = USBMainFrame.this.fc.showOpenDialog(USBMainFrame.this);
				if (result == 0) {
					File f = USBMainFrame.this.fc.getSelectedFile();
					USBMainFrame.this.setSourceDir(f);
					pnlDrives.revalidate();
					pnlDrives.repaint();
				}
			}
		});
		
		btnShowSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File folder = GlobalStatus.getSourceDir();
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(folder);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<DrivePanel> drivePanels = USBMainFrame.this.createDrivePanels();
				GlobalStatus.setDrivePanels(drivePanels);
				pnlDrives.removeAll();
				pnlDrives.setLayout(new GridBagLayout());
				
				int count = 0;
				for (JPanel panel : drivePanels) {
					GridBagConstraints glc = new GridBagConstraints();
					glc.gridx = count % 2;
					glc.gridy = count / 2;
					glc.fill = GridBagConstraints.HORIZONTAL;
					glc.weightx = 1;
					glc.weighty = 0;
					glc.anchor = GridBagConstraints.PAGE_START;
					glc.insets = (glc.gridx == 0) ? new Insets(2, 2, 0, 0) : new Insets(2, 2, 0, 2);
					pnlDrives.add(panel, glc);
					count++;
				}
				
				GridBagConstraints glc = new GridBagConstraints();
				glc.gridx = 0;
				glc.gridy = count+2 / 2;
				glc.fill = GridBagConstraints.NONE;
				glc.weightx = 1;
				glc.weighty = 1;
				glc.anchor = GridBagConstraints.PAGE_END;
				JComponent filler = new JLabel();
				pnlDrives.add(filler, glc);
				
				
				pnlDrives.revalidate();
				USBMainFrame.this.scrollPane.revalidate();

				lblDriveSummary.setText("(" + drivePanels.size() + ")");
				pnlDrives.repaint();
			}
		});
		
		this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                int i = JOptionPane.showConfirmDialog(USBMainFrame.this, "確定嗎？");
                if( i == 0) {
                	try {
						GlobalStatus.save();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
                    System.exit(0);
                }
            }
        });		
	}

	public String getDiskLabel() {
		return this.txtDiskLabel.getText();
	}

	public void setDiskLabel(String text) {
		this.txtDiskLabel.setText(text);
	}

	private void setSourceDir(File f) {
		if (f != null) {
			GlobalStatus.setSourceDir(f);
			this.txtSelectedFolder.setText(f.getAbsolutePath());

			this.totalSize = FileUtils.getTotalSize(f);
			this.txtTotalSize.setText(FileUtils.readableFileSize(this.totalSize));
			GlobalStatus.setSourceSize(this.totalSize);

			this.scrollPane.doLayout();
			this.scrollPane.repaint();

			repaint();

			List<File> sourceFiles = GlobalStatus.getSourceFiles();

			this.listModel.clear();
			if (sourceFiles != null) {
				for (File file : sourceFiles) {
					this.listModel.addElement(file.getName());
				}
				this.panel_3.doLayout();
			}
		}
	}

	private List<DrivePanel> createDrivePanels() {
		List<Drive> drives = DiskDriveFactory.getUSBDrives();
		List<DrivePanel> panels = new ArrayList<DrivePanel>();
		for (Drive drive : drives) {
			panels.add(new DrivePanel(this, drive));
		}
		return panels;
	}

	boolean monitoring = false;

	private void startMonitor() {
		if (this.monitoring) {
			return;
		}
		USBThreadPool.getInstance().run(new Runnable() {
			public void run() {
				try {
					USBMainFrame.this.monitoring = true;
					boolean bCont = true;
					while (bCont) {
						Thread.sleep(1000L);
						List<DrivePanel> drivePanels = GlobalStatus.getDrivePanels();
						boolean bBusy = false;
						for (DrivePanel dp : drivePanels) {
							if (dp.isProcessing()) {
								bBusy = true;
								break;
							}
						}
						bCont = bBusy;
					}
				} catch (InterruptedException localInterruptedException) {
				} finally {
					USBMainFrame.this.monitoring = false;
					playAlert();
				}
			}
		});
	}

	private static void playAlert() {
		Toolkit.getDefaultToolkit().beep();
	}
	
	public void showLog(String message) {
		this.logDialog.setMessage(message);
		this.logDialog.setVisible(true);
	}
}

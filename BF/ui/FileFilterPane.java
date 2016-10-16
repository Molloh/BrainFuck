package ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import rmi.RemoteHelper;

public class FileFilterPane extends JDialog implements ActionListener{
	/**
	 * 文件夹对话框，用于选择用户专有文件
	 */
	private static final long serialVersionUID = 1L;
	private JLabel label;
	private JComboBox<String> box;
	private JButton btn_CF;
	private JButton btn_CC;
	private String[] content;
	private String file;
	private String username;
	private String fileName;
	
	public FileFilterPane(String username, JFrame frame) {
		super(frame, "FILECHOOSER", true);
		this.username = username;
		
		label = new JLabel("Choose file: ");
		//下拉列表
		box = new JComboBox<String>();
		btn_CF = new JButton("CONFIRM");
		btn_CC = new JButton("CANCEL");	
		btn_CC.addActionListener(this);
		
		try {
			content = RemoteHelper.getInstance().getIOService().readFileList(username);
			if(content == null) {
				this.dispose();
				JOptionPane.showMessageDialog(null, "You haven't created any file!", "WARNING", JOptionPane.PLAIN_MESSAGE);
			}else {
				for(String item: content) {
					box.addItem(item);
				}
				
				btn_CF.addActionListener(this);
				
				JPanel jp1 = new JPanel();
				JPanel jp2 = new JPanel();
				
				jp1.add(label);
				jp1.add(box);
				jp2.add(btn_CF);
				jp2.add(btn_CC);
				
				this.setLayout(new GridLayout(2, 1));
				this.getContentPane().add(jp1);
				this.getContentPane().add(jp2);
				
				this.setSize(250, 160);
				this.setLocationRelativeTo(null);
				this.setVisible(true);
				this.pack();
			}
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}		
	}

	//获取文件内容
	public String getFile() {
		return this.file;
	}
	
	//获取文件名
	public String getFileName() {
		return fileName;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//读取相应文件
		if(e.getSource() == btn_CF) {
			try {
				if((String)box.getSelectedItem() != null) {
					fileName = (String)box.getSelectedItem();
					this.file = RemoteHelper.getInstance().getIOService().readFile(this.username, fileName);
				}
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			this.setVisible(false);
	        this.dispose();	
	        
		}else {
			this.setVisible(false);
	        this.dispose();	
		}
			
	}
	
}

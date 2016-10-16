package ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import rmi.RemoteHelper;

public class OptionPane extends JDialog implements ActionListener{
	/**
	 * 弹出窗口，用于注册登录
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel lb_ID;
	private JLabel lb_KEY;
	private JTextField tf_ID;
	private JPasswordField tf_PW;
	private JButton btn_CF;
	private JButton btn_CC;
	private JPanel jp1;
	private JPanel jp2;
	private JPanel jp3;
	private String title;
	private String username;
	
	public OptionPane(JFrame frame, String title) {
		super(frame, title, true);
		this.title = title;
		
		lb_ID = new JLabel("ID:                   ");
		lb_KEY = new JLabel("PASSWORD:");
		tf_ID = new JTextField(10);
		tf_PW = new JPasswordField(10);
		btn_CF = new JButton("CONFIRM");
		btn_CC = new JButton("CANCEL");
		
		btn_CF.addActionListener(this);
		btn_CC.addActionListener(this);
		
		this.setLayout(new GridLayout(3,1));
		
		jp1 = new JPanel();
		jp2 = new JPanel();
		jp3 = new JPanel();
		
		jp1.add(lb_ID);
		jp1.add(tf_ID);
		jp2.add(lb_KEY);
		jp2.add(tf_PW);
		jp3.add(btn_CF);
		jp3.add(btn_CC);
		
		this.getContentPane().add(jp1);
		this.getContentPane().add(jp2);
		this.getContentPane().add(jp3);
		
		this.setSize(300, 200);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.pack();
	}
	
	private String getID() {
		return tf_ID.getText();
	}
	
	private String getPASSWORD() {
		return String.valueOf(tf_PW.getPassword());
	}
	
	//获得最终用户名
	public String getUserName() {
		return username;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btn_CF) {
			if(title.equals("REGISTER")) {
				//注册
				try {
					boolean judge = RemoteHelper.getInstance().getUserService().register(this.getID(), this.getPASSWORD());
					if(judge) {
						JOptionPane.showMessageDialog(null, "You have registered successfully!", "REGISTER", JOptionPane.PLAIN_MESSAGE);
						username = RemoteHelper.getInstance().getUserService().getUsername();
					}else {
						JOptionPane.showMessageDialog(null, "This ID has been used!", "REGISTER", JOptionPane.PLAIN_MESSAGE);
					}
										
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				
			}else if(title.equals("LOGIN")) {
				//登陆
				try {
					boolean judge = RemoteHelper.getInstance().getUserService().login(this.getID(), this.getPASSWORD());
					if(judge) {
						JOptionPane.showMessageDialog(null, "You have logged in, code now!", "LOGIN", JOptionPane.PLAIN_MESSAGE);						
						username = RemoteHelper.getInstance().getUserService().getUsername();
					}else {
						JOptionPane.showMessageDialog(null, "Wrong password or id!", "LOGIN", JOptionPane.PLAIN_MESSAGE);
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}								
			}
			
			this.setVisible(false);
	        this.dispose();
	        
		}else {
			this.setVisible(false);
	        this.dispose();
		}		
	}
}
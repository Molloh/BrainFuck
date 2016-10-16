package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import rmi.RemoteHelper;

public class MainFrame extends JFrame {
	/**
	 * GUI
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextArea textArea;
	private JTextArea inputArea;
	private JTextArea resultArea;
	private JFrame frame;
	private String userId;
	private String fileName;
	
	private LinkedList<String> undo = new LinkedList<String>();
	private LinkedList<String> redo = new LinkedList<String>();
	
	public MainFrame() {
		// 创建窗体及设置布局
		frame = new JFrame("BrainFuck Client");		
		frame.setLayout(new GridBagLayout());
		frame.setSize(800, 600);
		
		//菜单栏
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		JMenu runMenu = new JMenu("Run");
		JMenu accountMenu = new JMenu("Account");
		JMenu versionMenu = new JMenu("Version");
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(runMenu);
		menuBar.add(accountMenu);
		menuBar.add(versionMenu);
		
		//添加子菜单
		JMenuItem newMenuItem = new JMenuItem("New");
		fileMenu.add(newMenuItem);
		JMenuItem openMenuItem = new JMenuItem("Open");
		fileMenu.add(openMenuItem);
		JMenuItem saveMenuItem = new JMenuItem("Save");
		fileMenu.add(saveMenuItem);
		JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
		fileMenu.add(saveAsMenuItem);
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		fileMenu.add(exitMenuItem);
		
		JMenuItem executeMenuItem = new JMenuItem("Execute");
		runMenu.add(executeMenuItem);
		
		JMenuItem undoMenuItem = new JMenuItem("Undo");
		editMenu.add(undoMenuItem);
		JMenuItem redoMenuItem = new JMenuItem("Redo");
		editMenu.add(redoMenuItem);
		
		JMenuItem registerMenuItem = new JMenuItem("Register");
		accountMenu.add(registerMenuItem);
		JMenuItem loginMenuItem = new JMenuItem("Login");
		accountMenu.add(loginMenuItem);
		JMenuItem logoutMenuItem = new JMenuItem("Logout");
		accountMenu.add(logoutMenuItem);
		
		JMenuItem[] version = new JMenuItem[] {
			new JMenuItem("VERSION_A"), new JMenuItem("VERSION_B"), new JMenuItem("VERSION_C")
		};
		for(JMenuItem item: version) {
			versionMenu.add(item);
			item.addActionListener(new VersionActionListener());
		}
		
		frame.setJMenuBar(menuBar);
		
		//添加监听
		newMenuItem.addActionListener(new FileActionListener());
		openMenuItem.addActionListener(new FileActionListener());
		saveMenuItem.addActionListener(new FileActionListener());
		saveAsMenuItem.addActionListener(new FileActionListener());
		exitMenuItem.addActionListener(new FileActionListener());		
		executeMenuItem.addActionListener(new RunActionListener());		
		registerMenuItem.addActionListener(new AccountActionListener());
		loginMenuItem.addActionListener(new AccountActionListener());
		logoutMenuItem.addActionListener(new AccountActionListener());		
		undoMenuItem.addActionListener(new EditActionListener());
		redoMenuItem.addActionListener(new EditActionListener());
				
		//添加快捷键
		undoMenuItem.setAccelerator(KeyStroke.getKeyStroke('Z',InputEvent.CTRL_MASK));
		redoMenuItem.setAccelerator(KeyStroke.getKeyStroke('Y',InputEvent.CTRL_MASK));
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke('N',InputEvent.CTRL_MASK));
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke('O',InputEvent.CTRL_MASK));
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke('W',InputEvent.CTRL_MASK));
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke('S',InputEvent.CTRL_MASK));
		executeMenuItem.setAccelerator(KeyStroke.getKeyStroke('R',InputEvent.CTRL_MASK));
		
		//CODE区域 
		textArea = new JTextArea();
		textArea.getDocument().addDocumentListener(new InputListener());
		textArea.setMargin(new Insets(5, 5, 5, 5));
		textArea.setBackground(Color.LIGHT_GRAY);
		textArea.setLineWrap(true);
		JScrollPane sp_text = new JScrollPane(textArea);
		sp_text .setBorder(BorderFactory.createTitledBorder("CODE"));
		//CODE布局
		GridBagConstraints bag1 = new GridBagConstraints();
		bag1.gridwidth = GridBagConstraints.REMAINDER;
		bag1.gridheight = 3;
		bag1.weightx = 1;
		bag1.weighty = 3;
		bag1.fill = GridBagConstraints.BOTH;
		bag1.anchor = GridBagConstraints.NORTH;
		frame.getContentPane().add(sp_text, bag1);
		
		//输入区域
		inputArea = new JTextArea();
		inputArea.setMargin(new Insets(5, 5, 5, 5));
		inputArea.setBackground(Color.LIGHT_GRAY);
		inputArea.setLineWrap(true);
		JScrollPane sp_input = new JScrollPane(inputArea);
		sp_input.setBorder(BorderFactory.createTitledBorder("INPUT"));
		//输入布局
		GridBagConstraints bag2 = new GridBagConstraints();
		bag2.gridwidth = 1;
		bag2.gridheight = 2;
		bag2.weightx = 1;
		bag2.weighty = 2;
		bag2.fill = GridBagConstraints.BOTH;
		bag2.anchor = GridBagConstraints.SOUTHWEST;
		frame.getContentPane().add(sp_input, bag2);
		
		//RESULT区域
		resultArea = new JTextArea();
		resultArea.setMargin(new Insets(5, 5, 5, 5));
		resultArea.setBackground(Color.LIGHT_GRAY);
		resultArea.setLineWrap(true);
		resultArea.setEditable(false);
		//指针移动到该区域为文本模式
		resultArea.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				resultArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
			}			
			public void mouseExited(MouseEvent e) {
				resultArea.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));			
			}
		});
		JScrollPane sp_result = new JScrollPane(resultArea);
		sp_result.setBorder(BorderFactory.createTitledBorder("RESULT"));
		//RESULT布局
		GridBagConstraints bag3 = new GridBagConstraints();
		bag3.gridwidth = 1;
		bag3.gridheight = 2;
		bag3.weightx = 1;
		bag3.weighty = 2;
		bag3.fill = GridBagConstraints.BOTH;
		bag3.anchor = GridBagConstraints.SOUTHEAST;
		frame.getContentPane().add(sp_result, bag3);

		//设置FRAME
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);	
	}

	/*
	 * File菜单响应事件
	 */
	class FileActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if(cmd.equals("New")) {
				//创建一个新窗口
				new MainFrame();
			}else if(cmd.equals("Open")) {
				//打开已保存的文件
				if(userId == null) {
					JOptionPane.showMessageDialog(null, "Please log in first!", "WARNING", JOptionPane.PLAIN_MESSAGE);
				}else {
					FileFilterPane ff = new FileFilterPane(userId, frame);
					textArea.setText(ff.getFile());
					fileName = ff.getFileName();			
				}
				
			}else if(cmd.equals("Save")) {
				String code = textArea.getText();
				if(code != null) {
					try {
						if(userId == null) {
							JOptionPane.showMessageDialog(null, "Please log in first!", "WARNING", JOptionPane.PLAIN_MESSAGE);
						}else {
							if(fileName == null) {
								fileName = JOptionPane.showInputDialog(null, "Name your file:", "FILESAVER",  JOptionPane.PLAIN_MESSAGE);
							}
							if(fileName != null) {
								String code_pre = RemoteHelper.getInstance().getIOService().readFile(userId, fileName);
								JOptionPane.showMessageDialog(null, "Your file have been successfully saved!", "SAVE", JOptionPane.PLAIN_MESSAGE);
								//保存的同时保留历史版本
								if(!code_pre.equals(code)) {
									RemoteHelper.getInstance().getIOService().writeFile(code, userId, fileName);
									RemoteHelper.getInstance().getIOService().writeFile(RemoteHelper.getInstance().getIOService().readFile(userId, fileName + "VERSION_B"), userId, fileName + "VERSION_C");
									RemoteHelper.getInstance().getIOService().writeFile(RemoteHelper.getInstance().getIOService().readFile(userId, fileName + "VERSION_A"), userId, fileName + "VERSION_B");
									RemoteHelper.getInstance().getIOService().writeFile(code_pre, userId, fileName + "VERSION_A");
								}
							}
						}
						
					}catch(RemoteException e1) {
						e1.printStackTrace();
					}
				}
			}else if(cmd.equals("Save As...")) {
				//另存为
				String code = textArea.getText();
				if(code != null) {
					try {
						fileName = JOptionPane.showInputDialog(null, "Name your file:", "FILESAVER",  JOptionPane.PLAIN_MESSAGE);
						if(fileName != null) {
							String code_pre = RemoteHelper.getInstance().getIOService().readFile(userId, fileName);
							RemoteHelper.getInstance().getIOService().writeFile(code, userId, fileName);
							JOptionPane.showMessageDialog(null, "Your file have been successfully saved!", "SAVE", JOptionPane.PLAIN_MESSAGE);
							//保存的同时保留历史版本
							if(!code_pre.equals(code)) {			
								RemoteHelper.getInstance().getIOService().writeFile(RemoteHelper.getInstance().getIOService().readFile(userId, fileName + "VERSION_B"), userId, fileName + "VERSION_C");
								RemoteHelper.getInstance().getIOService().writeFile(RemoteHelper.getInstance().getIOService().readFile(userId, fileName + "VERSION_A"), userId, fileName + "VERSION_B");
								RemoteHelper.getInstance().getIOService().writeFile(code_pre, userId, fileName + "VERSION_A");
							}
						}
					}catch(RemoteException e1) {
						e1.printStackTrace();
					}					
				}
				
			}else if(cmd.equals("Exit")) {
				//退出
				try {
					RemoteHelper.getInstance().getUserService().logout(userId);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);
			}
		}
	}
	
	/*
	 * Run菜单响应事件
	 */
	class RunActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String code = textArea.getText();
			String param = inputArea.getText();
			try {
				resultArea.setText(RemoteHelper.getInstance().getExecuteService().execute(code, param));
			}catch(RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}

	/*
	 * Account菜单响应事件
	 */
	class AccountActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			OptionPane op;
			if(cmd.equals("Register")){
				//注册
				op = new OptionPane(frame, "REGISTER");
				userId = op.getUserName();
				
			}else if(cmd.equals("Login")){
				//登陆
				op = new OptionPane(frame, "LOGIN");
				userId = op.getUserName();
				if(userId != null) {
					frame.setTitle("BrainFuck Client(" + userId + ")");	
				}
				
			}else if(cmd.equals("Logout")){
				//登出
				try {
					boolean judge = RemoteHelper.getInstance().getUserService().logout(userId);
					if(judge) {
						userId = "";
						frame.setTitle("BrainFuck Client");
						JOptionPane.showMessageDialog(null, "You've been logged out successfully!", "LOGOUT", JOptionPane.PLAIN_MESSAGE);
					}else {
						JOptionPane.showMessageDialog(null, "You've not been logged in!", "WARNING", JOptionPane.PLAIN_MESSAGE);
					}
					
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * Version菜单响应事件
	 */
	class VersionActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(userId == null) {
				JOptionPane.showMessageDialog(null, "Please log in first!", "WARNING", JOptionPane.PLAIN_MESSAGE);
			}else {
				//读取历史文件
				try {
					String content = RemoteHelper.getInstance().getIOService().readFile(userId, fileName + e.getActionCommand());
					if(content.equals("")) {
						JOptionPane.showMessageDialog(null, "This previous version  is not available!", "WARNING", JOptionPane.PLAIN_MESSAGE);
					}else {
						textArea.setText(content);
					}
				}catch(RemoteException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * Edit菜单响应事件
	 */
	class EditActionListener implements ActionListener {
		//链表实现
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if(cmd == "Undo") {
				try {
					redo.addLast(undo.getLast());
					undo.removeLast();
					textArea.setText(undo.getLast());
					undo.removeLast();
				}catch(NoSuchElementException e1) {					
				}
				
			}else if(cmd == "Redo") {
				try {
					textArea.setText(redo.getLast());
					undo.addLast(redo.getLast());
					redo.removeLast();
				}catch(NoSuchElementException e1) {					
				}
				
			}
		}
	}
	
	/*
	 * 对文本域的监听
	 */
	class InputListener implements DocumentListener {
		@Override
		public void insertUpdate(DocumentEvent e) {
			undo.addLast(textArea.getText());
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			undo.push(textArea.getText());
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			undo.addLast(textArea.getText());
		}		
	}
	
}

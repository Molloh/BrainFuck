package serviceImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import service.UserService;

public class UserServiceImpl implements UserService {	
	private String username;
	private ArrayList<String> haveLogged = new ArrayList<String>();
	@Override
	//注册功能实现
	public boolean register(String username, String password) throws RemoteException {
		boolean result = true;
		try {
			//从文件“UserImformation”中读取用户信息
			FileReader fr = new FileReader("users/UserImformation");
			BufferedReader br = new BufferedReader(fr);				
			String temp;
			while((temp = br.readLine())!= null) {
				if(temp.equals(username)) {
					result = false;
				}
			}
			fr.close();
			br.close();	
			//新建用户信息
			if(result) {
				this.username = username;
				FileWriter fw = new FileWriter("users/UserImformation", true);		
				fw.write(username + "\r\n");
				fw.write(password + "\r\n");
				fw.flush();
				fw.close();
			}			
			
		}catch(IOException ex) {
			ex.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public boolean login(String username, String password) throws RemoteException {
		//登陆功能实现
		boolean result = false;
		try {
			if(!haveLogged.contains(username)) {
				FileReader fr = new FileReader("users/UserImformation");
				BufferedReader br = new BufferedReader(fr);	
				String temp;
				//遍历用户信息文件
				while((temp = br.readLine())!= null) {
					if(temp.equals(username)) {
						if(br.readLine().equals(password)) {
							this.username = username;
							haveLogged.add(username);
							result = true;
						}
					}
				}
				fr.close();
				br.close();	
				
			}
						
		}catch(IOException ex) {
			ex.printStackTrace();
		}
		
		return result;
	}

	@Override
	public boolean logout(String username) throws RemoteException {
		if(username != null) {
			haveLogged.remove(username);
			this.username = "";
			return true;
		}else {
			return false;
		}
		
	}
	
	@Override
	public String getUsername() {
		return this.username;
	}

}

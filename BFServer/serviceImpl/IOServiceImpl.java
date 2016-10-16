package serviceImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import service.IOService;

public class IOServiceImpl implements IOService{
	//写操作 
	@Override
	public boolean writeFile(String file, String userId, String fileName) {
		String temp = "";
		String tempname = "";
		if(fileName.length() > 9) {
			temp = fileName.substring(fileName.length() - 9);
			tempname = fileName.substring(0, fileName.length() - 9);
		}
		//创建用户目录
		if(!temp.equals("VERSION_A") && !temp.equals("VERSION_B") && !temp.equals("VERSION_C")) {
			File dir = new File("code/" + userId + "/" + fileName);
			if(!dir.exists()) {
				dir.mkdirs();
			}
			
			//创建版本文件
			File A = new File(dir, "VERSION_A");
			if(!A.exists()) {
				try {
					A.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			File B = new File(dir, "VERSION_B");
			if(!B.exists()) {
				try {
					B.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			File C = new File(dir, "VERSION_C");
			if(!C.exists()) {
				try {
					C.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		//写入
		String filePath;
		
		if(temp.equals("VERSION_A")) {
			filePath = "code/" + userId + "/" + tempname + "/" + "VERSION_A";
		}else if(temp.equals("VERSION_B")) {
			filePath = "code/" + userId + "/" + tempname + "/" + "VERSION_B";
		}else if(temp.equals("VERSION_C")) {
			filePath = "code/" + userId + "/" + tempname + "/" + "VERSION_C";
		}else {
			filePath = "code/" + userId + "/" + fileName + "/" + fileName;
		}
		try {
			FileWriter fw = new FileWriter(filePath, false);
			fw.write(file);
			fw.flush();
			fw.close();
			return true;
		}catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	//读操作
	@Override
	public String readFile(String userId, String fileName) {
		String file = "";
		String filePath;
		String temp = "";
		String tempname = "";
		if(fileName.length() > 9) {
			temp = fileName.substring(fileName.length() - 9);
			tempname = fileName.substring(0, fileName.length() - 9);
		}
		if(temp.equals("VERSION_A")) {
			filePath = "code/" + userId + "/" + tempname + "/" + "VERSION_A";
		}else if(temp.equals("VERSION_B")) {
			filePath = "code/" + userId + "/" + tempname + "/" + "VERSION_B";
		}else if(temp.equals("VERSION_C")) {
			filePath = "code/" + userId + "/" + tempname + "/" + "VERSION_C";
		}else {
			filePath = "code/" + userId + "/" + fileName + "/" + fileName;
		}
		
		try {
			File f = new File(filePath);
			if(f.exists()) {
				FileReader fr = new FileReader(filePath);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				while(line != null) {
					file += line;
					line = br.readLine();
				}
				fr.close();
				br.close();			
			}
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	//读取文件列表
	@Override
	public String[] readFileList(String userId) {
		File dir = new File("code/" + userId);
		if(dir.exists()){
			File[] files = dir.listFiles();
			int length = files.length;
			String[] content = new String[length];
		
			if(files != null) {
				for(int i = 0; i < length; i ++) {
					if(files[i].getName() != "VERSION_A" && files[i].getName() != "VERSION_B" && files[i].getName() != "VERSION_C") {
						content[i] = files[i].getName();
					}
				}
			}
		
			return content;
		}else {
			return null;
		}
	}
	
}

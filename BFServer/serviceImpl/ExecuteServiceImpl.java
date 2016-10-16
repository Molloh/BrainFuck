//请不要修改本文件名
package serviceImpl;

import java.rmi.RemoteException;

import service.ExecuteService;

public class ExecuteServiceImpl implements ExecuteService {
	/**
	 * 实现BF语言的编译
	 */
	@Override
	public String execute(String code, String param) throws RemoteException {					
		//初始化为零的数组，分配一定空间
		int[] stack = new int[1024];
		//指向该数组的指针
		int pointer = 0;
		//返回的结果
		String result = "";

		char[] codes = code.toCharArray();
		char[] params = param.toCharArray();
		int paramNum = 0;
		
		for(int sequence = 0; sequence < codes.length; sequence ++) {
			switch(codes[sequence]) {
				case '>' :
					pointer ++;
					break;
				case '<' :
					pointer --;
					break;
				case '+' :
					stack[pointer] ++;
					break;
				case '-' :
					stack[pointer] --;
					break;
				case '.' :
					if(stack[pointer] == 0) {
						//这是个回车
						result += "\r";
					}else {
						result += (char)stack[pointer];
					}
					break;
				case ',' :
					if(paramNum < params.length) {
						stack[pointer] = params[paramNum];
						paramNum ++;
					}					
					break;
				case '[' :
					if(stack[pointer] == 0) {
						int count = 0;
						//匹配括号
						while(true) {
							sequence ++;
							if(codes[sequence] == '[') {
								count ++;
							}else if(codes[sequence] == ']') {
								if(count == 0) {
									break;
								}else {
									count --;
								}
							}
							
							if(sequence >= codes.length) {
								return "Syntax error!";
							}
						}
					}
					break;
				case ']' :
					if(stack[pointer] != 0) {
						int count = 0;
						//匹配括号
						while(true) {
							sequence --;
							if(codes[sequence] == ']') {
								count ++;								
							}else if(codes[sequence] == '[') {
								if(count == 0) {
									break;
								}else {
									count --;
								}
							}
							
							if(sequence < 0) {
								return "Syntax error!";
							}
						}
					}
					break;
				default :
					break;
			}
		}			
		return result;
	}
}

package com.rex.app.onmyoji.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ResoureWriter {
	private FileOutputStream os;
	private OutputStreamWriter osw;
	private static volatile ResoureWriter instance;

	private ResoureWriter() {
		try {
			String fileName = "yhfa.txt";
			os = new FileOutputStream("./" + fileName, false);
			osw = new OutputStreamWriter(os, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("输入文件打开失败");
		}
	}
	
	public static ResoureWriter getInstance() {
		if(null == instance) {
			synchronized(ResoureWriter.class){
				if (null == instance) {
					instance = new ResoureWriter();
				} 
			}
		}
		return instance;
	}

	public synchronized static void close() {
		if (null == instance) {
			return;
		} else {
			try {
				instance.os.close();
				instance.osw.close();
				System.out.println("文件已关闭");
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("文件关闭失败");
			}
			instance = null;
		}
	}
	
	/**
	 * 输出文件
	 */
	public void writeReport(StringBuffer content) {
		try {
			osw.write(content.toString());
			osw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				os.close();
				osw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}

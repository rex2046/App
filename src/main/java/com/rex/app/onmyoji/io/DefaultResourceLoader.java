package com.rex.app.onmyoji.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

import com.rex.app.onmyoji.nigimitama.NgAbAddition;
import com.rex.app.onmyoji.nigimitama.Nigimitama;


public class DefaultResourceLoader {
	private static volatile List<ArrayList<Nigimitama>> ngAllList;
	
	public static void main(String[] args) {
		DefaultResourceLoader.load();
	}
	
	public static List<ArrayList<Nigimitama>> load() {
		if (ngAllList == null) {
			synchronized (DefaultResourceLoader.class) {
					ngAllList = new ArrayList<ArrayList<Nigimitama>>();
					for (int i = 0; i < 6; i++) {
						ngAllList.add(new ArrayList<Nigimitama>());
					}
					File[] ngFileList = getNgInputFile();
				try {
					for (File ngFile : ngFileList) {
						doLoad(ngFile, ngAllList);
					}
					System.out.println("处理成功！");
				} catch (Exception e) {
					// System.out.println(e.getMessage());
					e.printStackTrace();
					System.out.println("处理失败！");
				}
			}
		}
		return ngAllList;
	}
	
	private static File[] getNgInputFile() {
		// File fileDir = new File("./"); // 默认当前目录
		ClassLoader clzld = DefaultResourceLoader.class.getClassLoader();
		File fileDir = new File(clzld.getResource("").getPath()); // 类加载路径
		System.out.println(fileDir);
		String regex = new String();
		regex = "^yh.*\\.xlsx";
		Pattern pattern = Pattern.compile(regex);

		ArrayList<File> fileList = new ArrayList<File>();
		for (File file : fileDir.listFiles()) {
			Matcher fMatcher = pattern.matcher(file.getName());
			if (fMatcher.matches()) {
				System.out.println("找到文件：" + file.getName());
				fileList.add(file);
			}
		}

		if (0 == fileList.size()) {
			RuntimeException ex = new RuntimeException("未找到原excel日志文件");
			throw ex;
		}

		File[] files = new File[fileList.size()];
		fileList.toArray(files);
		return files;
	}
    
	private static void doLoad(File excelReport, List<ArrayList<Nigimitama>> ngAllList) throws IOException {
		FileInputStream is = null;
		XSSFWorkbook wb = null;
		try {
			// 按行处理文件
			is = new FileInputStream(excelReport);
			wb = new XSSFWorkbook(is);
			XSSFSheet sh = wb.getSheetAt(0); // 取默认第1页
			// XSSFSheet sh = wb.getSheet("御魂");
			XSSFRow ro = null;
			XSSFCell cell = null;

			for (int i = 1;sh.getRow(i) != null; i++) { // 略过表头，从报表第2行开始处理
				ro = sh.getRow(i);
				Nigimitama ng = new Nigimitama(); // 御魂
				BeanWrapper ngbw = new BeanWrapperImpl(ng);
				NgAbAddition ngab = new NgAbAddition();
				BeanWrapper ngabbw = new BeanWrapperImpl(ngab); // 御魂属性
				ng.setNgAbAddition(ngab);

				for (int j = 0; j < 16; j++) {
					cell = ro.getCell(j);
					if (j < 5) { // 设置御魂
						ngbw.setPropertyValue(
								getCellStrValue(sh.getRow(0).getCell(j)).trim(),
								getCellStrValue(cell).trim());
					} else { // 设置御魂属性
						ngabbw.setPropertyValue(
								getCellStrValue(sh.getRow(0).getCell(j)).trim(),
								StringUtils.hasText(getCellStrValue(cell)) ? getCellStrValue(
										cell).trim()
										: Float.valueOf("0"));
					}
				}

				ngAllList.get(Integer.valueOf(ng.getPosition()) - 1 /*list序号和御魂位置差1*/).add(ng);
				System.out.println("读入第" + ng.getSeqNo() + "号御魂，合计" + i + "个");
			}
		}  finally {
			if(wb != null) {
				wb.close();
			}
			if (is != null) {
				is.close();
			}
		}
	}
	
	 /**
     * 返回单元格的String值
     */
    static private String getCellStrValue(XSSFCell cell) {
    	if(null == cell) {
    		return "";
    	}
    	else if(cell.getCellType() == Cell.CELL_TYPE_STRING) {
    		return cell.getStringCellValue();
    	}
    	else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
    		return cell.getCellFormula();
    	}
    	else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
    		if(HSSFDateUtil.isCellDateFormatted(cell)) { // 日期格式
    			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    			return formatter.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue())).toString();
    		}
    		
    		double dv= cell.getNumericCellValue();
    		if (dv%1 == 0) { // 整数
    			return String.valueOf(new DecimalFormat("#").format(dv));
    		}
    		else { // 非整数
    			return String.valueOf(new DecimalFormat("0.00").format(dv));
    		}
    	}
    	else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
    		return String.valueOf(cell.getBooleanCellValue());
    	}
    	
    	return cell.toString();
    	
    }
}

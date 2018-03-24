package com.rex.app.onmyoji.nigimitama;

import java.util.List;

public class SuiteNg {
	private List<Nigimitama> ngAlocateList; // 御魂方案
	private String stType; // 四件套类型
	private NgAbAddition stAbAddition; // 属性加成
	
	public List<Nigimitama> getNgAlocateList() {
		return ngAlocateList;
	}
	public void setNgAlocateList(List<Nigimitama> ngAlocateList) {
		this.ngAlocateList = ngAlocateList;
	}
	public String getStType() {
		return stType;
	}
	public void setStType(String stType) {
		this.stType = stType;
	}
	public NgAbAddition getStAbAddition() {
		return stAbAddition;
	}
	public void setStAbAddition(NgAbAddition stAbAddition) {
		this.stAbAddition = stAbAddition;
	}
}

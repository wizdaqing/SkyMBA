package com.mba.dao;

import java.util.ArrayList;
import java.util.HashMap;

public class PrepareData {
	public  static ArrayList getPreparedData(){
		ArrayList list=new ArrayList();
		HashMap map1=new HashMap();
		map1.put("title", "跳水节目");
		map1.put("abs", "跳水节目在今年盛行，到夏天可能更火");
		map1.put("keyword1", "跳水");
		map1.put("keyword2", "女明星");
		
		HashMap map2=new HashMap();
		map2.put("title", "百里画廊");
		map2.put("abs", "听说百里画廊很美，想要骑车去看看呢");
		map2.put("keyword1", "百里画廊");
		map2.put("keyword2", "美");
		list.add(map1);
		list.add(map2);		
		return list;
	}
	
	public static ArrayList getDocument(){
		ArrayList  list =new ArrayList();
		list.add("跳水内容");
		list.add("百里画廊");
		return list;
	}
}

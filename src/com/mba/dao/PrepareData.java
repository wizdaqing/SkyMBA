package com.mba.dao;

import java.util.ArrayList;
import java.util.HashMap;

public class PrepareData {
	public  static ArrayList getPreparedData(){
		ArrayList list=new ArrayList();
		HashMap map1=new HashMap();
		map1.put("title", "��ˮ��Ŀ");
		map1.put("abs", "��ˮ��Ŀ�ڽ���ʢ�У���������ܸ���");
		map1.put("keyword1", "��ˮ");
		map1.put("keyword2", "Ů����");
		
		HashMap map2=new HashMap();
		map2.put("title", "���ﻭ��");
		map2.put("abs", "��˵���ﻭ�Ⱥ�������Ҫ�ﳵȥ������");
		map2.put("keyword1", "���ﻭ��");
		map2.put("keyword2", "��");
		list.add(map1);
		list.add(map2);		
		return list;
	}
	
	public static ArrayList getDocument(){
		ArrayList  list =new ArrayList();
		list.add("��ˮ����");
		list.add("���ﻭ��");
		return list;
	}
}

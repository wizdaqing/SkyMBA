package com.mba.dao;

import java.util.ArrayList;
import java.util.HashMap;

public class GradeDao {
	static public ArrayList<HashMap<String, String>> getGrades() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		String[] grades = { "0", "0", "0", "0", "0", "0", "0", "0" };
		String[] catoNames = { "管理", "创业", "营销", "人力", "销售", "金融", "财务", "案例" };
		HashMap<String, String> map;

		for (int j = 0; j < 8; j++) {
			map = new HashMap<String, String>();
			map.put("catoname", catoNames[j]);
			map.put("grade", grades[j]);
			list.add(map);
		}

		return list;

	}
}

package com.mba.view;

public class ReadClasses {
	private static String[]classes={"��Ͱ����","ϲ���ּ�","��;����","�򳵹���","���֮��","˯ǰ����"};
 public static String getReadClasses(int id){
	 return classes[id-1];
 }
 
 public static int getVelLimit(int id){
	 if(id%2==0){
		 return 40;//200��ÿ����
	 }
	 else {
		 return 60;//333��ÿ����
	 }
 }
 
 public static int getCredit(int id){
	 int credit=0;
	 switch (id) {
	case 0:
		 
	case 1:
	
	case 3:
		credit=6;break;
	case 2:
	case 5:
		credit=10;break;
	case 4:
		credit=20;break;
	default:
		break;
	}
  return credit;
 
 }
}

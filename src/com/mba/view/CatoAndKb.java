package com.mba.view;

public class CatoAndKb {
	static String[] tagIds = { "0a78b0d5-da7b-45be-bb66-8c62ef4fa7b4",
			"566f1469-80b2-4b68-a6b0-adfca48bd6f3",
			"8c642f8c-584b-4b16-b315-e537b6e4c429",
			"c721f718-0e2d-4a92-89a7-916e72690cd8",
			"c01f51b9-c489-49be-bd7d-412fe041593c",
			"80ca4ee9-eb75-48d4-9051-a78469f1970a",
			"d5f53fcb-5688-4667-a68d-d8603a644d37",
			"cb5288ca-9b46-4645-8cef-d68fea08e730",
			"4d0dca24-a720-44bd-97fe-a22c764b640a",
			"bfa489bb-d7cd-4947-b61c-7e0892c2d19f",
			"f544fa53-b201-4cd4-9119-115837af5071" };
	static String[] catoNames = { "管理", "创业", "营销", "人力", "销售", "金融", "财务",
			"案例", "养生", "运动", "品酒" };
	static String[] kbGuids={"44bdd495-2e90-4581-afb2-b154809c4cd8","008e0ca8-7f8d-44db-aa6a-9db4ce21c3cc"};
  public static String getCatoByTagid(String tagid){
	  		for(int j=0;j<tagIds.length;j++){
	  			if(tagid.equals(tagIds[j])){
	  				return catoNames[j];
	  			}
	  		}
			return tagid;
	  }
  
  public static String getKbguidByTagid(String tagid){
	   for(int j=0;j<tagIds.length;j++){
		   if(tagid.equals(tagIds[j])&&j<8){
			   return kbGuids[0];
		   }else if(tagid.equals(tagIds[j])&&j>=8){
			   return kbGuids[1];
		   }
	   }
	   return null;
  }
  
  public static String getKbguidById(int id){
	  return kbGuids[id];
  }
}

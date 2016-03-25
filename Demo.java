package com.svail.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.svail.population_mobility.CountyPopulation;
import com.svail.population_mobility.JsonData;
import com.svail.population_mobility.PopulationStatistics;
import com.svail.util.FileTool;
import com.svail.util.Tool;

public class Demo {
	public static String code;
	public static String countyname;
	public static String countyLN;
	public static String countyLA;
	public static String countyCoor;
	public static String folder = "D:/人口数据/";
	public String PostCode;
	public String Code;
	public String to;
	public String from;
	public String amounts;

	public static void main(String argv[]) throws Exception {

		// fileCut(710662,"D:/人口数据/2-postCode.txt");
		// System.out.println("导入每个区划的数据：");
		// setCounty();

		// System.out.println("开始生成个人postCode数据：");
		// for(int i=1;i<=88;i++){
		// String f=folder+i+".txt";
		// ClassifyStatistic(f);
		// }
		// countTo("D:/zhouxiang/人口数据/宾馆数据/人口统计/CodeResult.txt","D:/人口数据/3级数据-统计人口流入流出数据/countFlowout.txt");
		// countAmount("D:/人口数据/countFlowout-tidy.txt");
		// productJson("D:/人口数据/countFlowin-tidy-countAmounts.txt");
		// delectMistake("D:/Crawldata_BeiJing/fang/resold/0314/fang_resold0108_zhoubian/fang_resold0108_zhoubian.txt");
		// Check_countFlowout_tidy("D:/人口数据/3级数据-统计人口流入流出数据/countFlowout-tidy.txt");

		// Check_countFlowout("D:/人口数据/3级数据-统计人口流入流出数据/countFlowout.txt");
		CompareAmouts("D:/人口数据/3级数据-统计人口流入流出数据/key2.txt","D:/人口数据/3级数据-统计人口流入流出数据/countFlowout-tidy-Check.txt");
		System.out.println("OK!");
	}
/**
 * 比较两个文件中的记录是否相同，并且将相同的记录写下来，从而找出两个文件是否存在不相同的记录
 * @param folder1
 * @param folder2
 */
	public static void CompareAmouts(String folder1, String folder2) {
		Map<String, Integer> map1 = new HashMap<String, Integer>();
		Map<String, Integer> map2 = new HashMap<String, Integer>();
		String poi1 = "";
		String poi2 = "";
		Vector<String> Pois1 = FileTool.Load(folder1, "utf-8");
		for (int a1 = 0; a1 < Pois1.size(); a1++) {
			poi1 = Pois1.elementAt(a1);
			String[] arr1 = poi1.split(":");
			map1.put(arr1[0], Integer.parseInt(arr1[1]));
		}
		Vector<String> Pois2 = FileTool.Load(folder2, "utf-8");
		for (int a2 = 0; a2 < Pois2.size(); a2++) {
			poi2 = Pois2.elementAt(a2);
			String[] arr2 = poi2.split(":");
			if(map2.get(arr2[0])!=null){
				int s=map2.get(arr2[0]);
				System.out.println(arr2[0]+":"+s+arr2[1]);
				map2.put(arr2[0], Integer.parseInt(s+arr2[1]));
			}else{
				map2.put(arr2[0], Integer.parseInt(arr2[1]));
			}
		    map2.put(arr2[0], Integer.parseInt(arr2[1]));
		}
			Iterator<String> it1 = map1.keySet().iterator();
			System.out.println("map1.size()="+map1.size());
			while (it1.hasNext()) {

				String key1;
				int value1;

				key1 = (String) it1.next();
				value1 = map1.get(key1);

				//System.out.println("map1-" + key1 + ":" + value1);
				Iterator<String> it2 = map2.keySet().iterator();
				System.out.println("map2.size()="+map2.size());
				while (it2.hasNext()) {
					String key2;
					int value2;
					key2 = (String) it2.next();
					value2 = map2.get(key2);
					//System.out.println("map2-" + key2 + ":" + value2);
					if ((key1.equals(key2)) && (value1 == value2)) {
						//FileTool.Dump(key1 + ":" + value1, "D:/人口数据/3级数据-统计人口流入流出数据/key1=key2.txt", "utf-8");
						//FileTool.Dump(key2 + ":" + value2, "D:/人口数据/3级数据-统计人口流入流出数据/key2.txt", "utf-8");
						it2.remove();
						break;
					}else if((key1.equals(key2)) && (value1 != value2)){
						System.out.println("map1-" + key1 + ":" + value1);
						System.out.println("map2-" + key2 + ":" + value2);
						break;
                     }
				}
               

			}
		}

	/**
	 * 通过检查countFlowout.txt文件中每个区县的poi条数来找出多出的poi
	 */
	public static void Check_countFlowout(String folder) {
		String poi = "";
		setCounty();
		Vector<String> Pois = FileTool.Load(folder, "utf-8");
		for (int a = 0; a < Pois.size(); a++) {
			poi = Pois.elementAt(a);
			String from = Tool.getStrByKey(poi, "<from>", "</from>", "</from>");
			for (int i = 0; i < county.size(); i++) {
				if (from.equals(county.get(i).code)) {
					county.get(i).setPoiamounts(1);
				}
			}
		}
		int acount = 0;
		for (int i = 0; i < county.size(); i++) {
			System.out.println(county.get(i).poiamounts);

			for (int k = 0; k < county.get(i).poiamounts.size(); k++) {
				acount += county.get(i).poiamounts.get(k);
			}
			System.out.println(county.get(i).code + ":" + acount);
			FileTool.Dump(county.get(i).code + ":" + acount, folder.replace(".txt", "") + "-Check.txt", "utf-8");
			acount = 0;
		}
	}

	/**
	 * 通过检查-tidy.txt文件中每个区县的poi条数来找出多出的poi
	 */
	public static void Check_countFlowout_tidy(String folder) {
		String poi = "";
		int count = 1;
		Vector<String> Pois = FileTool.Load(folder, "utf-8");
		for (int a = 0; a < Pois.size(); a++) {
			poi = Pois.elementAt(a);
			String from = Tool.getStrByKey(poi, "<from>", "</from>", "</from>");
			String beforefrom = "";
			if (a > 0) {
				beforefrom = Tool.getStrByKey(Pois.elementAt(a - 1), "<from>", "</from>", "</from>");
				if (from.equals(beforefrom)) {
					count++;
				} else {
					System.out.println(beforefrom + ":" + count);
					FileTool.Dump(beforefrom + ":" + count, folder.replace(".txt", "") + "-Check.txt", "utf-8");
					count = 1;
				}
			}
		}
	}

	public static void delectMistake(String folder) {
		try {
			File file = new File(folder);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader reader = null;
			String tempString = null;

			reader = new BufferedReader(isr);
			while ((tempString = reader.readLine()) != null) {
				if (tempString.indexOf("<PRICE>") != -1) {
					FileTool.Dump(tempString.replace(" ", "").trim(), folder.replace(".txt", "") + "-tidy.txt",
							"utf-8");
				} else {
					FileTool.Dump(tempString.replace(" ", "").trim(), folder.replace(".txt", "") + "-mistake.txt",
							"utf-8");
				}

			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Demo(String line) {

		if (line.indexOf("<from>") != -1)
			from = Tool.getStrByKey(line, "<from>", "</from>", "</from>");
		if (line.indexOf("<to>") != -1)
			to = Tool.getStrByKey(line, "<to>", "</to>", "</to>");
		if (line.indexOf("<amounts>") != -1)
			amounts = Tool.getStrByKey(line, "<amounts>", "</amounts>", "</amounts>");

	}

	public static void productJson(String folder) {

		JSONArray jsonArr = new JSONArray();// json格式的数组

		try {
			File file = new File(folder);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader reader = null;
			String tempString = null;

			reader = new BufferedReader(isr);
			while ((tempString = reader.readLine()) != null) {
				Demo demo = new Demo(tempString);
				JSONObject jsonObjArr = new JSONObject();

				jsonObjArr.put("from", demo.from);
				jsonObjArr.put("to", demo.to);
				jsonObjArr.put("amounts", demo.amounts);
				jsonArr.put(jsonObjArr);
			}

			// 将json格式的数据放到json格式的数组里

			// jsonObj.put("Points", jsonObjArr);//再将这个json格式的的数组放到最终的json对象中。

			// System.out.println(jsonArr);
			System.out.println("开始写入txt中");
			FileTool.Dump(jsonArr.toString(), folder.replace(".txt", "") + "-Json .txt", "utf-8");
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void countAmount(String folder) {
		String poi = "";
		try {
			Vector<String> Pois = FileTool.Load(folder, "utf-8");
			Map<String, Integer> map = new HashMap<String, Integer>();
			for (int a = 0; a < Pois.size(); a++) {
				int temp = 0;
				poi = Pois.elementAt(a);
				String from = Tool.getStrByKey(poi, "<from>", "</from>", "</from>");
				String to = Tool.getStrByKey(poi, "<to>", "</to>", "</to>");
				int amount = Integer.parseInt(Tool.getStrByKey(poi, "<amount>", "</amount>", "</amount>"));
				String index = "";
				if (a == 0) {
					map.put(to, amount);
				} else {
					index = Tool.getStrByKey(Pois.elementAt(a - 1), "<from>", "</from>", "</from>");
					if (from.equals(index)) {
						// 对每个区域逐个统计
						int count = 0;
						for (int b = 0; b < a; b++) {
							String probe = Tool.getStrByKey(Pois.elementAt(b), "<to>", "</to>", "</to>");
							if (to.equals(probe)) {
								if (map.get(probe) != null) {
									int s = map.get(probe);
									map.put(probe, s + amount);
									break;
								} else {
									map.put(to, amount);
									break;
								}
							} else {
								count++;
							}
						}
						if (count == a) {
							map.put(to, amount);
						}
						int s = Pois.size();
						if ((a + 1) == s) {
							for (Map.Entry<String, Integer> entry : map.entrySet()) {
								String key = entry.getKey().toString();
								String value = entry.getValue().toString();
								String str = "<from>" + index + "</from>" + "<to>" + key + "</to>" + "<amounts>" + value
										+ "</amounts>";
								// System.out.println(str);
								FileTool.Dump(str, folder.replace(".txt", "") + "-countAmounts.txt", "utf-8");
							}
						}

					} else {
						for (Map.Entry<String, Integer> entry : map.entrySet()) {
							String key = entry.getKey().toString();
							String value = entry.getValue().toString();
							String str = "<from>" + index + "</from>" + "<to>" + key + "</to>" + "<amounts>" + value
									+ "</amounts>";
							// System.out.println(str);
							FileTool.Dump(str, folder.replace(".txt", "") + "-countAmounts.txt", "utf-8");
						}
						map.clear();
						map.put(to, amount);
					}

				}
			}
		} catch (NullPointerException e) {
			System.out.println(e.getMessage());
			FileTool.Dump(poi, folder.replace(".txt", "") + "-exception.txt", "utf-8");
		}

	}

	public static void countTo(String codefolder, String countfoder) {
		try {
			File file = new File(codefolder);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader reader = null;
			String tempString = null;
			reader = new BufferedReader(isr);
			int counts1 = 0;
			int counts2 = 0;
			while ((tempString = reader.readLine()) != null) {
				counts2++;
				Vector<String> countfile = FileTool.Load(countfoder, "utf-8");
				int counts3 = countfile.size();
				for (int i = 0; i < countfile.size(); i++) {
					String poi = countfile.elementAt(i);
					Demo demo = new Demo(poi);
					String code = Tool.getStrByKey(tempString, "<Code>", "</Code>", "</Code>");
					if (code.equals(demo.from)) {
						// System.out.println("line-"+(i+1)+": "+poi);
						counts1++;
						FileTool.Dump("line-" + (i + 1) + ": " + poi,
								countfoder.replace(".txt", "") + "-tidy-tonight.txt", "utf-8");
					}

				}

			}
			System.out.println("共有" + counts1 + "条记录被写下！");
			System.out.println("共有" + counts2 + "个code被校对！");
			reader.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 将每个区县定义成一个county
	 */
	public static ArrayList<CountyPopulation> county = new ArrayList<CountyPopulation>();

	/**
	 * 对每个county进行数据的填充
	 */
	public static void addCountyPopulation(CountyPopulation cp) {
		county.add(cp);

	}

	public static void setCounty() {
		try {
			// 设置每个区县的区县代码、区县名字以及坐标
			File file = new File("D:/zhouxiang/人口数据/宾馆数据/人口统计/CodeResult.txt");
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader reader = null;
			String tempString = null;

			reader = new BufferedReader(isr);
			while ((tempString = reader.readLine()) != null) {
				code = Tool.getStrByKey(tempString, "<Code>", "</Code>", "</Code>");
				countyname = Tool.getStrByKey(tempString, "<CodeAddr>", "</CodeAddr>", "</CodeAddr>");
				CountyPopulation cp = new CountyPopulation();
				// System.out.println(code);
				cp.setCode(code);
				// System.out.println(countyname);
				cp.setCountyname(countyname);
				addCountyPopulation(cp);
			}
			reader.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

	}

	public static void ClassifyStatistic(String folder) throws IOException {

		// 将人口的通讯地址的代码分配到各个区县
		File file1 = new File(folder);
		FileInputStream fis1 = new FileInputStream(file1);
		InputStreamReader isr1 = new InputStreamReader(fis1, "UTF-8");
		BufferedReader reader1 = null;
		String tempString1 = null;

		reader1 = new BufferedReader(isr1);
		while ((tempString1 = reader1.readLine()) != null) {

			if (tempString1.indexOf("<PostReg>") != -1) {
				String PostReg = Tool.getStrByKey(tempString1, "<PostReg>", "</PostReg>", "</PostReg>");
				String[] PostAddr = PostReg.split(",");
				String addr = (PostAddr[0] + PostAddr[1] + PostAddr[2]).replace("null", "").replace("苏州工业园区", "吴中区")
						.replace("汕尾市汕尾市", "汕尾市").replace("黑河市黑河市", "黑河市").replace("重庆市潼南县", "重庆市潼南区")
						.replace("重庆市县", "重庆市").replace("包头市白云区", "包头市白云鄂博矿区").replace("重庆市荣昌县", "重庆市荣昌区")
						.replace("桂林市荔浦县", "桂林市荔蒲县").replace("承德市营子矿区", "承德市鹰手营子矿区").replace("乌鲁木齐市东山区", "乌鲁木齐市米东区")
						.replace("甘肃省临泽县", "甘肃省张掖市临泽县").replace("固原市海原县", "中卫市海原县").replace("吴忠市红寺堡", "吴忠市红寺堡开发区")
						.replace("广西壮族自治区富川瑶族自治县", "广西壮族自治区贺州市富川瑶族自治县").replace("甘肃省肃州区", "甘肃省酒泉市肃州区")
						.replace("甘肃省静宁县", "甘肃省平凉市静宁县").replace("甘肃省灵台县", "甘肃省平凉市灵台县").replace("", "").replace("", "")
						.replace("", "");

				int count = 0;
				String poi = "";
				for (int i = 0; i < county.size(); i++) {

					if (addr.equals(county.get(i).countyname)) {
						poi = "<PostCode>" + county.get(i).code + "</PostCode>" + tempString1;
						// county.get(i).setpostPois(poi);
						FileTool.Dump(poi, folder.replace(".txt", "") + "-postCode.txt", "utf-8");
						break;
					} else {
						count++;
					}
				}

				if (count == county.size()) {
					FileTool.Dump(tempString1, "D:/人口数据/找不到对应行政区划数据.txt", "utf-8");
				}

			}

		}

		reader1.close();
	}

	public static void fileCut(int n, String folder) {
		try {
			File file = new File(folder);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader reader = null;
			String rs = null;
			reader = new BufferedReader(isr);
			System.out.println("文件分类开始：");
			int count = 0;
			while ((rs = reader.readLine()) != null) {
				count++;
				if (count < n) {
					FileTool.Dump(rs, folder.replace(".txt", "") + "_1.txt", "utf-8");

				} else {
					FileTool.Dump(rs, folder.replace(".txt", "") + "_2.txt", "utf-8");
				}
			}
			System.out.println("文件分类结束！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

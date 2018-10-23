/**
 * @author wuzy
 * @create 2018-10-17 14:35
 */
package com.suncreate.bowei.fenci;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.huaban.analysis.jieba.SegToken;

public class AddrTrans {
	private JiebaSegmenter segmenter = new JiebaSegmenter();

	Map<String, String> city_name2code = new HashMap<String, String>();
	Map<String, String> city_code2name = new HashMap<String, String>();
	Map<String,String> capArray = new HashMap<String, String>();
	
	public AddrTrans(String csvFile) throws Exception{
		//csv文件路径，貌似不能使用相对路径
		//String csvFile = "/home/wuzy/bowei_workspace/addr_analysis/city_code.csv";		
		//读取
		CsvReader reader = new CsvReader(csvFile,',',Charset.forName("UTF-8"));
		
		while(reader.readRecord()) {
			//生成遍历文件
			String code = reader.getRawRecord().substring(0, reader.getRawRecord().indexOf(","));
			String name = reader.getRawRecord().substring(reader.getRawRecord().indexOf(",")+1);			
			if(name.indexOf("|")>=0) {
				city_name2code.put(name.substring(name.indexOf("|")+1),code);
				name = name.substring(0,name.indexOf("|"));					
			}
			city_name2code.put(name,code);
			city_code2name.put(code,name);
			
			//用于获取所有的省会，有6个特殊地区
			String Headcode = code.substring(0, 2);
			String endCode = code.substring(2);
			//四个直辖市没有省会,两个特区,以市代省会。台湾没有收录市的内容
			if(Headcode.equals("11")||Headcode.equals("12")||Headcode.equals("31")||Headcode.equals("50")||Headcode.equals("81")||Headcode.equals("71")||Headcode.equals("82")) {
				if(endCode.equals("0000")) {
					capArray.put(name, code);
				}
			}else if(endCode.equals("0100")){
				capArray.put(name, code);
			}
		}	
	}

	public String code(String name) {
		if(city_name2code.get(name)!=null) {
			return city_name2code.get(name);
		}
		return "-1";		
	}
	
	public String name(String code) {
		if(city_code2name.get(code)!=null) {
			return city_code2name.get(code);
		}
		return "-1";			
	}
	
	public String getProvinceCode(String city) {
		String city_code = code(city);
		if(!city_code.equals("-1")) {
			String pro_code = city_code.substring(0, 2)+"0000";					
			return pro_code;
		}
		return "-1";		
	}
	
	public String getProCaptical(String city){
		String city_code = code(city);
		if(!city_code.equals("-1")) {
			String pro_code = city_code.substring(0, 2);
			//除去四个直辖市没有省会
			if(pro_code.equals("11")||pro_code.equals("12")||pro_code.equals("31")||pro_code.equals("50")) {
				pro_code += "0000";
			}else {
				pro_code += "0100";
			}			
			return pro_code;
		}
		return "-1";
	}
	
	public String where(String addr){			
		List<SegToken> list = segmenter.process(addr, SegMode.INDEX);
		for(SegToken line : list) {
			String changeAddr = line.toString().substring(1, line.toString().indexOf(","));				
			if(city_name2code.get(changeAddr)!=null) {
				return city_name2code.get(changeAddr);
			}			
		}	
		return "-1";
	}
	
	//获取输入地址的省、省会
	public Map<String, String> getProAndCapByAddr(String addr) {
		Map<String, String> result = new HashMap<String, String>();
		//获取输入地区的code值与name
		String addr_code = where(addr);
		String addr_name = name(addr_code);
		if(!addr_code.equals("-1")) {
			result.put("addrCode", addr_code);
			result.put("addrName", addr_name);
		}
		//计算省份的code值与name
		String pro_code = getProvinceCode(addr_name);
		String pro_name = name(pro_code);
		if(!addr_code.equals("-1")) {
			result.put("proCode", pro_code);
			result.put("proName", pro_name);
		}
		//获取省会的code与name
		String cap_code = getProCaptical(addr_name);
		String cap_name = name(cap_code);
		if(!addr_code.equals("-1")) {
			result.put("capCode", cap_code);
			result.put("capName", cap_name);
		}				
		return result;
	}
	
	//获取所有省会
	public Map<String,String> getAllCap(){	
		return capArray;		
	}
}

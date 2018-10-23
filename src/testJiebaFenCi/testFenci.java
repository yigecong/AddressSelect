package testJiebaFenCi;

import java.util.Map;

import com.suncreate.bowei.fenci.AddrTrans;

public class testFenci {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("开始......");
		try {
			AddrTrans add = new AddrTrans("/home/wuzy/bowei_workspace/addr_analysis/city_code.csv");
			String addr = "赣州";
			Map<String, String> result = add.getProAndCapByAddr(addr);
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

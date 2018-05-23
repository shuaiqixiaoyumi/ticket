package cn.com.smart.web.utils;

import java.security.Key;
import java.util.UUID;

public class Test {

	@org.junit.Test
	public void Test() {
		try {
			String a = "我很好";
			String b=new String(a.getBytes("GBK"),"ISO-8859-1");
			System.out.println(b);
			String c=new String(b.getBytes("ISO-8859-1"),"utf-8");
			System.out.println(c);
			
			String d = "æå¾å¥½";
			
//			String keyFile = "D://usr/11104200.key";
//
//			byte[] keyByte = EncryptUtils.getBytesFromFile(keyFile);
//
//			Key key = EncryptUtils.restorePrivateKey(keyByte);
//			String data = EncryptUtils.encryptWithEncodeBase64UTF8(
//
//					"{}", key);
//
//          String input = "{\"eid\":\"11104200\",\"nonce\", "+UUID.randomUUID().toString()+",\"data\":"+data+"}";
//          String retuest = HttpUtil.doPost("https://www.yunzhijia.com/openaccess/input/dept/getall", input);
//          System.out.println(retuest);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}

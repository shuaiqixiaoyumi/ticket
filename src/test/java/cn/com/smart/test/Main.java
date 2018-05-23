package cn.com.smart.test;
import java.security.Key;

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;

import com.mashape.unirest.http.JsonNode;

import com.mashape.unirest.http.Unirest;

import com.mashape.unirest.http.exceptions.UnirestException;


public class Main {
	private final static String yzj_base_url = "https://www.yunzhijia.com";

	// private final static String

	// person_getall_url=yzj_base_url+"/openaccess/input/person/getall ";

	private final static String org_getall_url = yzj_base_url
			
			+ "/openaccess/input/person/get";

//			+ "/openaccess/input/person/getall ";



	public static void main(String[] args) throws Exception {
		

		try {
			boolean hasperson = true;
			int i = 0;
			while(hasperson){
//				JsonNode jsonData = new JsonNode("{'begin':"+i+",'count':1000}"); // json data without encrypt
				JsonNode jsonData = new JsonNode("{'array':['13486171692']}"); // json data without encrypt

				String keyFile = "D://usr/11104200.key";

				byte[] keyByte = EncryptUtils.getBytesFromFile(keyFile);

				Key key = EncryptUtils.restorePrivateKey(keyByte);

System.out.println(EncryptUtils.encryptWithEncodeBase64UTF8(

									jsonData.toString(), key));

				HttpResponse<JsonNode> jsonResponse = Unirest

						.post(org_getall_url)

						.header("Content-Type", "application/x-www-form-urlencoded")

						.field("eid", "11104200")	
						.field("nonce", UUID.randomUUID().toString())

						.field("data",

								EncryptUtils.encryptWithEncodeBase64UTF8(

										jsonData.toString(), key)).asJson();

				System.out.println(jsonResponse.getBody().getObject().get("data"));
				JsonNode jn = jsonResponse.getBody();
				JSONArray js =  (JSONArray) jn.getObject().get("data");
				
				if(js.length() >0 ) {
					hasperson =true;
					i=i+1000;
				}else {
					hasperson = false;
				}
				break;
			}
			

		} catch (UnirestException e) {

			e.printStackTrace();

		}

	}


}

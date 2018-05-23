package cn.com.smart.test;

import java.net.URL;
import java.security.Key;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import cn.com.smart.init.config.InitSysConfig;



public class OrgTest {
	private static String eid = InitSysConfig.getInstance().getValue("eid");
	private static String group = InitSysConfig.getInstance().getValue("group");
	private static String keypath = InitSysConfig.getInstance().getValue("keypath");
	private static String yzj_base_url = InitSysConfig.getInstance().getValue("yzj_base_url");
	private static String org_getall_url = InitSysConfig.getInstance().getValue("org_getall_url");
	private static String user_getall_url = InitSysConfig.getInstance().getValue("user_getall_url");
	private static String path =  InitSysConfig.getInstance().getPath(keypath);
	
//	String jarPath = this.getClass().getClassLoader().getResource(keypath).getPath();

	// private final static String

	// person_getall_url=yzj_base_url+"/openaccess/input/person/getall ";

	

//			+ "/openaccess/input/person/getall ";


	
	public static void main(String[] args) throws Exception {

		try {

			JsonNode jsonData = new JsonNode("{}"); // json data without encrypt
			org_getall_url = yzj_base_url+org_getall_url;
			
			String keyFile =path;
			System.out.println(path);

			byte[] keyByte = EncryptUtils.getBytesFromFile(keyFile);

			Key key = EncryptUtils.restorePrivateKey(keyByte);



			HttpResponse<JsonNode> jsonResponse = Unirest

					.post(org_getall_url)

					.header("Content-Type", "application/x-www-form-urlencoded")

					.field("eid", eid)
					.field("nonce", UUID.randomUUID().toString())
					.field("data",

							EncryptUtils.encryptWithEncodeBase64UTF8(

									jsonData.toString(), key)).asJson();

			System.out.println(jsonResponse.getBody());
			JsonNode jn = jsonResponse.getBody();
			JSONArray js =  (JSONArray) jn.getObject().get("data");
			System.out.println(js);
			if(js.length() >0 ) {
				for(int i=3; i<js.length();i++) {
					JSONObject jb = js.getJSONObject(i);
					String name = jb.getString("name");
					String department = jb.getString("department");
					System.out.println(name +department );
					
					break;
				}
			}

		} catch (UnirestException e) {

			e.printStackTrace();

		}

	}
}

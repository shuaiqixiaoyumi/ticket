package cn.com.smart.flow.timer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mixsmart.utils.LoggerUtils;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.init.config.InitSysConfig;
import cn.com.smart.web.bean.entity.TNOrg;
import cn.com.smart.web.dao.impl.OrgDao;
import cn.com.smart.web.service.OrgService;
import cn.com.smart.web.utils.EncryptUtils;
/**
 * 
 * @author zhanglb
 *
 */
@Component
public class OrgAndUserTimer {
	
	@Autowired
	private OrgDao orgDao;
	@Autowired
	private OrgService orgService;

	private static final Logger logger = LoggerFactory.getLogger(OrgAndUserTimer.class);
	
	private static String eid = InitSysConfig.getInstance().getValue("eid");
	private static String group = "新光控股集团有限公司";
	private static String keypath = InitSysConfig.getInstance().getPath(InitSysConfig.getInstance().getValue("keypath"));
	private static String yzj_base_url = InitSysConfig.getInstance().getValue("yzj_base_url");
	private static String org_getall_url = InitSysConfig.getInstance().getValue("org_getall_url");
	private static String user_getall_url = InitSysConfig.getInstance().getValue("user_getall_url");
	
	public void run() {
		LoggerUtils.info(logger, "正在启动组织人员同步...");
		try {
//			synUser();
			synOrg();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 云之家同步组织
	 */
	@Test
	public void synOrg() throws Exception {
		try {

//			JsonNode dfsf = new JsonNode("{}");
			
			JSONObject jsonData = new JSONObject("{}"); // json data without encrypt
//			JSONObject jsonData = new JSONObject();
//			jsonData.put("eid", eid);
			byte[] keyByte = EncryptUtils.getBytesFromFile(keypath);
			
			Key key = EncryptUtils.restorePrivateKey(keyByte);
			
//			String body = "[{\"eid\":\""+eid+"\",\"noce\":\""+ UUID.randomUUID().toString()+"\",\"data\":\""+EncryptUtils.encryptWithEncodeBase64UTF8(
//					jsonData.toString(), key).toString()+"\"}]";
//			System.out.println(body);
			Map<String, Object> map = new HashMap<>();
			map.put("eid", eid);
			map.put("nonce", UUID.randomUUID().toString());
			map.put("data", EncryptUtils.encryptWithEncodeBase64UTF8(
									jsonData.toString(), key).toString());
			HttpResponse<InputStream> jsonResponse = Unirest

					.post(yzj_base_url
							+org_getall_url)

					.header("Content-Type", "application/x-www-form-urlencoded;Charset=UTF-8")
					.fields(map)
//					.body(body)
//					.field("eid", "11104200")
//					.field("nonce", UUID.randomUUID().toString())
//					.field("data",
//
//							EncryptUtils.encryptWithEncodeBase64UTF8(
//									jsonData.toString(), key).toString())
					.asBinary();

			InputStream jsonObject = jsonResponse.getBody();
			BufferedReader in = new BufferedReader(new InputStreamReader(jsonObject,"utf-8"));
			String s =in.readLine();
//			String s=new String(in.readLine().getBytes(), "utf-8");
//			JSONObject jn = jsonResponse.getBody().getObject();
//			JSONObject jn = new JSONObject(s); 
//			String s=new String( jsonResponse.getBody().getBytes(), "utf-8");
			JSONObject jn = new JSONObject(s); 
			System.out.println(jn);
			LoggerUtils.info(logger, "返回组织。。。"+jsonResponse.getBody());
			JSONArray js =  jn.getJSONArray("data");
			if(js.length() >0 ) {
				for(int i=0; i<js.length();i++) {
					JSONObject jb = js.getJSONObject(i);
					String name = jb.getString("name");
					String department = jb.getString("department");
					department = department.startsWith(group)?department:group+"\\"+department;
					System.out.println(name +department );
					String parentDeptId = "0";
					String []orgnizations = department.split("\\\\");
					String deptId = "0";
				    String seqNames ="";
				    String seqParantIds = "";
					for(String dept : orgnizations) {
						TNOrg tnOrg = orgDao.getDeptIdByName(dept,parentDeptId);
						if(tnOrg != null) {
				    		parentDeptId =tnOrg.getId();
				    		deptId = tnOrg.getId();
				    	}else {
			
				    		TNOrg entity = new TNOrg();
				    		entity.setName(dept);
				    		entity.setCode("");
				    		entity.setParentId(parentDeptId);
				    		if(parentDeptId == "0") {
				    			seqParantIds ="null";
				    			seqNames = dept;
				    			entity.setType("company");
				    		}else {
				    			seqParantIds =seqParantIds+"."+parentDeptId;
				    			seqNames = seqNames+">"+dept;
				    			entity.setType("department");
				    		}
				    		entity.setContactNumber("");
				    		entity.setContacts("");
				    		entity.setSeqNames(seqNames);
				    		entity.setSeqParentIds(seqParantIds);
				    		entity.setCreateTime(new Date());
				    		entity.setSortOrder(1);
				    		SmartResponse<String> smartResp = orgService.save(entity);
				    		deptId = smartResp.getData();
				    		
				    		parentDeptId = deptId;
				    	}
					}
				}
			}

		} catch (UnirestException e) {

			e.printStackTrace();

		}

	}
	
	public void synUser() {
		try {
			boolean hasperson = true;
			int i = 0;
			while(hasperson){
				JsonNode jsonData = new JsonNode("{'begin':"+i+",'count':1000}"); // json data without encrypt

				byte[] keyByte = EncryptUtils.getBytesFromFile(keypath);

				Key key = EncryptUtils.restorePrivateKey(keyByte);

				HttpResponse<InputStream> jsonResponse = Unirest

						.post(yzj_base_url+user_getall_url)

						.header("Content-Type", "application/x-www-form-urlencoded")

						.field("eid", eid)	
						.field("nonce", UUID.randomUUID().toString())

						.field("data",

								EncryptUtils.encryptWithEncodeBase64UTF8(

										jsonData.toString(), key)).asBinary();
				InputStream jsonObject = jsonResponse.getBody();
				BufferedReader in = new BufferedReader(new InputStreamReader(jsonObject,"utf-8"));
				String s =in.readLine();
				JSONObject jn = new JSONObject(s); 
				JSONArray js =  jn.getJSONArray("data");
				if(js.length() >0 ) {
					hasperson =true;
					i=i+1000;
				}else {
					hasperson = false;
				}
				
			}
			

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
}

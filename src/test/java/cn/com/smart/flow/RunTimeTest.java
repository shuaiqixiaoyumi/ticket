package cn.com.smart.flow;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.mixsmart.utils.LoggerUtils;


import cn.com.smart.utils.DbBase;
import cn.com.smart.web.bean.entity.TNUser;
import cn.com.smart.web.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml", "classpath:applicationContext-snaker.xml"})
//@ContextConfiguration(locations={"classpath:applicationContext.xml", "classpath:applicationContext-snaker.xml","classpath:spring-web-config.xml"})
@WebAppConfiguration
public class RunTimeTest {
	private static final Logger logger = LoggerFactory.getLogger(RunTimeTest.class);
	DbBase db = new DbBase();
	@Autowired
	private UserService userService ;
	@Test
	@Scheduled(cron = "0 30 17 * * ?") 
	public void run() {
		LoggerUtils.info(logger, "正在启动一线值班人员调整...");
		String sql = "select * from t_n_user where org_id = (select id from t_n_org where name = '信息中心' ) order by sort_order";
		TNUser tnUser1 = userService.getDao().queryUser("support1");
		TNUser tnUser2 = userService.getDao().queryUser("support2");
		String support1FullName = tnUser1.getFullName();
		String support2FullName = tnUser2.getFullName();
		List<Map<String, Object>> list = db.queryForList(sql);
		int i = 0;
		for(Map<String, Object> map : list) {
			i++;
			String fullName = map.get("full_name")+"";
			if(fullName.equals(support2FullName)) {
				support1FullName = fullName;
				String support1Email = map.get("email")+"";
				String phone1 = map.get("mobile_no")+"";
				tnUser1.setEmail(support1Email);
				tnUser1.setFullName(support1FullName);
				tnUser1.setMobileNo(phone1);
				userService.update(tnUser1);
				if(i>=list.size()) {
					support2FullName = list.get(0).get("full_name")+"";
					String support2Email = list.get(0).get("email")+"";
					String phone = list.get(0).get("mobile_no")+"";
					tnUser2.setEmail(support2Email);
					tnUser2.setFullName(support2FullName);
					tnUser2.setMobileNo(phone);
					userService.update(tnUser2);
				}else {
					support2FullName = list.get(i).get("full_name")+"";
					String support2Email = list.get(i).get("email")+"";
					String phone = list.get(0).get("mobile_no")+"";
					tnUser2.setEmail(support2Email);
					tnUser2.setFullName(support2FullName);
					tnUser2.setMobileNo(phone);
					userService.update(tnUser2);
				}
				break;
			}
			
			
		}
		
	}
}

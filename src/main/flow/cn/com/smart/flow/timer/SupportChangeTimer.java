package cn.com.smart.flow.timer;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mixsmart.utils.LoggerUtils;

import cn.com.smart.utils.DbBase;
import cn.com.smart.web.bean.entity.TNFontLine;
import cn.com.smart.web.bean.entity.TNUser;
import cn.com.smart.web.service.FlowroleService;
import cn.com.smart.web.service.FontLineService;
import cn.com.smart.web.service.OrgService;
import cn.com.smart.web.service.UserService;
/**
 * 
 * @author zhanglb
 *
 */
@Component
public class SupportChangeTimer {
	private static final Logger logger = LoggerFactory.getLogger(SupportChangeTimer.class);
	DbBase db = new DbBase();
	@Autowired
	private UserService userService ;
	@Autowired
	private OrgService orgServ ;
	
	@Autowired
	private FontLineService fontlineServ ;
	
	@Autowired
	private FlowroleService flowroleServ;

//	@Scheduled(cron = "0 17 11 ? * *") 
//	@Scheduled(cron = "0/5 * * * * ? ")
	/**原一线轮询
	public void run() {
		LoggerUtils.info(logger, "正在启动一线值班人员调整...");
		String sql = "select * from t_n_user where id in ( select user_id from t_n_role_user where role_id = (select id from t_n_role where name = '一线人员') ) and sort_order !=''  order by sort_order";
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
					String phone = list.get(i).get("mobile_no")+"";
					tnUser2.setEmail(support2Email);
					tnUser2.setFullName(support2FullName);
					tnUser2.setMobileNo(phone);
					userService.update(tnUser2);
				}
				break;
			}
			
			
		}
		
	}*/
	
	/**
	 * 以一线a角为传递标准
	 */
	/*
	@SuppressWarnings("unchecked")
	public void run() {
		LoggerUtils.info(logger, "正在启动一线值班人员调整...");
//		String sql = "select * from t_n_user where id in ( select user_id from t_n_role_user where role_id = (select id from t_n_role where name = '一线人员') ) and sort_order !=''  order by sort_order"; 
		Map<String, List> gmap = new HashMap<>();//存放各个组的所有人员
//		List<Map<String, Object>> list = db.queryForList(sql);
		List<TNUser> list =flowroleServ.getUserListByFlowRoleName("frontline");
		if(list!=null) {
			for(TNUser map : list) {
				String orgId = map.getOrgId();
				Object userId = map.getId();
				String seqParentIds = orgServ.getBusinessGroupByOrgId(orgId);
				if(seqParentIds!=null && !"".equals(seqParentIds)) {
					if(gmap.containsKey(seqParentIds)) {
						gmap.get(seqParentIds).add(userId);
					}else {
						List<Object> list2 = new ArrayList<>();
						list2.add(userId);
						gmap.put(seqParentIds, list2);
					}
				}
			}
			Iterator<String> keys =	gmap.keySet().iterator();
			while(keys.hasNext()) {
				String key = keys.next();
				List valuelist = gmap.get(key); //获取当前组的所有带循环的人
				List<TNFontLine> tnflist = fontlineServ.getFontLine1ListByGroupId(key);//获取当前组的support1
//				List<TNFontLine> tnflist = fontlineServ.getFontLine2ListByGroupId(key);//获取当前组的support2
				if(tnflist!=null) {
					int i= 0;
					boolean ishas= false;
					
					for(Object value : valuelist) {
						if(value.toString().equals(tnflist.get(0).getUserId())) {
							ishas =true;
							System.out.println(key+"--------");
							fontlineServ.deleteByGroupId(key);
							TNFontLine tnf = new TNFontLine();
							if(valuelist.size() >1) {
								tnf.setGroupId(key);
								tnf.setState(2);
								tnf.setCreateTime(new Date());
								tnf.setUserId(value.toString());
								tnf.setSortOrder(2);
								fontlineServ.save(tnf);
								if(i>=valuelist.size()-1) {
									tnf  = new TNFontLine();
									tnf.setGroupId(key);
									tnf.setState(1);
									tnf.setCreateTime(new Date());
									tnf.setSortOrder(1);
									tnf.setUserId(valuelist.get(0).toString());
									fontlineServ.save(tnf);
								}else {
									tnf  = new TNFontLine();
									tnf.setGroupId(key);
									tnf.setState(1);
									tnf.setCreateTime(new Date());
									tnf.setSortOrder(1);
									tnf.setUserId(valuelist.get(i+1).toString());
									fontlineServ.save(tnf);
								}
							}else {
								tnf = new TNFontLine();
								tnf.setGroupId(key);
								tnf.setState(1);
								tnf.setCreateTime(new Date());
								tnf.setUserId(value.toString());
								tnf.setSortOrder(1);
								fontlineServ.save(tnf);
							}
						}
						i++;
					}
					if(ishas == false) {
						fontlineServ.deleteByGroupId(key);
						TNFontLine tnf = new TNFontLine();
						tnf.setGroupId(key);
						tnf.setState(1);
						tnf.setCreateTime(new Date());
						tnf.setUserId(valuelist.get(0).toString());
						tnf.setSortOrder(1);
						fontlineServ.save(tnf);
						if(valuelist.size() >1 ) {
							tnf  = new TNFontLine();
							tnf.setGroupId(key);
							tnf.setState(2);
							tnf.setSortOrder(2);
							tnf.setCreateTime(new Date());
							tnf.setUserId(valuelist.get(1).toString());
							fontlineServ.save(tnf);
						}
					}
				}else {
					TNFontLine tnf = new TNFontLine();
					tnf.setGroupId(key);
					tnf.setState(1);
					tnf.setCreateTime(new Date());
					tnf.setUserId(valuelist.get(0).toString());
					tnf.setSortOrder(1);
					fontlineServ.save(tnf);
					if(valuelist.size() >1 ) {
						tnf  = new TNFontLine();
						tnf.setGroupId(key);
						tnf.setState(2);
						tnf.setSortOrder(2);
						tnf.setCreateTime(new Date());
						tnf.setUserId(valuelist.get(1).toString());
						fontlineServ.save(tnf);
					}
				}
				
			}
		}
	}*/
	
	
	/**
	 * 以一线b角为传递标准
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		LoggerUtils.info(logger, "正在启动一线值班人员调整...");
//		String sql = "select * from t_n_user where id in ( select user_id from t_n_role_user where role_id = (select id from t_n_role where name = '一线人员') ) and sort_order !=''  order by sort_order"; 
		Map<String, List> gmap = new HashMap<>();//存放各个组的所有人员
//		List<Map<String, Object>> list = db.queryForList(sql);
		List<TNUser> list =flowroleServ.getUserListByFlowRoleName("frontline");
		if(list!=null) {
			for(TNUser map : list) {
				String orgId = map.getOrgId();
				Object userId = map.getId();
				String seqParentIds = orgServ.getBusinessGroupByOrgId(orgId);
				if(seqParentIds!=null && !"".equals(seqParentIds)) {
					if(gmap.containsKey(seqParentIds)) {
						gmap.get(seqParentIds).add(userId);
					}else {
						List<Object> list2 = new ArrayList<>();
						list2.add(userId);
						gmap.put(seqParentIds, list2);
					}
				}
			}
			Iterator<String> keys =	gmap.keySet().iterator();
			while(keys.hasNext()) {
				String key = keys.next();
				List valuelist = gmap.get(key); //获取当前组的所有带循环的人
				List<TNFontLine> tnflist = fontlineServ.getFontLine2ListByGroupId(key);//获取当前组的support2
				if(tnflist!=null) {
					int i= 0;
					boolean ishas= false;
					
					for(Object value : valuelist) {
						if(value.toString().equals(tnflist.get(0).getUserId())) {
							ishas =true;
							System.out.println(key+"--------");
							fontlineServ.deleteByGroupId(key);
							TNFontLine tnf = new TNFontLine();
							if(valuelist.size() >1) {
								tnf.setGroupId(key);
								tnf.setState(1);
								tnf.setCreateTime(new Date());
								tnf.setUserId(value.toString());
								tnf.setSortOrder(1);
								fontlineServ.save(tnf);
								if(i>=valuelist.size()-1) {
									tnf  = new TNFontLine();
									tnf.setGroupId(key);
									tnf.setState(2);
									tnf.setCreateTime(new Date());
									tnf.setSortOrder(2);
									tnf.setUserId(valuelist.get(0).toString());
									fontlineServ.save(tnf);
								}else {
									tnf  = new TNFontLine();
									tnf.setGroupId(key);
									tnf.setState(2);
									tnf.setCreateTime(new Date());
									tnf.setSortOrder(2);
									tnf.setUserId(valuelist.get(i+1).toString());
									fontlineServ.save(tnf);
								}
							}else {
								tnf = new TNFontLine();
								tnf.setGroupId(key);
								tnf.setState(1);
								tnf.setCreateTime(new Date());
								tnf.setUserId(value.toString());
								tnf.setSortOrder(1);
								fontlineServ.save(tnf);
							}
						}
						i++;
					}
					if(ishas == false) {
						fontlineServ.deleteByGroupId(key);
						TNFontLine tnf = new TNFontLine();
						tnf.setGroupId(key);
						tnf.setState(1);
						tnf.setCreateTime(new Date());
						tnf.setUserId(valuelist.get(0).toString());
						tnf.setSortOrder(1);
						fontlineServ.save(tnf);
						if(valuelist.size() >1 ) {
							tnf  = new TNFontLine();
							tnf.setGroupId(key);
							tnf.setState(2);
							tnf.setSortOrder(2);
							tnf.setCreateTime(new Date());
							tnf.setUserId(valuelist.get(1).toString());
							fontlineServ.save(tnf);
						}
					}
				}else {
					TNFontLine tnf = new TNFontLine();
					tnf.setGroupId(key);
					tnf.setState(1);
					tnf.setCreateTime(new Date());
					tnf.setUserId(valuelist.get(0).toString());
					tnf.setSortOrder(1);
					fontlineServ.save(tnf);
					if(valuelist.size() >1 ) {
						tnf  = new TNFontLine();
						tnf.setGroupId(key);
						tnf.setState(2);
						tnf.setSortOrder(2);
						tnf.setCreateTime(new Date());
						tnf.setUserId(valuelist.get(1).toString());
						fontlineServ.save(tnf);
					}
				}
				
			}
		}
	}
}

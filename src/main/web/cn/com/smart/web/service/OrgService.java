package cn.com.smart.web.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.hibernate.SQLQuery;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.bean.TreeProp;
import cn.com.smart.exception.DaoException;
import cn.com.smart.exception.ServiceException;
import cn.com.smart.helper.TreeHelper;
import cn.com.smart.init.config.InitSysConfig;
import cn.com.smart.service.impl.MgrServiceImpl;
import cn.com.smart.web.bean.entity.TNOrg;
import cn.com.smart.web.bean.entity.TNRole;
import cn.com.smart.web.bean.entity.TNRoleOrg;
import cn.com.smart.web.bean.entity.TNUser;
import cn.com.smart.web.bean.entity.TPfproperties;
import cn.com.smart.web.cache.impl.OrgMemoryCache;
import cn.com.smart.web.constant.enums.OrgType;
import cn.com.smart.web.dao.impl.OrgDao;
import cn.com.smart.web.dao.impl.RoleDao;
import cn.com.smart.web.dao.impl.RoleOrgDao;
import cn.com.smart.web.plugins.OrgZTreeData;
import cn.com.smart.web.plugins.ZTreeHelper;
import cn.com.smart.web.utils.EncryptUtils;
import cn.com.smart.web.utils.TreeUtil;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mixsmart.utils.LoggerUtils;
import com.mixsmart.utils.StringUtils;

/**
 * 
 * @author lmq
 *
 */
@Service("orgServ")
public class OrgService extends MgrServiceImpl<TNOrg> {

	@Autowired
	private RoleOrgDao roleOrgDao;
	@Autowired
	private OrgMemoryCache orgCache;
	@Autowired
	private OrgDao orgDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private RoleService roleServ;
	
	@Autowired
    private PropertiesService propertiesService;
    
    @Autowired
    private SubPropertiesService subPropertiesService;
	
	@Override
	public SmartResponse<String> save(TNOrg bean) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(null != bean) {
				TNOrg parentOrg = orgCache.find(bean.getParentId());
				if(null == parentOrg)
					parentOrg = super.find(bean.getParentId()).getData();
				if(null != parentOrg) {
					bean.setSeqParentIds(parentOrg.getSeqParentIds()+"."+parentOrg.getId()+".");
					if(OrgType.DEPARTMENT.getValue().equals(bean.getType())) {
						bean.setSeqNames(parentOrg.getSeqNames()+">"+bean.getName());
					} else {
						bean.setSeqNames(bean.getName());
					}
				} else {
					bean.setSeqNames(bean.getName());
				}
				parentOrg = null;
				smartResp = super.save(bean);
				if(OP_SUCCESS.equals(smartResp.getResult())) {
					TNRole role = roleDao.adminRole();
					if(null != role) {
						LoggerUtils.info(logger, "把添加的组织机构添加到管理员角色里面（数据权限）");
						if(OP_SUCCESS.equals(roleServ.addOrg2Role(role.getId(), new String[]{bean.getId()}))) {
							LoggerUtils.info(logger, "组织机构添加到管理员角色里面[成功]");
						} else {
							LoggerUtils.info(logger, "组织机构添加到管理员角色里面[失败]");
						}
					}
					orgCache.refreshCache();
				}
			}
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return smartResp;
	}
	
	
	@Override
	public SmartResponse<String> update(TNOrg bean) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(null != bean) {
				TNOrg parentOrg = orgCache.find(bean.getParentId());
				if(null == parentOrg)
					parentOrg = super.find(bean.getParentId()).getData();
				//判断是否需要级联更新
				boolean isCascadeUpdate = false; 
				if(!bean.getSeqParentIds().endsWith(bean.getParentId()+".")) {
					isCascadeUpdate = true;
				}
				bean.getParentId();
				bean.getSeqParentIds();
				if(null != parentOrg) {
					bean.setSeqParentIds(StringUtils.handleNull(parentOrg.getSeqParentIds())+parentOrg.getId()+".");
					if(OrgType.DEPARTMENT.getValue().equals(bean.getType())) {
						bean.setSeqNames(parentOrg.getSeqNames()+">"+bean.getName());
					} else {
						bean.setSeqNames(bean.getName());
					}
				} else {
					bean.setSeqNames(bean.getName());
				}
				List<TNOrg> updateOrgs = new ArrayList<TNOrg>();
				if(isCascadeUpdate) {
					List<TNOrg> orgs = orgCache.findAll();
					if(null == orgs)
						orgs = super.findAll().getDatas();
					TNOrg org = getOrg(bean, orgs);
					copyBeanPropValue(bean, org);
					updateOrgs.add(org);
					Stack<TNOrg> stacks = new Stack<TNOrg>();
					stacks.push(bean);
					while(null != stacks && !stacks.isEmpty()) {
						parentOrg = stacks.pop();
						List<TNOrg> subOrgs = getSubOrg(orgs,parentOrg);
						if(null != subOrgs && subOrgs.size()>0) {
							for (TNOrg orgTmp : subOrgs) {
								orgTmp.setSeqParentIds(StringUtils.handleNull(parentOrg.getSeqParentIds())+parentOrg.getId()+".");
								orgTmp.setSeqNames(parentOrg.getSeqNames()+">"+orgTmp.getName());
								updateOrgs.add(orgTmp);
							}
							stacks.addAll(subOrgs);
						}
					}//while
					orgs = null;
					stacks = null;
				} else {
					updateOrgs.add(bean);
				}
				parentOrg = null;
				smartResp = super.update(updateOrgs);
				updateOrgs = null;
				if(OP_SUCCESS.equals(smartResp.getResult())) {
					orgCache.refreshCache();
				}
			}
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return smartResp;
	}
	
	/**
	 *  删除
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<String> delete(String id) {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(super.getDao().delete(id)) {
				smartResp.setResult(OP_SUCCESS);
				smartResp.setMsg(OP_SUCCESS_MSG);
				orgCache.refreshCache();
			}
		} catch (DaoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return smartResp;
	}
	
	/**
	 * 获取对象
	 * @param bean
	 * @param orgs
	 * @return
	 */
	private TNOrg getOrg(TNOrg bean, List<TNOrg> orgs) {
		for (TNOrg org : orgs) {
			if(org.getId().equals(bean.getId())) {
				bean = org;
				break;
			}
		}
		return bean;
	}
	
	/**
	 * 复制对象值
	 * @param source
	 * @param target
	 */
	private void copyBeanPropValue(TNOrg source,TNOrg target) {
		if(null != source && null != target) {
			target.setCode(source.getCode());
			target.setContactNumber(source.getContactNumber());
			target.setContacts(source.getContacts());
			target.setName(source.getName());
			target.setParentId(source.getParentId());
			target.setSeqNames(source.getSeqNames());
			target.setSeqParentIds(source.getSeqParentIds());
			target.setSortOrder(source.getSortOrder());
			target.setType(source.getType());
		}
	}
	
	
	/**
	 * 获取子组织机构
	 * @param orgs
	 * @param org
	 * @return
	 * @throws ServiceException
	 */
	protected List<TNOrg> getSubOrg(List<TNOrg> orgs ,TNOrg org) throws ServiceException {
		List<TNOrg> subOrgs = null;
		try {
			if(null != orgs && orgs.size()>0 && null != org) {
				subOrgs = new ArrayList<TNOrg>();
				for (TNOrg orgTmp : orgs) {
					if(org.getId().equals(orgTmp.getParentId())) {
						subOrgs.add(orgTmp);
					}
				}
				subOrgs = subOrgs.size()>0?subOrgs:null;
			}
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return subOrgs;
	}
	
	
	/**
	 * 获取子组织机构
	 * @param orgIds
	 * @return
	 * @throws ServiceException
	 */
	public List<TreeProp> getTree(Collection<String> orgIds) throws ServiceException {
		List<TreeProp> treeProps = null;
		try {
			List<TNOrg> orgs = null;
			if(null != orgIds && orgIds.size()>0) {
				//orgs =  orgDao.find(StringUtil.list2Array(orgIds));
				orgs =  orgCache.find(StringUtils.list2Array(orgIds));
			} else {
				//orgs = orgDao.findAll();
				orgs = orgCache.findAll();
			}
			if(null != orgs && orgs.size()>0) {
				TreeHelper<TNOrg> treeHelper = new TreeHelper<TNOrg>();
				List<TNOrg> orgTrees = treeHelper.outPutTree(orgs);
				treeProps = TreeUtil.Org2TreeProp(orgTrees);
				orgTrees = null;
				treeHelper = null;
				orgs = null;
			}
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return treeProps;
	}
	
	
	/**
	 * 获取子组织机构
	 * @param orgIds
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<OrgZTreeData> getZTree(List<String> orgIds,String treeType) throws ServiceException {
		SmartResponse<OrgZTreeData> smartResp = new SmartResponse<OrgZTreeData>();
		try {
			if(null != orgIds && orgIds.size()>0) {
				List<TreeProp> treeProps = getTree(orgIds);
				if(null != treeProps && treeProps.size()>0) {
					ZTreeHelper<OrgZTreeData> zTreeHelper = new ZTreeHelper<OrgZTreeData>(OrgZTreeData.class, treeProps);
					List<OrgZTreeData> ztreeDatas = zTreeHelper.convert(treeType);
					zTreeHelper = null;
					treeProps = null;
					if(null != ztreeDatas && ztreeDatas.size()>0) {
						smartResp.setResult(OP_SUCCESS);
						smartResp.setMsg(OP_SUCCESS_MSG);
						smartResp.setDatas(ztreeDatas);
					}
					ztreeDatas = null;
				}
		  }
	  } catch (Exception e) {
		  throw new ServiceException(e.getCause());
	  }
	  return smartResp;
	}
	
	
	/**
	 * 获取树
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<OrgZTreeData> getZTree() throws ServiceException {
		SmartResponse<OrgZTreeData> smartResp = new SmartResponse<OrgZTreeData>();
		try {
			List<TreeProp> treeProps = getTree(null);
			if(null != treeProps && treeProps.size()>0) {
				ZTreeHelper<OrgZTreeData> zTreeHelper = new ZTreeHelper<OrgZTreeData>(OrgZTreeData.class, treeProps);
				List<OrgZTreeData> ztreeDatas = zTreeHelper.convert(null);
				zTreeHelper = null;
				treeProps = null;
				if(null != ztreeDatas && ztreeDatas.size()>0) {
					smartResp.setResult(OP_SUCCESS);
					smartResp.setMsg(OP_SUCCESS_MSG);
					smartResp.setDatas(ztreeDatas);
				}
				ztreeDatas = null;
			}
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return smartResp;
	}
	
	/**
	 * 从组织结构中删除角色
	 * @param orgId
	 * @param id
	 * @return
	 */
	public SmartResponse<String> deleteRole(String orgId, String id) {
	    SmartResponse<String> smartResp = new SmartResponse<String>();
	    smartResp.setMsg("删除失败");
	    if(StringUtils.isEmpty(orgId) || StringUtils.isEmpty(id)) {
	        return smartResp;
	    }
	    Map<String,Object> param = new HashMap<String, Object>(3);
	    param.put("orgId", orgId);
	    param.put("id", id);
	    param.put("flag", "o");
	    if(roleOrgDao.delete(param)) {
	        smartResp.setResult(OP_SUCCESS);
	        smartResp.setMsg("删除成功");
	    }
	    return smartResp;
	}
	
	/**
	 * 组织机构中添加角色
	 * @param orgId
	 * @param roleIds
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<String> addRole2Org(String orgId,String[] roleIds) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(StringUtils.isNotEmpty(orgId) && null != roleIds && roleIds.length>0) {
				if(!roleOrgDao.isRoleInOrgExist(orgId, roleIds)) {
					List<TNRoleOrg> roleOrgs = new ArrayList<TNRoleOrg>();
					TNRoleOrg roleOrg = null;
					for (int i = 0; i < roleIds.length; i++) {
						roleOrg = new TNRoleOrg();
						roleOrg.setRoleId(roleIds[i]);
						roleOrg.setOrgId(orgId);
						roleOrg.setFlag(TNRoleOrg.ORG_FLAG);
						roleOrgs.add(roleOrg);
					}
					List<Serializable> ids = roleOrgDao.save(roleOrgs);
					if(null != ids && ids.size()>0) {
						smartResp.setResult(OP_SUCCESS);
						smartResp.setMsg(OP_SUCCESS_MSG);
					}
					ids = null;
					roleOrg= null;
					roleOrgs = null;
				} else {
					smartResp.setMsg("角色已添加到该组织机构里面，不能重复添加！");
				}
				roleIds = null;
			}
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return smartResp;
	}
	
	
	private static String eid = null;
	private static String group =null ;
	private static String keypath = InitSysConfig.getInstance().getPath(InitSysConfig.getInstance().getValue("keypath"));
	private static String yzj_base_url = null;
	private static String user_getbyphone_url = null;
	
	public void initYunzhijia() {
		TPfproperties toPfproperties = propertiesService.getByCode("yunzhijia");
		String formDataId = toPfproperties.getForm_data_id();
		eid = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "eid").getKeyvalue();
		group = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "group").getKeyvalue();
//		keypath = InitSysConfig.getInstance().getPath(InitSysConfig.getInstance().getValue("keypath"));
		yzj_base_url = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "yzj_base_url").getKeyvalue();
		user_getbyphone_url = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "user_getbyphone_url").getKeyvalue();
	}
	
	public String getDeptIdByPhoneFromYZj(String phone, String username) {
		String deptId = "0";
		initYunzhijia();
		try {
			if("".equals(phone) || phone == null) {
				return null;
			}
			JsonNode jsonData = new JsonNode("{'array':['"+phone+"']}");
			byte[] keyByte = EncryptUtils.getBytesFromFile(keypath);
			Key key = EncryptUtils.restorePrivateKey(keyByte);
			HttpResponse<InputStream> jsonResponse = Unirest

					.post(yzj_base_url+user_getbyphone_url)

					.header("Content-Type", "application/x-www-form-urlencoded")

					.field("eid", "11104200")	
					.field("nonce", UUID.randomUUID().toString())

					.field("data",

							EncryptUtils.encryptWithEncodeBase64UTF8(

									jsonData.toString(), key)).asBinary();
			InputStream jsonObject = jsonResponse.getBody();
			BufferedReader in = new BufferedReader(new InputStreamReader(jsonObject,"utf-8"));
			String s =in.readLine();
			JSONObject jn = new JSONObject(s); 
			
			System.out.println(s);
			JSONArray js =  jn.getJSONArray("data");
			if(js.length() >0 ) {
				JSONObject jb = js.getJSONObject(0);
				String email = jb.getString("email");
				if(email !=null && !"".equals(email)) {
					String name = email.split("@")[0];
					if(!name.equals(username)) {
						return null;
					}
				}
				String department = jb.getString("department");
				department = department.startsWith(group)?department:group+"\\"+department;
				String []orgnizations = department.split("\\\\");
				String seqNames ="";
			    String seqParantIds = "";
			    String parentDeptId = "0";
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
			    		SmartResponse<String> smartResp = this.save(entity);
			    		deptId = smartResp.getData();
			    		
			    		parentDeptId = deptId;
			    	}
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return deptId;
	}
	
	
	public String getBusinessGroupByOrgId(String orgId) {
		String seqParentId = this.getDao().find(orgId).getSeqParentIds();
		 if(seqParentId != null && !"".equals(seqParentId)) {
			 String[] bussinessGroupId2 = seqParentId.split("\\.\\.");
			 if(bussinessGroupId2.length >1) {
				 return bussinessGroupId2[1].replaceAll("\\.", ""); 
			 }else{
				 return bussinessGroupId2[0].replaceAll("\\.", "");
			 }
		 }
		 return null;
		
	}
	
}

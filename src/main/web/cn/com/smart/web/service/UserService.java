package cn.com.smart.web.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mixsmart.security.SecurityUtils;
import com.mixsmart.utils.LoggerUtils;
import com.mixsmart.utils.StringUtils;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.bean.TreeProp;
import cn.com.smart.exception.DaoException;
import cn.com.smart.exception.ServiceException;
import cn.com.smart.helper.ObjectHelper;
import cn.com.smart.service.impl.MgrServiceImpl;
import cn.com.smart.utils.DbBase;
import cn.com.smart.web.bean.UserInfo;
import cn.com.smart.web.bean.entity.TNFlowRoleUser;
import cn.com.smart.web.bean.entity.TNOrg;
import cn.com.smart.web.bean.entity.TNPosition;
import cn.com.smart.web.bean.entity.TNRoleUser;
import cn.com.smart.web.bean.entity.TNUser;
import cn.com.smart.web.bean.entity.TPfproperties;
import cn.com.smart.web.constant.enums.OrgType;
import cn.com.smart.web.controller.impl.PropertiesController;
import cn.com.smart.web.dao.impl.FlowRoleUserDao;
import cn.com.smart.web.dao.impl.OrgDao;
import cn.com.smart.web.dao.impl.PositionDao;
import cn.com.smart.web.dao.impl.RoleUserDao;
import cn.com.smart.web.dao.impl.UserDao;
import cn.com.smart.web.filter.bean.UserSearchParam;
import cn.com.smart.web.helper.TreeCombinHelper;
import cn.com.smart.web.plugins.OrgUserZTreeData;
import cn.com.smart.web.plugins.ZTreeHelper;

/**
 * 
 * @author lmq
 *
 */
@Service
public class UserService extends MgrServiceImpl<TNUser> {
	DbBase db = new DbBase();
    @Autowired
	private UserDao userDao;
    @Autowired
	private OrgDao orgDao;
    @Autowired
	private RoleUserDao roleUserDao;
    @Autowired
    private FlowRoleUserDao flowroleUserDao;
    @Autowired
    private TreeCombinHelper treeCombinHelper;
    @Autowired
	private OrgService orgServ;
    @Autowired
    private PositionDao posDao;
    
    @Autowired
    private OrgService orgService;
    
    @Autowired
    private RoleService roleSev;
    
    @Autowired
    private PropertiesService propertiesService;
    
    @Autowired
    private SubPropertiesService subPropertiesService;
	
	/**
	 * 根据searchParam参数查询用户信息
	 * @param searchParam
	 * @param start
	 * @param rows
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<Object> findAllObj(UserSearchParam searchParam,int start,int rows) throws ServiceException {
		SmartResponse<Object> smartResp = new SmartResponse<Object>();
		smartResp.setResult(OP_NOT_DATA_SUCCESS);
		smartResp.setMsg(OP_NOT_DATA_SUCCESS_MSG);
		try {
			List<Object> objs = userDao.queryObjPage(searchParam, start, rows);
			if(null != objs && objs.size()>0) {
				smartResp.setResult(OP_SUCCESS);
				smartResp.setMsg(OP_SUCCESS_MSG);
				objs = ObjectHelper.handleObjDate(objs);
				smartResp.setDatas(objs);
				long total = userDao.queryObjCount(searchParam);
				smartResp.setTotalNum(total);
				smartResp.setTotalPage(getTotalPage(total, rows));
			}
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return smartResp;
	}
	
	

	/**
	 * 保存用户信息
	 * @param user
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<String> save(TNUser user) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(null != user) {
				String userName = user.getUsername()==null?"":user.getUsername();
				if(userDao.isExistUsername(userName)){
					smartResp.setResult(OP_FAIL);
					smartResp.setMsg("该用户名已存在，不能在注册！");
					return smartResp;
				}
				user.setPassword(SecurityUtils.md5(user.getPassword()));
				Serializable  id = userDao.save(user);
				if(null != id) {
					smartResp.setResult(OP_SUCCESS);
					smartResp.setMsg("用户添加成功");
					smartResp.setData(id.toString());
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
	public SmartResponse<String> update(TNUser user) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(null != user) {
				TNUser oldUser = userDao.queryUser(user.getUsername());
				if(null != oldUser && !oldUser.getId().equals(user.getId())){//如果用户名被修改了 ，就需要验证新名字是否冲突
					smartResp.setResult(OP_FAIL);
					smartResp.setMsg("该用户名已存在，不能在注册！");
				} else {
					boolean is = false;
					if(null != oldUser && oldUser.getId().equals(user.getId())) {
						oldUser.setUsername(user.getUsername());
						oldUser.setEmail(user.getEmail());
						oldUser.setFullName(user.getFullName());
						oldUser.setMobileNo(user.getMobileNo());
						oldUser.setOrgId(user.getOrgId());
						oldUser.setPositionId(user.getPositionId());
						oldUser.setQq(user.getQq());
						oldUser.setRemark(user.getRemark());
						oldUser.setState(user.getState());
						is = userDao.update(oldUser);
					} else {
						is = userDao.update(user);
					}
					if(is) {
						smartResp.setResult(OP_SUCCESS);
						smartResp.setMsg("用户信息修改成功");
						smartResp.setData(user.getId());
					}
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
	 * 删除用户信息
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<String> del(String id) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(null != id) {
				if(userDao.delete(id)){
					smartResp.setResult(OP_SUCCESS);
					smartResp.setMsg("用户数据删除成功");
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
	 * 用户登录
	 * @param username
	 * @param password
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<UserInfo> login(String username,String password) throws ServiceException {
		SmartResponse<UserInfo> smartResp = new SmartResponse<UserInfo>();
		try {
			if(StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
				if(propertiesService.isValidByCode("ADomain")) {
					if(ADCheck(username, password)) {
						GetADInfo(username, password);
					}
				}
				
				String md5Pwd = SecurityUtils.md5(password);
				TNUser user = userDao.queryLogin(username, md5Pwd);
				if(null != user) {
				    String telephone = user.getMobileNo();
				    UserInfo userInfo = getUserInfo(user);
					String deptId = "";
					if(telephone != null && propertiesService.isValidByCode("yunzhijia")) {
				    	deptId = orgService.getDeptIdByPhoneFromYZj(telephone.trim(), username);//云之家的同步组织
				    	if(!"".equals(deptId) && deptId!=null && deptId !="0") {
				    		user.setOrgId(deptId);
				    		this.update(user);
				    	}
				    }
					smartResp.setResult(OP_SUCCESS);
					smartResp.setMsg(OP_SUCCESS_MSG);
					smartResp.setData(userInfo);
				} else {
					LoggerUtils.info(logger, "用户名或密码错误--输入用户名["+username+"]---");
				}
			} else {
				LoggerUtils.info(logger, "用户名或密码为空--");
			}
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getCause());
		}
		return smartResp;
	}
	
	/**
	 * 获取用户信息 
	 * @param user 用户实体对象
	 * @return 返回用户信息
	 */
	public UserInfo getUserInfo(TNUser user) {
	    UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setEmail(user.getEmail());
        userInfo.setUsername(user.getUsername());
        userInfo.setFullName(user.getFullName());
        TNOrg org =  orgDao.find(user.getOrgId());
        String deptName = null;
        String seqDeptName = null;
        if(null != org) {
            deptName = org.getName();
            seqDeptName = org.getSeqNames();
            userInfo.setOrgId(org.getId());
            if(OrgType.DEPARTMENT.getValue().equals(org.getType())) {
                userInfo.setDepartmentId(org.getId());
            }
        } else {
            userInfo.setOrgId(user.getOrgId());
        }
        userInfo.setPositionId(user.getPositionId());
        if(StringUtils.isNotEmpty(user.getPositionId())){
            TNPosition position = posDao.find(user.getPositionId());
            if(null != position) {
                userInfo.setPositionName(position.getName());
            }
            position = null;
        }
        userInfo.setDeptName(deptName);
        userInfo.setSeqDeptNames(seqDeptName);
        userInfo.setMenuRoleIds(userDao.queryMenuRoleIds(user.getId()));
        userInfo.setRoleIds(userDao.queryRoleIds(user.getId()));
        userInfo.setOrgIds(userDao.queryOrgIds(user.getId()));
        return userInfo;
	}
	
	@SuppressWarnings("unused")
	public String GetADInfo(String name, String password ) {
		  String userName = name; // 用户名称
		  if(userName==null){
			  userName = "";
			  return null;
		  }
		  
		  String company = "";
		  TPfproperties toPfproperties = propertiesService.getByCode("ADomain");
		  String formDataId = toPfproperties.getForm_data_id();
		  String host = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "host").getKeyvalue();//AD域IP，必须填写正确
		  String domain = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "domain").getKeyvalue();//AD域IP，必须填写正确"@xinguangnet.com";//域名后缀，例.@noker.cn.com
		  String port = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "port").getKeyvalue(); //端口，一般默认389
		  String adminName =subPropertiesService.getByFormDataIdAndKeyname(formDataId, "adminName").getKeyvalue(); // 注意用户名的写法：domain\User 
		  String adminPassword =subPropertiesService.getByFormDataIdAndKeyname(formDataId, "adminPassword").getKeyvalue(); // 密码
		  String searchBase =subPropertiesService.getByFormDataIdAndKeyname(formDataId, "searchBase").getKeyvalue(); // 密码
		  String url = new String("ldap://" + host + ":" + port);
		  Hashtable<String, String> HashEnv = new Hashtable<String, String>();
		  HashEnv.put(Context.SECURITY_AUTHENTICATION, "simple"); // LDAP访问安全级别
		  HashEnv.put(Context.SECURITY_PRINCIPAL, adminName); // AD User
		  HashEnv.put(Context.SECURITY_CREDENTIALS, adminPassword); // AD Password
		  HashEnv.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory"); // LDAP工厂类
		  HashEnv.put(Context.PROVIDER_URL, url);
		  try {
		   LdapContext ctx = new InitialLdapContext(HashEnv, null);
		   // LDAP搜索过滤器类
		   String searchFilter = "sAMAccountName="+userName;
		   // 搜索控制器
		   SearchControls searchCtls = new SearchControls(); 
		   searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Specify
		   String returnedAtts[] = {"name","OU","sAMAccountName","userPrincipalName","mobile"};

		   searchCtls.setReturningAttributes(returnedAtts); // 设置返回属性集
		   // 根据设置的域节点、过滤器类和搜索控制器搜索LDAP得到结果
		   NamingEnumeration answer = ctx.search(searchBase, searchFilter,
		     searchCtls);// Search for objects using the filter
		   // 初始化搜索结果数为0
		   int totalResults = 0;// Specify the attributes to return
		   int rows = 0;
		   System.out.println(answer);
		   while (answer.hasMoreElements()) {// 遍历结果集
		    SearchResult sr = (SearchResult) answer.next();// 得到符合搜索条件的DN
		    System.out.println(++rows
		      + "************************************************");
		    /**注释域里的组织同步
		    String dn = sr.getName();
		    String []orgnizations = dn.split(",");
		    String parentDeptId = "0";
		    String deptId = "0";
		    String seqNames ="";
		    String seqParantIds = "";
		    for(int i =orgnizations.length-1;i>0;i--) {
		    	String dept = orgnizations[i].replace("OU=", "").trim();
		    	
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
		    }*/
		    Attributes Attrs = sr.getAttributes();// 
		    String userCheineseName = Attrs.get("name").get(0)+"";
		    String userAccout = Attrs.get("sAMAccountName").get(0)+"";
		    String userEmail = Attrs.get("userPrincipalName").get(0)+"";
		    TNUser userEntity =  userDao.queryUser(userAccout);
		    String telephone = userEntity.getMobileNo();
		    String deptId = "";
		    System.out.println(telephone+"telephone----------------------------");
		    if(telephone != null) {
		    	deptId = orgService.getDeptIdByPhoneFromYZj(telephone, userAccout);
		    	System.out.println(deptId+"-===============");
		    }
		    if(userEntity != null) {
		    	userEntity.setFullName(userCheineseName);
		    	userEntity.setEmail(userEmail);
		    	System.out.println("----"+deptId);
		    	if(deptId!="" && deptId!=null && !"".equals(deptId)) {
		    		userEntity.setOrgId(deptId);
		    	}
		    	
		    	this.update(userEntity);
		    	password = SecurityUtils.md5(password);
		    	userDao.changePwd(userEntity.getId(), password);
		    }else {
//		    	userEntity = new TNUser();
//		    	userEntity.setUsername(userAccout);
//		    	userEntity.setFullName(userCheineseName);
//		    	userEntity.setEmail(userEmail);
//		    	userEntity.setPassword(password);
//		    	userEntity.setOrgId(deptId);
//		    	userEntity.setState("1");
//		    	this.save(userEntity);
		    }
		    
		   }
		   ctx.close();
		  } catch (NamingException e) {
		   e.printStackTrace();
		   System.err.println("Throw Exception : " + e);
		  }
		  return company;
		 }
	
	
	public boolean registerUser(String name, String password, String phone ) {
		 boolean result = false;
		  String userName = name; // 用户名称
		  if(userName==null){
			  userName = "";
			  return result;
		  }
		  String company = "";
		  TPfproperties toPfproperties = propertiesService.getByCode("ADomain");
		String formDataId = toPfproperties.getForm_data_id();
		String host = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "host").getKeyvalue();//AD域IP，必须填写正确
        String port = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "port").getKeyvalue(); //端口，一般默认389
        String adminName =subPropertiesService.getByFormDataIdAndKeyname(formDataId, "adminName").getKeyvalue(); // 注意用户名的写法：domain\User 
		String adminPassword =subPropertiesService.getByFormDataIdAndKeyname(formDataId, "adminPassword").getKeyvalue(); // 密码  
        String url = new String("ldap://" + host + ":" + port);
		  Hashtable<String, String> HashEnv = new Hashtable<String, String>();
		  
		  HashEnv.put(Context.SECURITY_AUTHENTICATION, "simple"); // LDAP访问安全级别
		  HashEnv.put(Context.SECURITY_PRINCIPAL, adminName); // AD User
		  HashEnv.put(Context.SECURITY_CREDENTIALS, adminPassword); // AD Password
		  HashEnv.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory"); // LDAP工厂类
		  HashEnv.put(Context.PROVIDER_URL, url);
		  try {
		   LdapContext ctx = new InitialLdapContext(HashEnv, null);
		   // 域节点
		   String searchBase = "DC=xinguangnet,DC=com";
		   // LDAP搜索过滤器类
		   String searchFilter = "sAMAccountName="+userName;
		   // 搜索控制器
		   SearchControls searchCtls = new SearchControls(); 
		   searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Specify
		   String returnedAtts[] = {"name","OU","sAMAccountName","userPrincipalName"};

		   searchCtls.setReturningAttributes(returnedAtts); // 设置返回属性集
		   // 根据设置的域节点、过滤器类和搜索控制器搜索LDAP得到结果
		   NamingEnumeration answer = ctx.search(searchBase, searchFilter,
		     searchCtls);// Search for objects using the filter
		   // 初始化搜索结果数为0
		   int totalResults = 0;// Specify the attributes to return
		   int rows = 0;
		   System.out.println(answer);
		   if(!answer.hasMoreElements()) {
			   
		   }else {
			   while (answer.hasMoreElements()) {// 遍历结果集
				    SearchResult sr = (SearchResult) answer.next();// 得到符合搜索条件的DN
				    System.out.println(++rows
				      + "************************************************");
				    /**取消从域获取组织
				    String dn = sr.getName();
				    String []orgnizations = dn.split(",");
				    String parentDeptId = "0";
				    String deptId = "0";
				    String seqNames ="";
				    String seqParantIds = "";
				    for(int i =orgnizations.length-1;i>0;i--) {
				    	String dept = orgnizations[i].replace("OU=", "").trim();
				    	
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
				    }*/
				   
				    Attributes Attrs = sr.getAttributes();// 
				    String userCheineseName = Attrs.get("name").get(0)+"";
				    String userAccout = Attrs.get("sAMAccountName").get(0)+"";
				    String userEmail = Attrs.get("userPrincipalName").get(0)+"";
				    String deptId  = orgService.getDeptIdByPhoneFromYZj(phone,userAccout);
				    
				    TNUser userEntity =  userDao.queryUser(userAccout);
				    if(userEntity != null) {
				    	userEntity.setFullName(userCheineseName);
				    	userEntity.setMobileNo(phone);
				    	userEntity.setEmail(userEmail);
				    	System.out.println("----"+deptId);
				    	userEntity.setOrgId(deptId);
				    	this.update(userEntity);
				    	password = SecurityUtils.md5(password);
				    	userDao.changePwd(userEntity.getId(), password);
				    }else {
				    	userEntity = new TNUser();
				    	userEntity.setUsername(userAccout);
				    	userEntity.setFullName(userCheineseName);
				    	userEntity.setEmail(userEmail);
				    	userEntity.setMobileNo(phone);
				    	userEntity.setPassword(password);
				    	userEntity.setOrgId(deptId);
				    	userEntity.setState("1");
				    	this.save(userEntity);
				    }
				    saveRoleToNewUser(userEntity.getId(), "普通用户");
				   }
			   result =true;
		   }
		   
		   ctx.close();
		  } catch (NamingException e) {
		   e.printStackTrace();
		   System.err.println("Throw Exception : " + e);
		  }
		  return result;
		 }
	
	
	
	@SuppressWarnings("finally")
	public boolean ADCheck(String userName, String password) {
		boolean result = false;
		TPfproperties toPfproperties = propertiesService.getByCode("ADomain");
		String formDataId = toPfproperties.getForm_data_id();
		String host = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "host").getKeyvalue();//AD域IP，必须填写正确
        String domain = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "domain").getKeyvalue();//AD域IP，必须填写正确"@xinguangnet.com";//域名后缀，例.@noker.cn.com
        String port = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "port").getKeyvalue(); //端口，一般默认389
        String url = new String("ldap://" + host + ":" + port);//固定写法
        String user = userName.indexOf(domain) > 0 ? userName : userName
                + domain;//网上有别的方法，但是在我这儿都不好使，建议这么使用
        Hashtable<String, String> env = new Hashtable<String, String>();//实例化一个Env
        DirContext ctx = null;
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//LDAP访问安全级别(none,simple,strong),一种模式，这么写就行
        env.put(Context.SECURITY_PRINCIPAL, user); //用户名
        env.put(Context.SECURITY_CREDENTIALS, password);//密码
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");// LDAP工厂类
        env.put(Context.PROVIDER_URL, url);//Url
        try {
            ctx = new InitialDirContext(env);// 初始化上下文
            result = true;
            System.out.println("身份验证成功!");
        } catch (AuthenticationException e) {
            System.out.println("身份验证失败!");
//            e.printStackTrace();
        } catch (javax.naming.CommunicationException e) {
            System.out.println("AD域连接失败!");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("身份验证未知异常!");
            e.printStackTrace();
        } finally{
            if(null!=ctx){
                try {
                    ctx.close();
                    ctx=null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    }
	
	
	/**
	 * 批量修改密码
	 * @param userIds
	 * @param pwd
	 * @param confirmPwd
	 * @return
	 * @throws ServiceException
	 */
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public SmartResponse<String> batchChangePwd(String userIds,String pwd,String confirmPwd) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(StringUtils.isNotEmpty(userIds) && StringUtils.isNotEmpty(pwd) && StringUtils.isNotEmpty(confirmPwd)) {
				if(pwd.equals(confirmPwd)) {
					String[] userIdArray = userIds.split(",");
					pwd = SecurityUtils.md5(pwd);
					if(userDao.batchChangePwd(userIdArray, pwd)) {
						smartResp.setResult(OP_SUCCESS);
						smartResp.setMsg("密码修改成功");
					} else 
						smartResp.setMsg("密码修改失败");
					userIdArray = null;
				} else {
					smartResp.setMsg("两次输入的密码不一致");
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
	 * 修改密码
	 * @param userId
	 * @param oldPwd
	 * @param newPwd
	 * @param confirmNewPwd
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<String> changePwd(String userId,String oldPwd,String newPwd,String confirmNewPwd) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(oldPwd) 
					&& StringUtils.isNotEmpty(newPwd) && StringUtils.isNotEmpty(confirmNewPwd)) {
				if(newPwd.equals(confirmNewPwd)) {
					TNUser user = userDao.find(userId);
					if(null != user) {
						String md5Pwd = SecurityUtils.md5(oldPwd);
						if(user.getPassword().equals(md5Pwd)) {
							String md5NewPwd = SecurityUtils.md5(newPwd);
							if(userDao.changePwd(userId, md5NewPwd)) {
								smartResp.setResult(OP_SUCCESS);
								smartResp.setMsg("密码修改成功");
								smartResp.setData(null);
							} else {
								smartResp.setResult(OP_FAIL);
								smartResp.setMsg("密码修改失败");
							}
						} else {
							smartResp.setMsg("旧密码输入错误");
							smartResp.setData("2");
						}
					} else {
						smartResp.setMsg("用户不存在");
					}
				} else {
					smartResp.setMsg("两次输入的密码不一致");
					smartResp.setData("1");
				}
			}
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return smartResp;
	}
	
	public void saveRoleToNewUser(String userId, String roleName) {
		String roleId = roleSev.getRoleIdByRoleName(roleName);
		String[] roleIds= {roleId};
		System.out.println(userId+"-====-"+roleId);
		addRole2User(userId, roleIds);
	}
	
	/**
	 * 用户中添加角色
	 * @param userId
	 * @param roleIds
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<String> addRole2User(String userId,String[] roleIds) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(StringUtils.isNotEmpty(userId) && null != roleIds && roleIds.length>0) {
				if(!roleUserDao.isRoleInUserExist(userId, roleIds)) {
					List<TNRoleUser> roleUsers = new ArrayList<TNRoleUser>();
					TNRoleUser roleUser = null;
					for (int i = 0; i < roleIds.length; i++) {
						roleUser = new TNRoleUser();
						roleUser.setRoleId(roleIds[i]);
						roleUser.setUserId(userId);
						roleUsers.add(roleUser);
					}
					List<Serializable> ids = roleUserDao.save(roleUsers);
					if(null != ids && ids.size()>0) {
						smartResp.setResult(OP_SUCCESS);
						smartResp.setMsg(OP_SUCCESS_MSG);
					}
					ids = null;
					roleUser = null;
					roleUsers = null;
				} else {
					smartResp.setMsg("角色已添加到用户里面，不能重复添加！");
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
	
	
	/**
	 * 用户中添加流程角色
	 * @param userId
	 * @param roleIds
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<String> addFlowRole2User(String userId,String[] roleIds) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(StringUtils.isNotEmpty(userId) && null != roleIds && roleIds.length>0) {
				if(!flowroleUserDao.isRoleInUserExist(userId, roleIds)) {
					List<TNFlowRoleUser> roleUsers = new ArrayList<TNFlowRoleUser>();
					TNFlowRoleUser roleUser = null;
					for (int i = 0; i < roleIds.length; i++) {
						roleUser = new TNFlowRoleUser();
						roleUser.setRoleId(roleIds[i]);
						roleUser.setUserId(userId);
						roleUsers.add(roleUser);
					}
					List<Serializable> ids = flowroleUserDao.save(roleUsers);
					if(null != ids && ids.size()>0) {
						smartResp.setResult(OP_SUCCESS);
						smartResp.setMsg(OP_SUCCESS_MSG);
					}
					ids = null;
					roleUser = null;
					roleUsers = null;
				} else {
					smartResp.setMsg("角色已添加到用户里面，不能重复添加！");
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
	
	/**
	 * 获取组织机构用户树
	 * @param orgIds
	 * @param userId 过滤用户
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<OrgUserZTreeData> getOrgUserZTree(List<String> orgIds,String userId) throws ServiceException {
		SmartResponse<OrgUserZTreeData> smartResp = new SmartResponse<OrgUserZTreeData>();
		List<TreeProp> ztreeProps = orgServ.getTree(orgIds);
		if(null != ztreeProps && ztreeProps.size()>0) {
			String[] orgIdArray = new String[ztreeProps.size()];
			int count = 0;
			for(TreeProp treeProp : ztreeProps) {
				orgIdArray[count] = treeProp.getId();
				count++;
			}
			try {
				List<TNUser> users = userDao.queryByOrgIds(orgIdArray);
				if(null != users && users.size()>0) {
					TreeProp treeProp = null;
					List<TreeProp> newTreeProp = new ArrayList<TreeProp>();
					count = 1;
					for(int i = 0; i < ztreeProps.size(); i++) {
						newTreeProp.add(ztreeProps.get(i));
						count++;
						for(TNUser userTmp : users) {
							if(ztreeProps.get(i).getId().equals(userTmp.getOrgId()) && !userId.equals(userTmp.getId())) {
								treeProp = new TreeProp();
								treeProp.setFlag("user");
								treeProp.setId(userTmp.getId());
								String fullName = userTmp.getFullName();
								if(StringUtils.isEmpty(fullName))
									fullName = userTmp.getUsername();
								treeProp.setName(fullName);
								treeProp.setSortOrder(count);
								treeProp.setParentId(ztreeProps.get(i).getId());
								newTreeProp.add(treeProp);
								count++;
							}
						}
					}//for;
					treeProp = null;
					ztreeProps = null;
					users = null;
					
					ZTreeHelper<OrgUserZTreeData> zTreeHelper = new ZTreeHelper<OrgUserZTreeData>(OrgUserZTreeData.class, newTreeProp);
					List<OrgUserZTreeData> ztreeDatas = zTreeHelper.convert("user");
					zTreeHelper = null;
					if(null != ztreeDatas && ztreeDatas.size()>0) {
						smartResp.setResult(OP_SUCCESS);
						smartResp.setMsg(OP_SUCCESS_MSG);
						smartResp.setDatas(ztreeDatas);
					}
					ztreeDatas = null;
				}
			} catch (DaoException e) {
				throw new ServiceException(e);
			} finally {
				orgIdArray = null;
			}
		}
		return smartResp;
	}
	
	/**
	 * 获取组织机构用户树
	 * @param orgIds
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<OrgUserZTreeData> getOrgUserZTree(List<String> orgIds) throws ServiceException {
		SmartResponse<OrgUserZTreeData> smartResp = new SmartResponse<OrgUserZTreeData>();
		List<TreeProp> ztreeProps = orgServ.getTree(orgIds);
		if(null != ztreeProps && ztreeProps.size()>0) {
			String[] orgIdArray = new String[ztreeProps.size()];
			int count = 0;
			for(TreeProp treeProp : ztreeProps) {
				orgIdArray[count] = treeProp.getId();
				count++;
			}
			try {
				smartResp = getOrgUserZTree(ztreeProps, userDao.queryByOrgIds(orgIdArray));
			} catch (DaoException e) {
				throw new ServiceException(e);
			} finally {
				orgIdArray = null;
			}
		}
		return smartResp;
	}
	
	/**
	 * 获取组织机构用户树
	 * @param users
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<OrgUserZTreeData> getOrgUserZTreeByUser(List<TNUser> users) throws ServiceException {
		SmartResponse<OrgUserZTreeData> smartResp = new SmartResponse<OrgUserZTreeData>();
		if(null != users && users.size()>0) {
			Set<String> orgIds = new HashSet<String>();
			for(TNUser user : users) {
				orgIds.add(user.getOrgId());
			}
			List<TreeProp> ztreeProps = orgServ.getTree(null);
			try {
				smartResp = getOrgUserZTree(ztreeProps, users);
			} catch (ServiceException e) {
				throw new ServiceException(e);
			} finally {
				orgIds = null;
			}
		}
		return smartResp;
	}
	
	/**
	 * 
	 * @param ztreeProps
	 * @param users
	 * @return
	 * @throws ServiceException
	 */
	private SmartResponse<OrgUserZTreeData> getOrgUserZTree(List<TreeProp> ztreeProps,List<TNUser> users) throws ServiceException {
		SmartResponse<OrgUserZTreeData> smartResp = new SmartResponse<OrgUserZTreeData>();
		if(null != users && users.size()>0  && null != ztreeProps && ztreeProps.size()>0) {
			int count = 0;
			TreeProp treeProp = null;
			List<TreeProp> newTreeProp = new ArrayList<TreeProp>();
			count = 1;
			for(int i = 0; i < ztreeProps.size(); i++) {
				newTreeProp.add(ztreeProps.get(i));
				count++;
				for(TNUser userTmp : users) {
					if(ztreeProps.get(i).getId().equals(userTmp.getOrgId())) {
						treeProp = new TreeProp();
						treeProp.setFlag("user");
						treeProp.setId(userTmp.getId());
						String fullName = userTmp.getFullName();
						if(StringUtils.isEmpty(fullName))
							fullName = userTmp.getUsername();
						treeProp.setName(fullName);
						treeProp.setSortOrder(count);
						treeProp.setParentId(ztreeProps.get(i).getId());
						newTreeProp.add(treeProp);
						count++;
					}
				}
			}//for;
			treeProp = null;
			ztreeProps = null;
			users = null;
			ZTreeHelper<OrgUserZTreeData> zTreeHelper = new ZTreeHelper<OrgUserZTreeData>(OrgUserZTreeData.class, newTreeProp);
			List<OrgUserZTreeData> ztreeDatas = zTreeHelper.convert("user");
			zTreeHelper = null;
			treeCombinHelper.trimLeaf(ztreeDatas);
			if(null != ztreeDatas && ztreeDatas.size()>0) {
				smartResp.setResult(OP_SUCCESS);
				smartResp.setMsg(OP_SUCCESS_MSG);
				smartResp.setDatas(ztreeDatas);
			}
			ztreeDatas = null;
		}
		return smartResp;
	}

	@Override
	public UserDao getDao() {
		return (UserDao)super.getDao();
	}
	
	/**
	 * 从用户中删除角色
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public SmartResponse<String> deleteRole(String userId, String roleId) {
	    SmartResponse<String> smartResp = new SmartResponse<String>();
	    smartResp.setMsg("删除失败");
	    if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(roleId)) {
	        return smartResp;
	    }
	    Map<String, Object> param = new HashMap<String, Object>(3);
	    param.put("userId", userId);
	    param.put("id", roleId);
	    param.put("flag", "u");
	    if(roleUserDao.delete(param)) {
	        smartResp.setResult(OP_SUCCESS);
	        smartResp.setMsg("删除成功");
	    }
	    return smartResp;
	}
	
	/**
	 * 从用户中删除角色
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public SmartResponse<String> deleteFlowRole(String userId, String roleId) {
	    SmartResponse<String> smartResp = new SmartResponse<String>();
	    smartResp.setMsg("删除失败");
	    if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(roleId)) {
	        return smartResp;
	    }
	    Map<String, Object> param = new HashMap<String, Object>(3);
	    param.put("userId", userId);
	    param.put("id", roleId);
	    param.put("flag", "u");
	    if(flowroleUserDao.delete(param)) {
	        smartResp.setResult(OP_SUCCESS);
	        smartResp.setMsg("删除成功");
	    }
	    return smartResp;
	}
	
	/**
	 * 获取虚拟注册用户信息
	 */
	public List<Map<String, Object>> getVirtualUserId() {
		String sql = "select * from t_n_user where username = ?";
		List<Map<String, Object>> list = db.queryForList(sql, new Object[] {"register"});
		if(list.size()>0) {
			return list;
		}
		return null;
	}
	
	
	
	
//	public UserInfo getVirtualUserId() {
//		String sql = "select id from t_n_user where username = ?";
//		List<Map<String, Object>> list = db.queryForList(sql, new Object[] {"register"});
//		if(list.size()>0) {
//			return list;
//		}
//		return null;
//	}
}

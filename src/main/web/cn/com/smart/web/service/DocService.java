package cn.com.smart.web.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mixsmart.utils.ArrayUtils;
import com.mixsmart.utils.LoggerUtils;
import com.mixsmart.utils.StringUtils;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.bean.TreeProp;
import cn.com.smart.exception.DaoException;
import cn.com.smart.exception.ServiceException;
import cn.com.smart.helper.TreeHelper;
import cn.com.smart.service.impl.MgrServiceImpl;
import cn.com.smart.web.bean.entity.TNDoc;
import cn.com.smart.web.bean.entity.TNOPAuth;
import cn.com.smart.web.bean.entity.TNResource;
import cn.com.smart.web.bean.entity.TNRoleDocResource;
import cn.com.smart.web.constant.enums.OrgType;
import cn.com.smart.web.dao.impl.DocDao;
import cn.com.smart.web.dao.impl.DocResourceDao;
import cn.com.smart.web.dao.impl.OPAuthDao;
import cn.com.smart.web.plugins.DocZTreeData;
import cn.com.smart.web.plugins.ZTreeData;
import cn.com.smart.web.plugins.ZTreeHelper;
import cn.com.smart.web.utils.TreeUtil;

@Service("docServ")
public class DocService extends MgrServiceImpl<TNDoc> {
	public static final String  RES_FLAG = "res";
	public static final String  AUTH_FLAG = "auth";
	@Autowired
	private DocDao docDao;
	
	@Autowired
	private DocResourceDao docResourceDao;
	
	@Autowired
	private OPAuthDao oPAuthDao;
	
	@Override
	public SmartResponse<String> save(TNDoc bean) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(null != bean) {
				TNDoc parentDoc = super.find(bean.getParentId()).getData();
				if(null != parentDoc) {
					bean.setSeqParentIds(parentDoc.getSeqParentIds()+"."+parentDoc.getId()+".");
					if(OrgType.DEPARTMENT.getValue().equals(bean.getType())) {
						bean.setSeqNames(parentDoc.getSeqNames()+">"+bean.getName());
					} else {
						bean.setSeqNames(bean.getName());
					}
				} else {
					bean.setSeqNames(bean.getName());
				}
				parentDoc = null;
				smartResp = super.save(bean);
				if(OP_SUCCESS.equals(smartResp.getResult())) {
					LoggerUtils.info(logger, "文档结构添加[成功]");
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
	public SmartResponse<String> update(TNDoc bean) throws ServiceException {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		try {
			if(null != bean) {
				
				TNDoc parentOrg = super.find(bean.getParentId()).getData();
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
				List<TNDoc> updateOrgs = new ArrayList<TNDoc>();
				if(isCascadeUpdate) {
					List<TNDoc>  orgs = super.findAll().getDatas();
					TNDoc org = getDoc(bean, orgs);
					copyBeanPropValue(bean, org);
					updateOrgs.add(org);
					Stack<TNDoc> stacks = new Stack<TNDoc>();
					stacks.push(bean);
					while(null != stacks && !stacks.isEmpty()) {
						parentOrg = stacks.pop();
						List<TNDoc> subOrgs = getSubOrg(orgs,parentOrg);
						if(null != subOrgs && subOrgs.size()>0) {
							for (TNDoc orgTmp : subOrgs) {
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
					
				}
			}
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return smartResp;
	}
	
	public TNDoc getDocByCode(String code) {
		return docDao.getDocByCode(code);
	}
	
	/**
	 * 获取对象
	 * @param bean
	 * @param orgs
	 * @return
	 */
	private TNDoc getDoc(TNDoc bean, List<TNDoc> orgs) {
		for (TNDoc org : orgs) {
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
	private void copyBeanPropValue(TNDoc source,TNDoc target) {
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
	protected List<TNDoc> getSubOrg(List<TNDoc> orgs ,TNDoc org) throws ServiceException {
		List<TNDoc> subOrgs = null;
		try {
			if(null != orgs && orgs.size()>0 && null != org) {
				subOrgs = new ArrayList<TNDoc>();
				for (TNDoc orgTmp : orgs) {
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
			}
		} catch (DaoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return smartResp;
	}
	
	/**
	 * 获取树
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<DocZTreeData> getZTree() throws ServiceException {
		SmartResponse<DocZTreeData> smartResp = new SmartResponse<DocZTreeData>();
		try {
			List<TreeProp> treeProps = getTree(null);
			if(null != treeProps && treeProps.size()>0) {
				ZTreeHelper<DocZTreeData> zTreeHelper = new ZTreeHelper<DocZTreeData>(DocZTreeData.class, treeProps);
				List<DocZTreeData> ztreeDatas = zTreeHelper.convert(null);
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
	 * 获取子组织机构
	 * @param orgIds
	 * @return
	 * @throws ServiceException
	 */
	public List<TreeProp> getTree(Collection<String> docIds) throws ServiceException {
		List<TreeProp> treeProps = null;
		try {
			List<TNDoc> docs = null;
			if(null != docIds && docIds.size()>0) {
				//orgs =  orgDao.find(StringUtil.list2Array(orgIds));
				docs =  docDao.find(StringUtils.list2Array(docIds));
			} else {
				//orgs = orgDao.findAll();
				docs = docDao.findAll();
			}
			if(null != docs && docs.size()>0) {
				TreeHelper<TNDoc> treeHelper = new TreeHelper<TNDoc>();
				List<TNDoc> docTrees = treeHelper.outPutTree(docs);
				treeProps = TreeUtil.Doc2TreeProp(docTrees);
				docTrees = null;
				treeHelper = null;
				docs = null;
			}
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return treeProps;
	}
	
	
	/**
	 * 选择文档权限列表
	 * @param roleId
	 * @return
	 * @throws ServiceException
	 */
	public SmartResponse<ZTreeData> selectDocResAuth(String roleId) throws ServiceException {
		SmartResponse<ZTreeData> smartResp = new SmartResponse<ZTreeData>();
		try {
			List<TNDoc> lists = docDao.queryContainAuths(null);
			if(null != lists && lists.size()>0) {
				List<TreeProp> treeProps = resAuth2TreeProp(lists);
				Map<String,List<TNOPAuth>> selectedResAuthMaps = null;
				if(StringUtils.isNotEmpty(roleId))
					selectedResAuthMaps = queryByRole(roleId);
				if(null != treeProps && treeProps.size()>0) {
					ZTreeHelper<ZTreeData> zTreeHelper = new ZTreeHelper<ZTreeData>(ZTreeData.class, treeProps);
					List<ZTreeData> ztrees = zTreeHelper.convert(null);
					if(null != ztrees && ztrees.size()>0) {
						smartResp.setResult(OP_SUCCESS);
						smartResp.setMsg(OP_SUCCESS_MSG);
						smartResp.setDatas(ztrees);
						//处理默认选择数据
						if(null != selectedResAuthMaps && selectedResAuthMaps.size()>0) {
							for (String key : selectedResAuthMaps.keySet()) {
								for (ZTreeData ztree : ztrees) {
									if(key.equals(ztree.getId())) {
										ztree.setChecked(true);
									} else if(key.equals(ztree.getpId())){
										List<TNOPAuth> selectedAuths = selectedResAuthMaps.get(key);
										if(null != selectedAuths && selectedAuths.size()>0) {
											for (TNOPAuth opAuth : selectedAuths) {
												if(opAuth.getValue().equals(ztree.getId())) {
													ztree.setChecked(true);
													break;
												}
											}
										}
									}
								}//for2
							}//for1
						}
					}
					selectedResAuthMaps = null;
					ztrees = null;
					zTreeHelper = null;
				}
			}
			lists = null;
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(),e.getCause());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return smartResp;
	}
	
	/**
	 * 从内存缓存中通过角色ID获取权限
	 */
	public Map<String, List<TNOPAuth>> queryByRole(String roleId) {
		Map<String,List<TNOPAuth>> authMaps = null;
		if(StringUtils.isEmpty(roleId)) 
			return authMaps;
		try {
			List<TNRoleDocResource> roleResources = getRoleResources();
			if(null != roleResources && !roleResources.isEmpty()) {
				authMaps = new HashMap<String, List<TNOPAuth>>();
				List<TNOPAuth> auths = null;
				for (TNRoleDocResource roleRes : roleResources) {
					if(roleRes.getRoleId().equals(roleId)) {
						if(StringUtils.isNotEmpty(roleRes.getOpAuths())) {
							auths = oPAuthDao.queryAuths(ArrayUtils.stringToArray(roleRes.getOpAuths(),","));
						}
						authMaps.put(roleRes.getResourceId(), auths);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return authMaps;
	}
	
	public List<TNRoleDocResource>  getRoleResources(){
		return docResourceDao.getRoleResources();
	}
	
	/**
	 * 资源权限列表转化为树形结构
	 * @param resList
	 * @return
	 * @throws ServiceException
	 */
	public List<TreeProp> resAuth2TreeProp(List<TNDoc> resList) throws ServiceException {
		List<TreeProp> trees = null;
		try {
			if(null != resList && resList.size()>0) {
				trees = new ArrayList<TreeProp>();
				TreeProp treeProp = null;
				for (TNDoc res: resList) {
					treeProp = new TreeProp();
					treeProp.setFlag(RES_FLAG);
					treeProp.setId(res.getId());
					treeProp.setName(res.getName());
					treeProp.setParentId("0");
					treeProp.setSortOrder(1);
					trees.add(treeProp);
					List<TNOPAuth> auths = res.getAuths();
					if(null != auths && auths.size()>0) {
						for (TNOPAuth auth : auths) {
							treeProp = new TreeProp();
							treeProp.setFlag(AUTH_FLAG);
							treeProp.setId(auth.getValue());
							treeProp.setName(auth.getName());
							treeProp.setParentId(res.getId());
							int sortOrder = 0;
							sortOrder = null == auth.getSortOrder()?0:auth.getSortOrder().intValue();
							treeProp.setSortOrder(sortOrder);
							trees.add(treeProp);
						}
					}//if
					auths = null;
				}//for
				treeProp = null;
			}//if
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return trees;
	}
	
	/**
	 * 树形结构转化为资源列表
	 * @param resList
	 * @return
	 */
	public List<TNDoc> treeProp2Res(List<TreeProp> treeProps) throws ServiceException {
		List<TNDoc> resources = null;
		try {
			if(null != treeProps && treeProps.size()>0) {
				resources = new ArrayList<TNDoc>();
				TNDoc res = null;
				TNOPAuth auth = null;
				List<TNOPAuth> auths = null;
				Map<String,List<TNOPAuth>> authMaps = new HashMap<String, List<TNOPAuth>>();
				for (TreeProp treeProp : treeProps) {
					if(RES_FLAG.equals(treeProp.getFlag())) {
						res = new TNDoc();
						res.setId(treeProp.getId());
						res.setName(treeProp.getName());
						resources.add(res);
					} else {
						auth = new TNOPAuth();
						auth.setValue(treeProp.getId());
						auth.setName(treeProp.getName());
						auths = authMaps.get(treeProp.getParentId());
						if(null == auths) {
							auths = new ArrayList<TNOPAuth>();
							authMaps.put(treeProp.getParentId(), auths);
						}
						auths.add(auth);
					}
				}
				if(resources.size()>0 && authMaps.size()>0) {
					for (TNDoc resTmp : resources) {
						auths = authMaps.get(resTmp.getId());
						if(null != auths && auths.size()>0) {
							resTmp.setAuths(auths);
						}
					}
				}
				authMaps = null;
				auths = null;
				auth = null;
				res = null;
			}
			treeProps = null;
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
		return resources;
	}
	
}



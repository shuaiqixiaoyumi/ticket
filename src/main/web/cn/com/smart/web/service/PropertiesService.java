package cn.com.smart.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.smart.service.impl.MgrServiceImpl;
import cn.com.smart.web.bean.entity.TPfproperties;
import cn.com.smart.web.dao.impl.PropertiesDao;
/**
 * 
 * @author zhanglb
 *
 */
@Service
public class PropertiesService extends MgrServiceImpl<TPfproperties> {
	@Autowired
	private PropertiesDao propertiesDao;
	@Autowired
	private SubPropertiesService subPropertiesService ;
	
	
	public TPfproperties getByCode(String code) {
		TPfproperties tPfproperties=  propertiesDao.getByCode(code);
		String formdatId = tPfproperties.getForm_data_id();
		
		tPfproperties.setList(subPropertiesService.getByFormDataId(formdatId));
		return tPfproperties;
	}
	
    public boolean isValidByCode(String code) {
    	boolean result = true;
    	TPfproperties tPfproperties = getByCode(code);
    	if(tPfproperties.getStatus() ==1) {
    		result =false;
    	}
    	return result;
    }
}

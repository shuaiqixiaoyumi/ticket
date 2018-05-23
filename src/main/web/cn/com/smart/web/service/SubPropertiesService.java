package cn.com.smart.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.smart.service.impl.MgrServiceImpl;
import cn.com.smart.web.bean.entity.TPfSubproperties;
import cn.com.smart.web.dao.impl.SubPropertiesDao;
/**
 * 
 * @author zhanglb
 *
 */
@Service
public class SubPropertiesService  extends MgrServiceImpl<TPfSubproperties> {
	
	@Autowired
	private SubPropertiesDao subPropertiesDao;

	public List<TPfSubproperties> getByFormDataId(String formDataId) {
		List<TPfSubproperties>  list=  subPropertiesDao.getByFormDataId(formDataId);
		
		return list;
	}
	
	public TPfSubproperties getByFormDataIdAndKeyname(String formDataId, String keyName) {
		TPfSubproperties tPfSubproperties = new TPfSubproperties();
		tPfSubproperties = subPropertiesDao.getByFormDataIdAndKeyname(formDataId, keyName);
		return tPfSubproperties;
	}
}

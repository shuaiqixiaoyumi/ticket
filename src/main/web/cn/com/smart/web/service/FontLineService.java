package cn.com.smart.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.smart.service.impl.MgrServiceImpl;
import cn.com.smart.web.bean.entity.TNFontLine;
import cn.com.smart.web.bean.entity.TNRoleUser;
import cn.com.smart.web.bean.entity.TNUser;
import cn.com.smart.web.dao.impl.FontLineDao;

@Service("fontLineServ")
public class FontLineService extends MgrServiceImpl<TNFontLine> {

	@Autowired
	private FontLineDao fonlineDao ;
	
	public List<TNFontLine> findByGroupId(String GroupId){
		List<TNFontLine> list =  fonlineDao.findByGroupId(GroupId);
		if(list !=null && list.size() >0) {
			return list;
		}
		return null;
	}

	public List<TNFontLine> getFontLine1ListByGroupId(String GroupId){ 
		List<TNFontLine> list =  fonlineDao.getFontLine1ListByGroupId(GroupId);
		if(list !=null && list.size() >0) {
			return list;
		}
		return null;
	}
	
	public boolean deleteByGroupId(String groupId) {
		return fonlineDao.deleteByGroupId(groupId);
	}
}

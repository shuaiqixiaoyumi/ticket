package cn.com.smart.flow.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snaker.engine.AssignmentHandler;
import org.snaker.engine.core.Execution;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.service.SmartContextService;
import cn.com.smart.web.bean.entity.TNFontLine;
import cn.com.smart.web.bean.entity.TNOrg;
import cn.com.smart.web.bean.entity.TNUser;
import cn.com.smart.web.service.FlowroleService;
import cn.com.smart.web.service.FontLineService;
import cn.com.smart.web.service.OrgService;
import cn.com.smart.web.service.UserService;

public class AcceptGroupHandle implements AssignmentHandler{
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrgService orgServ;
	
	@Autowired
	private FlowroleService flowroleServ;
	
	@Autowired
	private FontLineService fontlineServ ;
	public AcceptGroupHandle() {
		userService  = SmartContextService.find(UserService.class);
		orgServ =SmartContextService.find(OrgService.class);
		flowroleServ = SmartContextService.find(FlowroleService.class);
		fontlineServ = SmartContextService.find(FontLineService.class);
	}

	@Override
	public Object assign(Execution arg0) {
		arg0.getTask().getOrderId();
		String userId = arg0.getOrder().getLastUpdator();
		System.out.println("435252235"+userId);
		String actorIds = "";
		SmartResponse<TNUser> userInfo = userService.find(userId);
		TNUser tnUser = userInfo.getData();
		String orgId = tnUser.getOrgId();
		TNOrg org = orgServ.getDao().find(orgId);
		String bussinessGroupId =orgServ.getBusinessGroupByOrgId(orgId);
		System.out.println("bussinessGroupId"+bussinessGroupId);
		try {
			if(bussinessGroupId != null && !"".equals(bussinessGroupId)) {
				Map<String, Object> map = new HashMap<>();
				map.put("groupId", bussinessGroupId);
				List<TNFontLine> list =  fontlineServ.findByGroupId(bussinessGroupId) ;
				if(list!=null && list.size() >0 ) {
					for(TNFontLine tnf : list) {
						if(actorIds == null || "".equals(actorIds)) {
							 actorIds = "u_"+tnf.getUserId();
						 }else {
							 actorIds =actorIds + ",u_"+tnf.getUserId();
						 }
					}
					if(actorIds != null && !"".equals(actorIds)){
						System.out.println(111111);
						System.out.println(actorIds);
						return actorIds;
					}
					
				}else {
					return null;
				}
			}else {
				return null;
			}
		}catch(Exception e) {
			return null;
		}
		
		
		
		return null;
//		return "U_U150823211918840150";
	}

}

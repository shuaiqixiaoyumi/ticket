package cn.com.smart.flow;

import org.snaker.engine.AssignmentHandler;
import org.snaker.engine.core.Execution;

public interface MyAssignmentHandler extends AssignmentHandler{

	@Override
	public Object assign(Execution execution);
	
	public Object assignByParentUser(Execution execution, String userId);
	
	public Object assignByParentUser(Execution execution, String userId, String assigns);
	
	public Object assignByParentUser( String userId);

}

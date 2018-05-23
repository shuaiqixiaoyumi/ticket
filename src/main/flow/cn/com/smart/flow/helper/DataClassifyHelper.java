package cn.com.smart.flow.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snaker.engine.entity.WorkItem;
import org.springframework.beans.factory.annotation.Autowired;

import com.mixsmart.utils.StringUtils;

import cn.com.smart.flow.bean.DataClassify;
import cn.com.smart.flow.dao.FlowProcessDao;
import cn.com.smart.flow.sort.ISortClassify;
import cn.com.smart.flow.sort.SortClassifyBean;
import cn.com.smart.flow.sort.SortClassifyFactory;
import cn.com.smart.res.SQLResUtil;
import cn.com.smart.utils.DbBase;

/**
 * 数据分类助手
 * @author lmq
 * @version 1.0 
 * @since 
 *
 */
public class DataClassifyHelper {
	
	static DbBase db = new DbBase();

	/**
	 * 待办分类；按流程名称分类，相同的流程名称归为一类
	 * @param workItems
	 * @return
	 */
	public static List<DataClassify<WorkItem>> todoClassify(List<WorkItem> workItems) {
		List<DataClassify<WorkItem>> toDoClassifys = null;
		if(null == workItems || workItems.size()<1) {
			return toDoClassifys;
		}
		//TreeMap<String,List<WorkItem>> workItemMap = new TreeMap<String, List<WorkItem>>();
		ISortClassify<List<WorkItem>> sortClassify = new SortClassifyFactory<List<WorkItem>>();
		List<WorkItem> workItemTmps = null;
		for (WorkItem workItem : workItems) {
			String orderId = workItem.getOrderId();
			String taskname = getTaskNameByOrderId(orderId);
			if(taskname != null && !"".equals(taskname)) {
				workItem.setTaskName(taskname);
			}
			workItemTmps = sortClassify.get(workItem.getProcessName());
			if(null == workItemTmps) {
				workItemTmps = new ArrayList<WorkItem>();
				sortClassify.put(workItem.getProcessName(), workItemTmps);
			}
			workItemTmps.add(workItem);
		}
		DataClassify<WorkItem> todoClassify = null;
		toDoClassifys = new ArrayList<DataClassify<WorkItem>>();
		List<SortClassifyBean<List<WorkItem>>> beans = sortClassify.getList();
		for (SortClassifyBean<List<WorkItem>> bean : beans) {
			todoClassify = new DataClassify<WorkItem>();
			todoClassify.setId(StringUtils.createSerialNum());
			todoClassify.setName(bean.getName());
			todoClassify.setDatas(bean.getEntity());
			toDoClassifys.add(todoClassify);
		}
		sortClassify.destory();
		sortClassify = null;
		todoClassify = null;
		workItemTmps = null;
		return toDoClassifys;
	}
	
	@SuppressWarnings("unused")
	public static String getTaskNameByOrderId(String OrderId) {
		db = new DbBase();
		String sql = "select display_name from t_wf_task where order_id = ?";
		List<Map<String, Object>> list = db.queryForList(sql, new Object[] {OrderId});
		if(list != null && list.size() >0) {
			return list.get(0).get("display_name")+"";
		}else {
			return "已结束";
		}
	}
	
	/**
	 * 待办分类；按流程名称分类，相同的流程名称归为一类
	 * @param datas
	 * @return
	 */
	public static List<DataClassify<Object>> orderClassify(List<Object> datas) {
		List<DataClassify<Object>> dataClassifys = null;
		if(null == datas || datas.size()<1) {
			return dataClassifys;
		}
		ISortClassify<List<Object>> sortClassify = new SortClassifyFactory<List<Object>>();
		List<Object> objs = null;
		for (Object obj : datas) {
			Object[] objArray = (Object[])obj;
			objs = sortClassify.get(objArray[2].toString());
			if(null == objs) {
				objs = new ArrayList<Object>();
				sortClassify.put(objArray[2].toString(), objs);
			}
			objs.add(obj);
		}
		DataClassify<Object> dataClassify = null;
		dataClassifys = new ArrayList<DataClassify<Object>>();
		List<SortClassifyBean<List<Object>>> beans = sortClassify.getList();
		for (SortClassifyBean<List<Object>> bean : beans) {
			dataClassify = new DataClassify<Object>();
			dataClassify.setId(StringUtils.createSerialNum());
			dataClassify.setName(bean.getName());
			dataClassify.setDatas(bean.getEntity());
			dataClassifys.add(dataClassify);
		}
		dataClassify = null;
		sortClassify.destory();
		sortClassify = null;
		return dataClassifys;
	}
	
}

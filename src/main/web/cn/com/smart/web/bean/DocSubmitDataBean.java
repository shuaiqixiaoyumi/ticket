package cn.com.smart.web.bean;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.com.smart.bean.TreeProp;

/**
 * 提交数据的对象;主要是用于提交文档属性的数据
 * @author zhanglb
 * @version 1.0 2018年4月28日
 * @since 1.0
 *
 */
@Component
public class DocSubmitDataBean {
	private List<TreeProp> treePropsDoc;

	/**
	 * 获取树形属性对象集合
	 * @return 返回树形结构集合
	 */
	public List<TreeProp> getTreePropsDoc() {
		return treePropsDoc;
	}

	/**
	 * 设置树形属性的对象集合
	 * @param treePropsDoc 设置树形集合
	 */
	public void setTreePropsDoc(List<TreeProp> treePropsDoc) {
		this.treePropsDoc = treePropsDoc;
	}
}

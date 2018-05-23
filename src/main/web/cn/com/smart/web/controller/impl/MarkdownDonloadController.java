package cn.com.smart.web.controller.impl;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itextpdf.text.pdf.codec.Base64.InputStream;
import com.itextpdf.text.pdf.codec.Base64.OutputStream;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.utils.DbBase;
import cn.com.smart.utils.FileUtil;
import cn.com.smart.web.controller.base.BaseController;

@Controller
@RequestMapping("/markdowndonloadcontroller")
public class MarkdownDonloadController extends BaseController {

	DbBase db = new DbBase();
	
	 @RequestMapping("/markownDownload")
	 @ResponseBody
	public SmartResponse<String> markownDownload(@RequestParam Map<String, Object> map,HttpServletRequest request, HttpServletResponse response){
		String uuid = map.get("uuid")+"";
		Map<String, String> sqlMap = new HashMap<>();
		sqlMap= getContent(uuid, request);
		String contents = sqlMap.get("contents");
		String filename = sqlMap.get("filename").substring(0, sqlMap.get("filename").lastIndexOf("."));
		String HTML = MdToHtml(contents);
		System.out.println(HTML);
//		try {
//			//获得请求文件名
////			String filename = request.getParameter("filename");
////			System.out.println(filename);
//			
//			//设置文件MIME类型
//			response.setContentType("html");
//			//设置Content-Disposition
//			response.setHeader("Content-Disposition", "attachment;filename="+filename);
//			//读取目标文件，通过response将目标文件写到客户端
//			//获取目标文件的绝对路径
//			String fullFileName = request.getServletContext().getRealPath("/download/" + filename);
//			//System.out.println(fullFileName);
//			//读取文件
//			FileInputStream in = new FileInputStream(fullFileName);
//			ServletOutputStream out = response.getOutputStream();
//			
//			//写文件
//			int b;
//			while((b=in.read())!= -1)
//			{
//				out.write(b);
//			}
//			
//			in.close();
//			out.close();
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
		
		return null;
		
	}
	 
	 public Map<String, String> getContent(String uuid,HttpServletRequest request) {
	    	String sql = "select * from t_n_markdown where id = ?";
	    	String contents ="";
	    	Map<String, String> map = new HashMap<>();
	    	List<Map<String, Object>> list = db.queryForList(sql, new Object[] {uuid});
	    	if(list.size() >0) {
	    		String path = list.get(0).get("file_path")+"";
	    		FileUtil fileutile = new  FileUtil();
	    		path = request.getSession().getServletContext()
				.getRealPath(path+"/"+uuid+".md");
				contents = fileutile.readFile(path, true);
				String filename = list.get(0).get("file_name")+"";
				map.put("contents", contents);
				map.put("filename", filename);
			}
	    	return map;
	    }
	 
	 /**
	  * md转html
	  * @param mdstr
	  * @return
	  */
	 public String MdToHtml(String mdstr) {
			MutableDataSet options = new MutableDataSet();
			Parser parser = Parser.builder(options).build();
	        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
	        Node document = parser.parse(mdstr);
	        String html = renderer.render(document); 
	        return html;
		}
}

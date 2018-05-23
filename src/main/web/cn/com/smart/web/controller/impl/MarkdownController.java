package cn.com.smart.web.controller.impl;

import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.json.JSONArray;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zefer.pd4ml.PD4Constants;
import org.zefer.pd4ml.PD4ML;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.mixsmart.utils.StringUtils;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;

import cn.com.smart.bean.SmartResponse;
import cn.com.smart.flow.service.FlowFormService;
import cn.com.smart.init.config.InitSysConfig;
import cn.com.smart.service.SmartContextService;
import cn.com.smart.utils.DbBase;
import cn.com.smart.utils.FileUtil;
import cn.com.smart.web.bean.UserInfo;
import cn.com.smart.web.bean.entity.TNDoc;
import cn.com.smart.web.bean.entity.TNMarkdown;
import cn.com.smart.web.bean.entity.TPfproperties;
import cn.com.smart.web.constant.IActionConstant;
import cn.com.smart.web.controller.base.BaseController;
import cn.com.smart.web.helper.HttpRequestHelper;
import cn.com.smart.web.service.DocService;
import cn.com.smart.web.service.MarkdownService;
import cn.com.smart.web.service.PropertiesService;
import cn.com.smart.web.service.SubPropertiesService;



/**
 * 上传保存markdown文件及信息
 * @author lmq
 *
 */
@Controller
@RequestMapping("/markdownController")
public class MarkdownController extends BaseController{
	
	
	private MarkdownService markdownServ;
	
	
	private DocService docServ;
	
	
    private PropertiesService propertiesService;
    
    
    private SubPropertiesService subPropertiesService;
	
	DbBase db = new DbBase();
	
	public String remotePath =InitSysConfig.getInstance().getValue("remotePath");//远程库路径
    public String gitName = InitSysConfig.getInstance().getValue("gitName");//远程库路径
	public String gitPwd = InitSysConfig.getInstance().getValue("gitPwd");//远程库路径
    public String localPath = "";//下载已有仓库到本地路径
    public String initPath = "";//本地路径新建
    
    public MarkdownController() {
    	markdownServ =SmartContextService.find(MarkdownService.class);
    	docServ =SmartContextService.find(DocService.class);
    }
    
    @RequestMapping("/heartbeat")
    @ResponseBody
    public SmartResponse<String> heartbeat(){
    	SmartResponse<String> smartResp = new SmartResponse<String>();
    	
		smartResp.setResult(OP_SUCCESS);
		smartResp.setMsg("200");
 	return smartResp;
    }
    
     @RequestMapping("/initNewuuid")
    @ResponseBody
    public SmartResponse<String> initNewuuid(HttpServletRequest request) {
    	SmartResponse<String> smartResp = new SmartResponse<String>();
    	
    		smartResp.setResult(OP_SUCCESS);
    		smartResp.setMsg(UUID.randomUUID().toString());
     	return smartResp;
    }
	
    @RequestMapping("/view")
    @ResponseBody
    public SmartResponse<String> view(@RequestParam Map<String, Object> map,HttpServletRequest request) {
    	SmartResponse<String> smartResp = new SmartResponse<String>();
    	String uuid = map.get("uuid")+"";
    	String sql = "select * from t_n_markdown where id = ?";
    	List<Map<String, Object>> list = db.queryForList(sql, new Object[] {uuid});
    	if(list.size() >0) {
    		String path = list.get(0).get("file_path")+"";
    		FileUtil fileutile = new  FileUtil();
    		path = request.getSession().getServletContext()
			.getRealPath(path+"/"+uuid+".md");
			String contents = fileutile.readFile(path, true);
			list.get(0).put("contents", contents);
			list.get(0).put("copyurl", getCopyUrl(request, uuid));
    		smartResp.setResult(OP_SUCCESS);
    		smartResp.setMap(list.get(0));
    	}
    	return smartResp;
    }
    
    public String getCopyUrl(HttpServletRequest request, String uuid) {
    	String copyurl = "";
    	String path = HttpRequestHelper.getDomain(request);
    	if(path != null && !"".equals(path)) {
    		copyurl = path+"index?currenturl=index/viewpage?uuid="+uuid;
    	}
    	return copyurl;
    }
    
    @RequestMapping("/update")
	@ResponseBody
	public SmartResponse<String> update(@RequestParam Map<String, Object> map,HttpServletRequest request) {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		
		try {
			String userId = getUserInfoFromSession(request).getId();
			String uuid = map.get("uuid")+"";
			smartResp.setMsg(uuid);
			String fileTitle = map.get("title")+"";
			String fileRemark = map.get("summary")+"";
			String contents = map.get("contents")+"";
			String filesize = contents.length()+"";
			String fileType = map.get("fileType")+"";
			String power = map.get("power")+"";
			System.out.println(fileType);
//			Clone(request);
			Pull(request);
			String file_path = getMarkdownDir(fileType, uuid,request);
			StringBuffer sql = new StringBuffer();
			sql.append("update t_n_markdown ");
			sql.append(" set file_title = ?,  file_name = ?,  file_path = ?,  file_size = ?, "
					+ " file_suffix = ?,  file_type = ?,  file_remark = ?,  user_id = ?, power=?");
			sql.append(" where id = ? ");
//			String sqlpath =request.getContextPath()+"/web_upload/markdown/"+"/"+fileType+"/"+uuid;
			String sqlpath ="/web_upload/markdown/"+"/"+fileType+"/"+uuid;
			db.saveOrUpdate(sql.toString(), new Object[] { fileTitle, fileTitle+".md", sqlpath, filesize, "md", fileType, fileRemark, userId, power, uuid});
			FileUtil fileutile = new  FileUtil();
			System.out.println(contents);
			System.out.println(file_path+"/"+uuid+".md");
			fileutile.writeFile(contents, file_path+"/"+uuid+".md", true);
			Push(request);
			uPdatePictureType(uuid);
			updateTnDoc(uuid);
		}catch(Exception e) {
			e.printStackTrace();
		}
		smartResp.setResult(OP_SUCCESS);
		
//		return Msg.success().add("newblog_id", uuid);
//		Map<String, Object> map3 = new HashMap<>();
//		map2.put("newblog_id", uuid);
		return smartResp;
		
	}
    
    public void updateTnDoc(String uuid) {
    	TNMarkdown tnMarkdown = markdownServ.getDao().find(uuid);
    	String fileType = tnMarkdown.getFileType();
    	String fileName = tnMarkdown.getFileName();
    	TNDoc tndoc = docServ.getDocByCode(uuid);
    	TNDoc pretndoc = docServ.getDocByCode(fileType);
    	if(tndoc != null && pretndoc != null) {
    		tndoc.setParentId(pretndoc.getId());
    		tndoc.setName(fileName);
    		tndoc.setSeqParentIds(pretndoc.getSeqParentIds()+pretndoc.getId()+".");
    		tndoc.setSeqNames(pretndoc.getSeqNames()+">"+tndoc.getName());
    		docServ.update(tndoc);
    	}else if(tndoc ==null && pretndoc != null) {
    		tndoc = new TNDoc();
    		tndoc.setSortOrder(2);
    		tndoc.setCode(uuid);
    		tndoc.setType("doc");
    		tndoc.setParentId(pretndoc.getId());
    		tndoc.setName(fileName);
    		tndoc.setSeqParentIds(pretndoc.getSeqParentIds()+pretndoc.getId()+".");
    		tndoc.setSeqNames(pretndoc.getSeqNames()+">"+tndoc.getName());
    		tndoc.setCreateTime(new Date());
    		docServ.save(tndoc);
    	}
    }
    
    public void saveTnDoc(String uuid) {
    	TNMarkdown tnMarkdown = markdownServ.getDao().find(uuid);
    	String fileType = tnMarkdown.getFileType();
    	String fileName = tnMarkdown.getFileName();
    	TNDoc tndoc = docServ.getDocByCode(uuid);
    	TNDoc pretndoc = docServ.getDocByCode(fileType);
    	if(tndoc != null && pretndoc != null) {
    		tndoc.setParentId(pretndoc.getId());
    		tndoc.setName(fileName);
    		tndoc.setSeqParentIds(pretndoc.getSeqParentIds()+pretndoc.getId()+".");
    		tndoc.setSeqNames(pretndoc.getSeqNames()+">"+tndoc.getName());
    		docServ.update(tndoc);
    	}else if(tndoc ==null && pretndoc != null) {
    		tndoc = new TNDoc();
    		tndoc.setSortOrder(2);
    		tndoc.setCode(uuid);
    		tndoc.setType("doc");
    		tndoc.setParentId(pretndoc.getId());
    		tndoc.setName(fileName);
    		tndoc.setSeqParentIds(pretndoc.getSeqParentIds()+pretndoc.getId()+".");
    		tndoc.setSeqNames(pretndoc.getSeqNames()+">"+tndoc.getName());
    		tndoc.setCreateTime(new Date());
    		docServ.save(tndoc);
    	}
    }
    
	@SuppressWarnings("static-access")
	@RequestMapping("/save")
	@ResponseBody
	public SmartResponse<String> save(@RequestParam Map<String, Object> map,HttpServletRequest request) {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		
		try {
			String userId = getUserInfoFromSession(request).getId();
			String uuid = map.get("uuid")+"";
			smartResp.setMsg(uuid);
			String fileTitle = map.get("title")+"";
			String fileRemark = map.get("summary")+"";
			String contents = map.get("contents")+"";
			String filesize = contents.length()+"";
			String fileType = map.get("fileType")+"";
			String power = map.get("power")+"";
			System.out.println(fileType);
//			Clone(request);
			Pull(request);
			String file_path = getMarkdownDir(fileType, uuid, request);
			StringBuffer sql = new StringBuffer();
//			String sqlpath =request.getContextPath()+"/web_upload/markdown/"+"/"+fileType+"/"+uuid;
			String sqlpath ="/web_upload/markdown/"+"/"+fileType+"/"+uuid;
			sql.append("insert into t_n_markdown ");
			sql.append("(id,create_time, file_title, file_name, file_path, file_size, file_suffix, file_type, file_remark, user_id, power) ");
			sql.append(" values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			db.save(sql.toString(), new Object[] {uuid, new Date(), fileTitle, fileTitle+".md", sqlpath, filesize, "md", fileType, fileRemark, userId,power});
			FileUtil fileutile = new  FileUtil();
			fileutile.writeFile(contents, file_path+"/"+uuid+".md", true);
			Push(request);
			uPdatePictureType(uuid);
			saveTnDoc(uuid);
		}catch(Exception e) {
			e.printStackTrace();
		}
		smartResp.setResult(OP_SUCCESS);
		return smartResp;
		
	}
	
	
	public SmartResponse<String> deletedoc(@RequestParam Map<String, Object> map,HttpServletRequest request) {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		String uuid = map.get("uuid")+"";
		TNMarkdown tnMarkdown = markdownServ.getDao().find(uuid);
    	if(tnMarkdown !=null) {
    		FileUtil fileutile = new  FileUtil();
    		String fileType = tnMarkdown.getFileType();
    		String file_path = getMarkdownDir(fileType, uuid, request);
			fileutile.deleteFile(file_path);
			markdownServ.delete(uuid);
    	}
		return smartResp;
	}
	
	public void uPdatePictureType(String uuid) {
		String sql = "update t_n_markpicture set  file_type = 0 where uuid = ?";
		db.saveOrUpdate(sql, new Object[] {uuid});
	}
	
	/**
	 * 获取存放markdown文件的路径 <br />
	 * 从配置文件中获取,属性为:upload.other.dir
	 * @return 返回其他文件存放目录
	 */
	protected String getMarkdownDir(String fileType, String uuid,HttpServletRequest request) {
//		String dirPath = InitSysConfig.getInstance().getValue("upload.markdown.dir")+"/"+fileType+"/"+uuid;
		String dirPath  = request.getSession().getServletContext()
				.getRealPath("/web_upload/markdown/")+"/"+fileType+"/"+uuid;
		File dir = new File(dirPath);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		dir = null;
		return dirPath;
	}
	
	
	
	@Test
	public void Clone(HttpServletRequest request ) throws IOException, GitAPIException {
        //设置远程服务器上的用户名和密码
        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider =new
                UsernamePasswordCredentialsProvider(gitName,gitPwd);
        localPath = request.getSession().getServletContext()
				.getRealPath("/web_upload/markdown/");
//        localPath = request.getContextPath()+"/web_upload/markdown/";
//        localPath = InitSysConfig.getInstance().getValue("upload.markdown.dir");
        //克隆代码库命令
        CloneCommand cloneCommand = Git.cloneRepository();
        File dir = new File(localPath);
        if(!dir.exists()) {
        	Git git= cloneCommand.setURI(remotePath) //设置远程URI
                    .setBranch("master") //设置clone下来的分支
                    .setDirectory(new File(localPath)) //设置下载存放路径
                    .setCredentialsProvider(usernamePasswordCredentialsProvider) //设置权限验证
                    .call();

            System.out.print(git.tag());
            git.close();
        }
       
    }
	
	/**
	    * 拉取远程仓库内容到本地
	    */
	@Test
    public void Pull(HttpServletRequest request) throws IOException, GitAPIException {

        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider =new
                UsernamePasswordCredentialsProvider(gitName,gitPwd);
//        localPath = request.getContextPath()+"/web_upload/markdown/";
        localPath = request.getSession().getServletContext()
				.getRealPath("/web_upload/markdown/");
//        localPath = InitSysConfig.getInstance().getValue("upload.markdown.dir");
        //git仓库地址
        Git git = new Git(new FileRepository(localPath+"/.git"));
        git.pull().setRemoteBranchName("master").
                setCredentialsProvider(usernamePasswordCredentialsProvider).call();
    }
	
	/**
	    * push本地代码到远程仓库地址
	    */
	@Test
    public void Push(HttpServletRequest request) throws IOException, JGitInternalException,
            GitAPIException {

        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider =new
                UsernamePasswordCredentialsProvider(gitName,gitPwd);
        //git仓库地址
//        localPath = InitSysConfig.getInstance().getValue("upload.markdown.dir");
//        localPath = request.getContextPath()+"/web_upload/markdown/";
        localPath = request.getSession().getServletContext()
				.getRealPath("/web_upload/markdown/");
        Git git = new Git(new FileRepository(localPath+"/.git"));   
        System.out.println(git.status().call().isClean());
        git.add().addFilepattern(".").call();
        git.add().setUpdate(true).addFilepattern(".").call();
        git.commit().setMessage("test jGit").call();
        git.push().setRemote("origin").setCredentialsProvider(usernamePasswordCredentialsProvider).call();
        git.close();
    }

	/**
	 * 上传图片
	 * @param request
	 * @param response
	 * @param file
	 */
	@RequestMapping(value="/uploadimg",method=RequestMethod.POST)
	public void ImgUpload(HttpServletRequest request,	HttpServletResponse response,
			@RequestParam(value = "editormd-image-file", required = false) MultipartFile file){
		SmartResponse<String> smartResp = new SmartResponse<String>();
		String docuuid = request.getParameter("uuid")+"";
		String uuid = UUID.randomUUID().toString();
		String userId = getUserInfoFromSession(request).getId();
		String fileType  = "picture";
		System.out.println(request.getSession().getServletContext().getRealPath("/web_upload/markdown/"+fileType+"/"+docuuid));
		try{
			request.setCharacterEncoding( "utf-8" );
			response.setHeader( "Content-Type" , "text/html" );
			Clone(request);
			Pull(request);
//			String rootPath = getMDPictureDir(fileType, uuid);
			
			String rootPath = request.getSession().getServletContext()
							.getRealPath("/web_upload/markdown/"+fileType+"/"+docuuid);
			
			System.out.println("UploadController/文件路径："+rootPath);
			/**
			 * 文件路径不存在则需要创建文件路径
			 */
			File filePath=new File(rootPath);
			if(!filePath.exists()){
				filePath.mkdirs();
			}
			System.out.println("UploadController/上传文件名："+file.getOriginalFilename());
			String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			//最终文件名
			File realFile=new File(rootPath+File.separator+uuid+suffix);
			FileUtils.copyInputStreamToFile(file.getInputStream(), realFile);
			System.out.println("UploadController/文件全路径："+realFile);
			Push(request);
			
			String resultpath = request.getContextPath()+"/web_upload/markdown/"+fileType+File.separator+docuuid+File.separator+uuid+suffix;
			resultpath = resultpath.replace("\\", "/");
			String sql = "insert into t_n_markpicture (id, create_time, file_title, file_name, file_path, uuid, file_type, user_id)"
					+ " values (?,?,?,?,?,?,?,?)";
			db.saveOrUpdate(sql, new Object[] {uuid, new Date(), file.getOriginalFilename(), uuid+file.getOriginalFilename(), resultpath, docuuid, 1, userId});
			//下面response返回的json格式是editor.md所限制的，规范输出就OK
			response.getWriter().write( "{\"success\": 1, \"message\":\"上传成功\",\"url\":\""+resultpath +  "\"}" );
		} catch (Exception e) {
	          try {
	        	  e.printStackTrace();
	              response.getWriter().write( "{\"success\":0}" );
	          } catch (IOException e1) {
	              e1.printStackTrace();
	          }
	      }
	}
	
	/**
	 * 上传图片
	 * @param request
	 * @param response
	 * @param file
	 */
	@RequestMapping(value="/uploadattach",method=RequestMethod.POST)
	public void AttachUpload(HttpServletRequest request,	HttpServletResponse response,
			@RequestParam(value = "editormd-attach-file", required = false) MultipartFile file){
		SmartResponse<String> smartResp = new SmartResponse<String>();
		String docuuid = request.getParameter("uuid")+"";
		String uuid = UUID.randomUUID().toString();
		String userId = getUserInfoFromSession(request).getId();
		String fileType  = "picture";
		System.out.println(request.getSession().getServletContext().getRealPath("/web_upload/markdown/"+fileType+"/"+docuuid));
		try{
			request.setCharacterEncoding( "utf-8" );
			response.setHeader( "Content-Type" , "text/html" );
			Clone(request);
			Pull(request);
//			String rootPath = getMDPictureDir(fileType, uuid);
			
			String rootPath = request.getSession().getServletContext()
							.getRealPath("/web_upload/markdown/"+fileType+"/"+docuuid);
			
			System.out.println("UploadController/文件路径："+rootPath);
			/**
			 * 文件路径不存在则需要创建文件路径
			 */
			File filePath=new File(rootPath);
			if(!filePath.exists()){
				filePath.mkdirs();
			}
			System.out.println("UploadController/上传文件名："+file.getOriginalFilename());
			String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			//最终文件名
			File realFile=new File(rootPath+File.separator+uuid+suffix);
			FileUtils.copyInputStreamToFile(file.getInputStream(), realFile);
			System.out.println("UploadController/文件全路径："+realFile);
			Push(request);
			
			String resultpath = request.getContextPath()+"/web_upload/markdown/"+fileType+File.separator+docuuid+File.separator+uuid+suffix;
			resultpath = resultpath.replace("\\", "/");
			String sql = "insert into t_n_markpicture (id, create_time, file_title, file_name, file_path, uuid, file_type, user_id)"
					+ " values (?,?,?,?,?,?,?,?)";
			db.saveOrUpdate(sql, new Object[] {uuid, new Date(), file.getOriginalFilename(), uuid+file.getOriginalFilename(), resultpath, docuuid, 1, userId});
			//下面response返回的json格式是editor.md所限制的，规范输出就OK
			response.getWriter().write( "{\"success\": 1, \"message\":\"上传成功\",\"url\":\""+resultpath +  "\"}" );
		} catch (Exception e) {
	          try {
	        	  e.printStackTrace();
	              response.getWriter().write( "{\"success\":0}" );
	          } catch (IOException e1) {
	              e1.printStackTrace();
	          }
	      }
	}
	
	/**
	 * 获取存放markdown文件的路径 <br />
	 * 从配置文件中获取,属性为:upload.other.dir
	 * @return 返回其他文件存放目录
	 */
	protected String getMDPictureDir(String fileType, String uuid) {
		String dirPath = InitSysConfig.getInstance().getValue("upload.markdown.dir")+"/"+fileType;
		File dir = new File(dirPath);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		dir = null;
		return dirPath;
	}
	
	
	 @RequestMapping("/getSystemType")
    @ResponseBody
    public SmartResponse<String> getSystemType(HttpServletRequest request) {
    	SmartResponse<String> smartResp = new SmartResponse<String>();
    	String sql = "select * from t_n_marktype";
    	List<Map<String, Object>> list = db.queryForList(sql);
    	if(list.size() >0) {
    		int i =0;
    		String msg = "";
    		for(Map<String,Object> map : list) {
    			if(i==0) {
    				msg = map.get("name")+"";
    			}else {
    				msg = msg +"," +map.get("name")+"";
    			}
    		}
    		smartResp.setResult(OP_SUCCESS);
    		smartResp.setMsg(msg);
    	}
    	return smartResp;
    }
	 
	 	@RequestMapping("/getViewModle")
	    @ResponseBody
	    public Map<String,Object> getViewModle(@RequestParam Map<String, Object> map, HttpServletRequest request) {
	    	SmartResponse<String> smartResp = new SmartResponse<String>();
	    	Map<String, Object> remap = new HashMap<>();
	    	String userid = getUserInfoFromSession(request).getId();
	    	UserInfo userInfo = (UserInfo) request.getSession().getAttribute(IActionConstant.SESSION_USER_KEY);
	    	boolean isPublic = true;
			if(!checkByUserIdAndRoleName("内部文档成员",userInfo.getId()) && !"一线值班人".equals(userInfo.getDeptName()) && !userInfo.getUsername().equals("admin")) {
				isPublic = false; 
			}
	    	String ispowersql = "";
	    	try {
//		    	List<TNMarkdown> listentity = new ArrayList<TNMarkdown>();
		    	String file_type =  map.get("fileType")+"";
		    	file_type = URLDecoder.decode(file_type, "UTF-8"); 
		    	String sql = "select * from t_n_markdown where ";
		    	if(!isPublic ) {
		    		sql = sql + " power = 1 and ";
				}
		    	sql =sql + " file_type = ? order by create_time desc";
		    	List<Map<String, Object>> list = db.queryForList(sql, new Object[] {file_type});
		    	if(list.size() >0) {
		    		remap.put("result", "1");
		    		remap.put("msg", list);
		    	}
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    	
	    	return remap;
	    }
	 	
	 	@RequestMapping("/getSearchResultView")
	    @ResponseBody
	    public Map<String,Object> getSearchResultView(@RequestParam Map<String, Object> map, HttpServletRequest request) {
	    	SmartResponse<String> smartResp = new SmartResponse<String>();
	    	Map<String, Object> remap = new HashMap<>();
	    	try {
		    	String searchInput =  map.get("searchInput")+"";
//		    	searchInput = URLDecoder.decode(searchInput, "UTF-8");
		    	System.out.println(map.get("pn"));
		    	int searchPageNumber = Integer.parseInt(map.get("pn")+"") ;
		    	UserInfo userInfo = (UserInfo) request.getSession().getAttribute(IActionConstant.SESSION_USER_KEY);
				boolean isPublic = true;
				System.out.println(userInfo.getDeptName());
				if(!checkByUserIdAndRoleName("内部文档成员",userInfo.getId()) && !"一线值班人".equals(userInfo.getDeptName()) && !userInfo.getUsername().equals("admin")) {
					isPublic = false; 
				}
				System.out.println(isPublic);
		    	String sql = "select * from t_n_markdown where ";
		    	if(!isPublic) {
		    		sql = sql +" power = 1 and ";
		    	}
		    	sql = sql + " ( file_name like concat('%',?,'%') or file_type  like concat('%',?,'%')) order by create_time desc  limit ?, 5 ";
		    	List<Map<String, Object>> list = db.queryForList(sql, new Object[] {searchInput, searchInput, (searchPageNumber-1)*5});
		    	String countsql = "select count(1) as totalsize from t_n_markdown where ";
		    	if(!isPublic) {
		    		countsql = countsql +" power = 1  and ";
		    	}
		    	countsql = countsql + "( file_name  like concat('%',?,'%')  or file_type  like concat('%',?,'%') )";
		    	System.out.println(countsql);
		    	List<Map<String, Object>> countlist = db.queryForList(countsql, new Object[] {searchInput,searchInput});
		    	
		    	if(list.size() >0) {
		    		remap.put("result", "1");
		    		remap.put("msg", list);
		    		remap.put("msgsize", list.size());
		    		remap.put("pageNum", searchPageNumber);
		    		int totalsize = Integer.parseInt(countlist.get(0).get("totalsize")+"");
		    		remap.put("totalsize", totalsize);
		    		int totalpages = totalsize /5;
		    		System.out.println(totalsize);
		    		if( totalpages*5 < totalsize) {
		    			totalpages++;
		    		}
		    		remap.put("totalpages",totalpages);
		    		boolean hasPreviousPage = true;
		    		boolean hasNextPage = true;
		    		if(searchPageNumber == 1) {
		    			hasPreviousPage = false;
		    		}
		    		if(searchPageNumber == totalpages) {
		    			hasNextPage = false;
		    		}
		    		remap.put("hasPreviousPage",hasPreviousPage);
		    		remap.put("hasNextPage",hasNextPage);
		    		
		    		if(totalpages >5 && searchPageNumber-3>0) {
		    			JSONArray jsArr = new JSONArray();
		    			if(searchPageNumber+2>totalpages) {
		    				for(int i=4;i>=0;i--) {
		    					jsArr.put(searchPageNumber-i);
		    				}
		    			}else {
		    				for(int i=-2;i<3;i++) {
		    					jsArr.put(searchPageNumber+i);
		    				}
		    			}
		    			remap.put("navigatepageNums",jsArr);
		    		}else {
		    			JSONArray jsArr = new JSONArray();
		    			for(int i=0;i<totalpages;i++) {
		    				jsArr.put(i+1);
		    				if(i==4) {
		    					break;
		    				}
		    			}
		    			remap.put("navigatepageNums",jsArr);
		    		}
		    	}
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    	
	    	return remap;
	    }
	 	
	 	static final MutableDataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
	            Extensions.ALL & ~(Extensions.ANCHORLINKS | Extensions.EXTANCHORLINKS_WRAP)
	    ).toMutable();
	 	
	 	 @RequestMapping("/markownDownload")
		 @ResponseBody
		public SmartResponse<String> markownDownload(@RequestParam Map<String, Object> map,HttpServletRequest request, HttpServletResponse response){
			String uuid = map.get("uuid")+"";
			Map<String, String> sqlMap = new HashMap<>();
			sqlMap= getContent(uuid, request);
			String contents = sqlMap.get("contents");
//			contents = map.get("inhtml")+"";
			String filename = sqlMap.get("filename").substring(0, sqlMap.get("filename").lastIndexOf("."));
			String HTML = MdToHtml(contents);
			System.out.println(HTML);
			//转换1
//			final Parser PARSER = Parser.builder(OPTIONS).build();
//			 final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();
//
//	        Node document = PARSER.parse(HTML);
//	        String html = RENDERER.render(document);
//			PdfConverterExtension.exportToPdf("D:\\test\\test.pdf", HTML,"", OPTIONS);

			//转换2
	        this.htmlCodeComeString(HTML, "D://test//iText_2.pdf");
			
			//转换3
//			File pdfFile = new File("D://test//iText_2.pdf");
//			StringReader strReader = new StringReader(HTML);
//			try {
//				this.generatePDF_1(pdfFile, strReader);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		    
//			try {
//				
//				//获得请求文件名
////				String filename = request.getParameter("filename");
////				System.out.println(filename);
//				
//				//设置文件MIME类型
//				response.setContentType("html");
//				//设置Content-Disposition
//				response.setHeader("Content-Disposition", "attachment;filename="+filename+".html");
//				//读取目标文件，通过response将目标文件写到客户端
//				//获取目标文件的绝对路径
//				String fullFileName = request.getServletContext().getRealPath("/download/" + filename);
//				//System.out.println(fullFileName);
//				//读取文件
//				FileInputStream in = new FileInputStream(fullFileName);
//				ServletOutputStream out = response.getOutputStream();
//				
//				//写文件
//				int b;
//				while((b=in.read())!= -1)
//				{
//					out.write(b);
//				}
//				
//				in.close();
//				out.close();
//			}catch(Exception e) {
//				e.printStackTrace();
//			}
//			
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
		        html = "<html>"+html+"</html>";
		        return html;
			}
		 
		 
		 public void htmlCodeComeFromFile(String filePath, String pdfPath) {
				Document document = new Document();
				try {
					StyleSheet st = new StyleSheet();
					st.loadTagStyle("body", "leading", "16,0");
					PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
					document.open();
					ArrayList p = (ArrayList) HTMLWorker.parseToList(new FileReader(filePath), st);
					for(int k = 0; k < p.size(); ++k) {
						document.add((Element)p.get(k));
					}
					document.close();
					System.out.println("文档创建成功");
				}catch(Exception e) {
					e.printStackTrace();
				}
			}

			public void htmlCodeComeString(String htmlCode, String pdfPath) {
				Document doc = new Document(PageSize.A4);
				try {
					PdfWriter.getInstance(doc, new FileOutputStream(pdfPath));
					doc.open();
					// 解决中文问题
					BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
					Font FontChinese = new Font(bfChinese, 12, Font.NORMAL);
					Paragraph t = new Paragraph(htmlCode, FontChinese);
					doc.add(t);
					doc.close();
					System.out.println("文档创建成功");
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			// HTML代码来自于HTML文件
			public void generatePDF_2(File outputPDFFile, String inputHTMLFileName) throws Exception {
				FileOutputStream fos = new FileOutputStream(outputPDFFile);
				PD4ML pd4ml = new PD4ML();
				pd4ml.setPageInsets(new Insets(20, 10, 10, 10));
				pd4ml.setHtmlWidth(950);
				pd4ml.setPageSize(pd4ml.changePageOrientation(PD4Constants.A4));
				pd4ml.useTTF("java:fonts", true);
				pd4ml.setDefaultTTFs("KaiTi_GB2312", "KaiTi_GB2312", "KaiTi_GB2312");
				pd4ml.enableDebugInfo();
				pd4ml.render("file:" + inputHTMLFileName, fos);
			}
			
			// 手动构造HTML代码
			public void generatePDF_1(File outputPDFFile, StringReader strReader) throws Exception {
				FileOutputStream fos = new FileOutputStream(outputPDFFile);
				PD4ML pd4ml = new PD4ML();
				pd4ml.setPageInsets(new Insets(20, 10, 10, 10));
				pd4ml.setHtmlWidth(950);
				pd4ml.setPageSize(pd4ml.changePageOrientation(PD4Constants.A4));
				pd4ml.useTTF("java:fonts", true);
				pd4ml.setDefaultTTFs("KaiTi_GB2312", "KaiTi_GB2312", "KaiTi_GB2312");
				pd4ml.enableDebugInfo();
				pd4ml.render(strReader, fos);
			}
			
	public boolean checkByUserIdAndRoleName (String roleName, String userId) {
		String sql = "select * from t_n_role r, t_n_role_user u where  u.role_id = r.id and (r.name = ? or r.flag= 'super_admin')  and u.USER_ID = ?";
		List<Map<String, Object>> list = db.queryForList(sql, new Object[] {roleName, userId});
		if(list.size() >0) {
			return true;
		}else {
			return false;
		}
	}
			
	/**
	 * 查看该用户是否拥有某个资源的权限
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/viewByRoleNameAndUserId")
	public @ResponseBody SmartResponse<String> viewByRoleNameAndUserId(@RequestParam Map<String, Object> map, HttpServletRequest request) throws Exception {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		List<Map<String , Object>> list = new ArrayList<>();
		String RoleName = map.get("RoleName")+"";
		System.out.println(RoleName);
		UserInfo userInfo = (UserInfo) request.getSession().getAttribute(IActionConstant.SESSION_USER_KEY);
		if(StringUtils.isNotEmpty(userInfo.getId()) && StringUtils.isNotEmpty(RoleName)) {
			String sql = "select * from t_n_role r, t_n_role_user u where  u.role_id = r.id and (r.name = ? or r.flag= 'super_admin')  and u.USER_ID = ?";
			list = db.queryForList(sql, new Object[] {RoleName, userInfo.getId()});
			if(list.size() >0) {
				smartResp.setResult(OP_SUCCESS);
	    		smartResp.setMsg("true");
			}else {
				smartResp.setResult(OP_NOT_DATA_SUCCESS);
	    		smartResp.setMsg("false");
			}
		}
		return smartResp;
	}
	
	/**
	 * 根据用户Id和文档id获取当前用户所拥有的文档操作权限
	 */
	@RequestMapping("/viewByDocIdAndUserId")
	public @ResponseBody SmartResponse<String> viewByDocIdAndUserId(@RequestParam Map<String, Object> map, HttpServletRequest request) throws Exception {
		SmartResponse<String> smartResp = new SmartResponse<String>();
		Map<String, Object> params = new HashMap<>();
		List<Map<String , Object>> list = new ArrayList<>();
		String docId = map.get("uuid")+"";
		UserInfo userInfo = (UserInfo) request.getSession().getAttribute(IActionConstant.SESSION_USER_KEY);
		if(StringUtils.isNotEmpty(userInfo.getId()) && StringUtils.isNotEmpty(docId)) {
			String sql = "select * from t_n_role_docresource r, t_n_doc d, t_n_role_user ru where d.code = ? and d.id = r.resource_id and ru.ROLE_ID = r.role_id  and ru.USER_ID = ?";
			list = db.queryForList(sql, new Object[] {docId, userInfo.getId()});
			if(list.size() >0) {
				for(int i=0; i<list.size();i++) {
					String opAuth = list.get(i).get("op_auths")+"";
					String opAuths[] = opAuth.split(",");
					for(String ops : opAuths) {
						if(!params.containsKey(ops)) {
							params.put(ops, ops);
						}
					}
				}
				smartResp.setMap(params);
				smartResp.setResult(OP_SUCCESS);
	    		smartResp.setMsg("true");
			}else {
				smartResp.setMap(null);
				smartResp.setResult(OP_NOT_DATA_SUCCESS);
	    		smartResp.setMsg("false");
			}
		}
		return smartResp;
	}
	
	
	 
			
			
			

}

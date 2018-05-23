package cn.com.smart.web.listen;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.smart.init.config.InitSysConfig;
import cn.com.smart.service.SmartContextService;
import cn.com.smart.web.bean.entity.TPfproperties;
import cn.com.smart.web.service.PropertiesService;
import cn.com.smart.web.service.SubPropertiesService;
/**
 * web启动的时候执行git的知识库clone
 * @author zhanglb
 *
 */
public class GitCloneContextListener implements ServletContextListener {

	
	public String remotePath =InitSysConfig.getInstance().getValue("remotePath");//远程库路径
    public String gitName = InitSysConfig.getInstance().getValue("gitName");//远程库路径
	public String gitPwd = InitSysConfig.getInstance().getValue("gitPwd");//远程库路径
    
    public GitCloneContextListener() {
    	
    }
	
	@Override
	@Test
	public void contextInitialized(ServletContextEvent sce) {
//		propertiesService  = SmartContextService.find(PropertiesService.class);
//    	subPropertiesService =SmartContextService.find(SubPropertiesService.class); 
//    	
//    	TPfproperties toPfproperties = propertiesService.getByCode("gitdocinfo");
//    	String formDataId = toPfproperties.getForm_data_id();
//    	remotePath = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "remotePath").getKeyvalue();//远程库路径
//    	gitName = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "gitName").getKeyvalue();//远程库路径
//    	gitPwd = subPropertiesService.getByFormDataIdAndKeyname(formDataId, "gitPwd").getKeyvalue();//远程库路径
		// TODO Auto-generated method stub
		//这里可以放你要执行的代码或方法	
//		String webAppRootKey = sce.getServletContext().getRealPath("/"); 
//		System.out.println("------ -----------"+webAppRootKey+"------------------------- ");
//		System.setProperty("ticket.root" , webAppRootKey); 
//		String path =System.getProperty("ticket.root")+"web_upload/markdown/"; 
//		System.out.println("========================="+path);
		String path =sce.getServletContext().getRealPath("/")+"web_upload/markdown/" ; 
		System.out.println("========================="+path);
		try {
			Clone(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
	
	
	public void Clone(String localPath ) throws IOException, GitAPIException {
        //设置远程服务器上的用户名和密码
        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider =new
                UsernamePasswordCredentialsProvider(gitName,gitPwd);
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
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}


}

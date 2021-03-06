package cn.com.smart.web.controller.impl;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.com.smart.init.config.InitSysConfig;
import cn.com.smart.web.bean.entity.TPfproperties;
import cn.com.smart.web.service.PropertiesService;
import cn.com.smart.web.service.SubPropertiesService;

import java.io.File;
import java.io.IOException;

/**
 * JGit API测试
 */
public class GitController {

    public String remotePath =InitSysConfig.getInstance().getValue("remotePath");//远程库路径
    public String gitName = InitSysConfig.getInstance().getValue("gitName");//远程库路径
	public String gitPwd = InitSysConfig.getInstance().getValue("gitPwd");//远程库路径
//    public String localPath = "D:\\project\\";//下载已有仓库到本地路径
    public String localPath = "D:\\web_upload\\upload\\markdown\\";
    public String initPath = "D:\\test\\";//本地路径新建


    /**
     * 克隆远程库
     * @throws IOException
     * @throws GitAPIException
     */
    @Test
    public void testClone() throws IOException, GitAPIException {
        //设置远程服务器上的用户名和密码
        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider =new
                UsernamePasswordCredentialsProvider(gitName,gitPwd);

        //克隆代码库命令
        CloneCommand cloneCommand = Git.cloneRepository();

       Git git= cloneCommand.setURI(remotePath) //设置远程URI
                .setBranch("master") //设置clone下来的分支
                .setDirectory(new File(localPath)) //设置下载存放路径
                .setCredentialsProvider(usernamePasswordCredentialsProvider) //设置权限验证
                .call();

        System.out.print(git.tag());
    }

    /**
    * 本地新建仓库
    */
    @Test
    public void testCreate() throws IOException {
        //本地新建仓库地址
        Repository newRepo = FileRepositoryBuilder.create(new File(initPath + "/.git"));
        newRepo.create();
    }

    /**
    * 本地仓库新增文件
    */
    @Test
    public void testAdd() throws IOException, GitAPIException {
        File myfile = new File(localPath + "/myfile.txt");
        myfile.createNewFile();
        //git仓库地址
        Git git = new Git(new FileRepository(localPath+"/.git"));

        //添加文件
        git.add().addFilepattern("myfile").call();
        git.commit().setMessage("test jGit").call();
        
    }

    /**
    * 本地提交代码
    */
    @Test
    public void testCommit() throws IOException, GitAPIException,
            JGitInternalException {
        //git仓库地址
        Git git = new Git(new FileRepository(localPath+"/.git"));
        //提交代码
        git.commit().setMessage("test jGit").call();
    }


    /**
    * 拉取远程仓库内容到本地
    */
    @Test
    public void testPull() throws IOException, GitAPIException {

        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider =new
                UsernamePasswordCredentialsProvider(gitName,gitPwd);
        //git仓库地址
        Git git = new Git(new FileRepository(localPath+"/.git"));
        git.pull().setRemoteBranchName("master").
                setCredentialsProvider(usernamePasswordCredentialsProvider).call();
    }

    /**
    * push本地代码到远程仓库地址
    */
    @Test
    public void testPush() throws IOException, JGitInternalException,
            GitAPIException {

        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider =new
                UsernamePasswordCredentialsProvider(gitName,gitPwd);
        //git仓库地址
        
        Git git = new Git(new FileRepository(localPath+"/.git"));   
        System.out.println(git.status().call().isClean());
        git.add().addFilepattern(".").call();
        git.add().setUpdate(true).addFilepattern(".").call();
        git.commit().setMessage("test jGit").call();
        git.push().setRemote("origin").setCredentialsProvider(usernamePasswordCredentialsProvider).call();
        git.close();
    }
}
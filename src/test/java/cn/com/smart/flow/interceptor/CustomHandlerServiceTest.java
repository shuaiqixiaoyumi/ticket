package cn.com.smart.flow.interceptor;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.com.smart.flow.helper.SpringContextHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml", "classpath:applicationContext-snaker.xml"})
public class CustomHandlerServiceTest {

    @Autowired
    private ApplicationContext applicationContext;
    
	@Test
	public void test() {
		CustomHandlerService customHandlerService = (CustomHandlerService)applicationContext.getBean(CustomHandlerService.class);
		
		customHandlerService.execScript();
	}

}

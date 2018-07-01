package org.apdplat.realtimelog.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Random;

/**
 * Created by ysc on 28/08/2017.
 */
@RestController
@RequestMapping("test/")
public class TestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @RequestMapping(value = "hello",method = RequestMethod.GET )
    public String hello(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("开始调用hello方法");
        int len = new Random().nextInt(1000);
        for(int i=0; i<len; i++){
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                LOGGER.error("线程等待异常", e);
            }
            LOGGER.debug("开始处理，步骤: {}", i);
            if(i%4==0){
                LOGGER.debug("处理异常: 步骤：{}", i);
            }
            if(i%4==1){
                LOGGER.info("处理错误: 步骤：{}", i);
            }
            if(i%4==2){
                LOGGER.warn("处理错误: 步骤：{}", i);
            }
            if(i%4==3){
                LOGGER.error("处理错误: 步骤：{}", i);
            }
        }
        LOGGER.info("hello方法调用结束");
        return "";
    }
}

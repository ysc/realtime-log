package org.apdplat.realtimelog.server;

import org.apache.commons.lang.StringUtils;
import org.apdplat.realtimelog.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ysc on 01/07/2018.
 */
@ServerEndpoint(value="/log/{projectName}/{serviceName}/{level}", configurator = HttpSessionConfigurator.class)
public class LogWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogWebSocketHandler.class);


    static class ProcessInfo {
        private Process process;
        private InputStream inputStream;
        private String projectName;
        private String serviceName;
        private long start;
    }

    private final Map<String, ProcessInfo> processInfosBySession = new HashMap<>();

    @OnOpen
    public void onOpen(@PathParam(value="projectName") String projectName,
                       @PathParam(value="serviceName") String serviceName,
                       @PathParam(value="level") String level,
                       Session session, EndpointConfig config) {
        try {
            HttpSession httpSession= (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
            String wsSessionId = session.getId();
            ProcessInfo processInfo = new ProcessInfo();
            processInfosBySession.put(wsSessionId, processInfo);
            /*
            //验证用户是否登录
            if(StringUtils.isBlank(getSessionProperty(httpSession, "userAccount"))){
                return;
            }
            */
            processInfo.projectName = projectName;
            processInfo.serviceName = serviceName;
            processInfo.start = System.currentTimeMillis();

            LOGGER.info("开始获取实时日志, projectName: {}, serviceName: {}, level: {}", projectName, serviceName, level);

            if(StringUtils.isBlank(serviceName)){
                session.getBasicRemote().sendText("没有指定要查看日志的服务名" + "<br>");
                return;
            }
            if(StringUtils.isBlank(projectName)){
                session.getBasicRemote().sendText("没有指定要查看日志的服务所属的项目名称" + "<br>");
                return;
            }

            String commandPrefix = "";
            try {
                /*
                //验证项目是否存在
                ProjectRepository projectRepository = SpringContextUtils.getBean("projectRepository");
                Project project = projectRepository.findByName(projectName);
                if(project == null){
                    String info = "名称为"+projectName+"的项目不存在";
                    LOGGER.error(info);
                    session.getBasicRemote().sendText(info + "<br>");
                    return;
                }
                //验证服务是否存在
                //如果服务存在, 则能获取到该服务部署在那一台服务器上
                //DEV环境, 服务只会部署一份
                String deployServerIp = getDeployServerIp(projectName, serviceName);
                if(StringUtils.isBlank(deployServerIp)){
                    String info = "名称为"+serviceName+"的服务不存在";
                    LOGGER.error(info);
                    session.getBasicRemote().sendText(info + "<br>");
                    return;
                }
                //获取部署服务使用的用户名
                String deployServerUser = project.getDeployServerUser();
                //使用SSH远程获取服务的日志
                commandPrefix = "ssh " + deployServerUser + "@" + deployServerIp + " ";
                */
            }catch (Exception e){
                String info = "参数验证失败";
                LOGGER.error(info, e);
                session.getBasicRemote().sendText(info + "<br>");
                return;
            }
            //所有部署服务的服务器会使用规范的日志路径，格式为：/home/ubuntu/paas/logs/项目名称/服务名称.log
            //String command = commandPrefix+" tail -f /home/ubuntu/paas/logs/"+projectName+"/"+serviceName+".log";
            String command = commandPrefix+" tail -f "+projectName+"/"+serviceName+".log";

            processInfo.process = Runtime.getRuntime().exec(command);

            processInfo.inputStream = processInfo.process.getInputStream();

            TailLogThread thread = new TailLogThread(processInfo.inputStream, session, level);
            thread.start();
        } catch (Exception e) {
            LOGGER.error("获取实时日志异常", e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        String wsSessionId = session.getId();
        if(!processInfosBySession.containsKey(wsSessionId)) {
            LOGGER.error("can not close invalid session id");
            return;
        }

        ProcessInfo processInfo = processInfosBySession.remove(wsSessionId);
        try {
            LOGGER.info("停止获取实时日志, 耗时: {}, projectName: {}, serviceName: {}",
                    TimeUtils.getTimeDes(System.currentTimeMillis()-processInfo.start), processInfo.projectName, processInfo.serviceName);
            if(processInfo.inputStream != null) {
                processInfo.inputStream.close();
            }
            if(processInfo.process != null) {
                processInfo.process.destroy();
            }
        } catch (Exception e) {
            LOGGER.error("关闭实时日志流异常", e);
        }
    }

    @OnError
    public void onError(Session session, Throwable t) {
        LOGGER.error("获取实时日志异常", t);
    }


    private static String getSessionProperty(HttpSession session, String propertyName){
        try {
            return session.getAttribute(propertyName) == null ? null : session.getAttribute(propertyName).toString();
        }catch (Exception e){
            LOGGER.error("获取会话属性异常", e);
        }
        return null;
    }

}

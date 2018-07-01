### 微服务日志之实时日志

   在微服务架构中，一般会有几百甚至几千个服务，这些服务可能会被自动部署到集群中的任何一台机器上，因此，开发人员在开发的时候，要想实时查看日志输出就很不方便了，首先需要查询出服务被部署到哪一台机器上了，其次要向管理员申请目标机器的访问权限，接着要用SSH登录到目标服务器上，使用tail -f来查看实时日志，而tail -f的功能很有限，使用起来也很不方便。这个开源项目就是为了解决微服务架构下日志的实时查看问题，使开发人员无需服务器权限就能获得强大灵活方便的查看实时日志的能力。

一、编译程序:

    mvn package


二、部署到Tomcat:

    cp target/realtime-log-0.0.1-SNAPSHOT.war ~/Downloads/apache-tomcat-8.5.32/webapps
     
三、启动Tomcat:

    cd ~/Downloads/apache-tomcat-8.5.32
    bin/catalina.sh start
    
四、调用测试接口生成日志：

    http://localhost:8080/realtime-log-0.0.1-SNAPSHOT/test/hello

五、查看实时日志：

    http://localhost:8080/realtime-log-0.0.1-SNAPSHOT/realtime-log.jsp?projectName=logs&serviceName=logback&level=debug
    
实际运行效果如下：

![src/main/resources/DEBUG.png](src/main/resources/DEBUG.png)
![src/main/resources/INFO.png](src/main/resources/INFO.png)
![src/main/resources/WARN.png](src/main/resources/WARN.png)
![src/main/resources/ERROR.png](src/main/resources/ERROR.png)
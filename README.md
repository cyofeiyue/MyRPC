# MyRPC
使用Akka实现Master-Worker通讯



###1.Akka配置信息
```
//Master
akka.actor.provider = "akka.remote.RemoteActorRefProvider"
//Master启动的主机与端口
akka.remote.netty.tcp.hostname = "$host"
akka.remote.netty.tcp.port = "$port"

//Worker
akka.actor.provider = "akka.remote.RemoteActorRefProvider"
//Worker启动的主机与端口
akka.remote.netty.tcp.hostname = "$host"
akka.remote.netty.tcp.port = "$port"
```
###2.编译，打包，运行Master
上传到`Linux（192.168.92.150）`服务器上，执行`jar包`
`java -jar my-rpc-2.0.jar 192.168.92.150 8888`
传入的主机名和IP端口：`Master启动的主机和端口`

###3.编译，打包，运行Worker
在本地机器（192.168.92.3）执行**jar包**
`java -jar my-rpc-2.0.jar 192.168.92.3 10000 192.168.92.150 8888`

传入的第一组：`Worker启动的主机与端口`
传入的第二组： `要连接的Master主机与端口`

![Master和Worker编译打包.png](https://upload-images.jianshu.io/upload_images/9173423-4e8ba5b08f90fd82.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###4.jps命令查看启动的java进程

![本地启动Worker.png](https://upload-images.jianshu.io/upload_images/9173423-c336953d339394c7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![服务端启动Master.png](https://upload-images.jianshu.io/upload_images/9173423-42e18a556d728216.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


本地机器（192.168.92.3）启动的java进程（Worker）
```
C:\Program Files\Java\jdk1.7.0_80\bin>jps -l
59572 my-rpc-2.0.jar
12480 sun.tools.jps.Jps
```
服务端机器（192.168.92.150）启动的java进程（Master）
```
[hadoop@hadoop ~]$ jps -m
47666 jar 192.168.92.150 8888
47823 Jps -m
```



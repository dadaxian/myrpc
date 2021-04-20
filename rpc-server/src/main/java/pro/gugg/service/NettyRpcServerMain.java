package pro.gugg.service;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pro.gugg.Hello;
import pro.gugg.HelloService;
import pro.gugg.annotations.RpcScan;
import pro.gugg.common.entity.RpcServiceProperties;
import pro.gugg.rpcserver.netty.NettyRpcServer;
import pro.gugg.serviceImpl.HelloServiceImpl2;
/**
 * Server: Automatic registration service via @RpcService annotation
 */
@RpcScan(basePackage = {"pro.gugg"})
public class NettyRpcServerMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext=new AnnotationConfigApplicationContext(NettyRpcServerMain.class);
        NettyRpcServer nettyRpcServer=(NettyRpcServer) applicationContext.getBean("nettyRpcServer");
        HelloService helloService2=new HelloServiceImpl2();
        RpcServiceProperties rpcServiceProperties=RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        nettyRpcServer.registerService(helloService2,rpcServiceProperties);
        nettyRpcServer.start();
    }
}

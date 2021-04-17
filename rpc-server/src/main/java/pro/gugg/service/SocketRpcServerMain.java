package pro.gugg.service;

import pro.gugg.HelloService;
import pro.gugg.common.entity.RpcServiceProperties;
import pro.gugg.rpcserver.SocketRpcServer;
import pro.gugg.serviceImpl.HelloServiceImpl;

public class SocketRpcServerMain {
    public static void main(String[] args) {
        HelloService helloService=new HelloServiceImpl();
        SocketRpcServer socketRpcServer=new SocketRpcServer();
        RpcServiceProperties rpcServiceProperties=RpcServiceProperties.builder()
                .group("groupSocket").version("versionSocket").build();
        socketRpcServer.registerService(helloService,rpcServiceProperties);
        socketRpcServer.start();
    }
}

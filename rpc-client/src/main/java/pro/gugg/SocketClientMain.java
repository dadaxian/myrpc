package pro.gugg;

import pro.gugg.common.entity.RpcServiceProperties;
import pro.gugg.rpcclient.proxy.RpcClientProxy;
import pro.gugg.rpcclient.RpcRequestTransport;
import pro.gugg.rpcclient.socket.SocketRpcClient;

public class SocketClientMain {
    public static void main(String[] args) {
        // 网络传输方式
        RpcRequestTransport rpcRequestTransport=new SocketRpcClient();
        // 目标服务定位（无网络位置）
        RpcServiceProperties rpcServiceProperties=RpcServiceProperties.builder().group("groupSocket").version("versionSocket").build();
        RpcClientProxy rpcClientProxy=new RpcClientProxy(rpcRequestTransport,rpcServiceProperties);
        HelloService helloService=rpcClientProxy.getProxy(HelloService.class);
        String hello=helloService.sayHello(new Hello("客户端","你很帅"));
        System.out.println( "client : "+ hello);
    }
}

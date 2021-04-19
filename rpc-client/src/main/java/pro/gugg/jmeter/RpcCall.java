package pro.gugg.jmeter;

import pro.gugg.Hello;
import pro.gugg.HelloService;
import pro.gugg.common.entity.RpcServiceProperties;
import pro.gugg.rpcclient.RpcRequestTransport;
import pro.gugg.rpcclient.proxy.RpcClientProxy;
import pro.gugg.rpcclient.socket.SocketRpcClient;

public class RpcCall {
    public  String testCall(){
        return  "hello";
    }
    public String SocketCall() {
        // 网络传输方式
        RpcRequestTransport rpcRequestTransport = new SocketRpcClient();
        // 目标服务定位（无网络位置）
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder().group("groupSocket").version("versionSocket").build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceProperties);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.sayHello(new Hello("客户端", "你很帅"));
//        String hello = "hello!";
        System.out.println("client : " + hello);
        return hello;
    }
}

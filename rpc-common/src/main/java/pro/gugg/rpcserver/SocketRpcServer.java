package pro.gugg.rpcserver;

import lombok.extern.slf4j.Slf4j;
import pro.gugg.hooks.CustomShutdownHook;
import pro.gugg.common.entity.RpcServiceProperties;
import pro.gugg.base.factory.SingletonFactory;
import pro.gugg.rpcserver.provider.ServiceProvider;
import pro.gugg.rpcserver.provider.ServiceProviderImpl;
import pro.gugg.base.utils.concurrent.threadpool.ThreadPoolFactoryUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static pro.gugg.rpcserver.provider.ServiceProviderImpl.PORT;

@Slf4j
public class SocketRpcServer {
    private  final ExecutorService threadPool;
    private  final ServiceProvider serviceProvider;

    public SocketRpcServer() {
        this.threadPool = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }
    public void registerService(Object service){
        serviceProvider.publishService(service);
    }
    public void registerService(Object service,RpcServiceProperties rpcServiceProperties){
        serviceProvider.publishService(service,rpcServiceProperties);
    }
    public void start(){
        try (ServerSocket server = new ServerSocket()) {
            String host = InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host, PORT));
            CustomShutdownHook.getCustomShutdownHook().clearAll();
            Socket socket;
            while ((socket = server.accept()) != null) {
                log.info("client connected [{}]", socket.getInetAddress());
                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("occur IOException:", e);
        }
    }
}

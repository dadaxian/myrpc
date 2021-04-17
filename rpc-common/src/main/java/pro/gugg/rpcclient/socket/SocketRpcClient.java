package pro.gugg.rpcclient.socket;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pro.gugg.rpcclient.RpcRequestTransport;
import pro.gugg.common.entity.RpcServiceProperties;
import pro.gugg.common.exception.RpcException;
import pro.gugg.base.extension.ExtensionLoader;
import pro.gugg.remote.ServiceDiscovery;
import pro.gugg.common.entity.dto.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@AllArgsConstructor
@Slf4j
public class SocketRpcClient implements RpcRequestTransport {
    private  final ServiceDiscovery serviceDiscovery;


    public  SocketRpcClient() {
        // 这一步没看懂
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // build rpc service name by rpcRequest
        String rpcServiceName = RpcServiceProperties.builder().serviceName(rpcRequest.getInterfaceName())
                .group(rpcRequest.getGroup()).version(rpcRequest.getVersion()).build().toRpcServiceName();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcServiceName);
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // Send data to the server through the output stream
            objectOutputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // Read RpcResponse from the input stream
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("调用服务失败:", e);
        }
    }
}

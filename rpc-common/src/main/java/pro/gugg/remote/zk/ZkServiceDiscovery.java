package pro.gugg.remote.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import pro.gugg.rpcclient.loadbalance.LoadBalance;
import pro.gugg.common.enums.RpcErrorMessageEnum;
import pro.gugg.common.exception.RpcException;
import pro.gugg.base.extension.ExtensionLoader;
import pro.gugg.remote.ServiceDiscovery;
import pro.gugg.remote.zk.util.CuratorUtils;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Service discovery based on zookeeper
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    private  final LoadBalance loadBalance;

    public ZkServiceDiscovery(){
        this.loadBalance= ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }


    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {
        CuratorFramework zkClient=CuratorUtils.getZkClient();
        List<String> serviceUrlList=CuratorUtils.getChildrenNodes(zkClient,rpcServiceName);
        if(serviceUrlList==null || serviceUrlList.size()==0){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND,rpcServiceName);
        }
        // load balance
        String targetServiceUrl=loadBalance.selectServiceAddress(serviceUrlList,rpcServiceName);
        log.info("client | service discovery : Successfully found the service address:[{}]", targetServiceUrl);
        String[]socketAddressArray=targetServiceUrl.split(":");
        String host=socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host,port);
    }
}

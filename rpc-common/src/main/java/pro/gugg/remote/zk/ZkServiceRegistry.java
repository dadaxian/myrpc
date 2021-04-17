package pro.gugg.remote.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import pro.gugg.remote.ServiceRegistry;
import pro.gugg.remote.zk.util.CuratorUtils;

import java.net.InetSocketAddress;


/**
 * release zookeeper register by cli
 */
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath= CuratorUtils.ZK_REGISTER_ROOT_PATH+"/"+rpcServiceName+inetSocketAddress.toString();
        CuratorFramework zkClient=CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient,servicePath);
    }
}

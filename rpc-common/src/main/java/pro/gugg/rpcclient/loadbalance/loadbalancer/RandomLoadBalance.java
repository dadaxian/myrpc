package pro.gugg.rpcclient.loadbalance.loadbalancer;

import pro.gugg.rpcclient.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

/**
 * 随机选择负载 节点
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, String rpcServiceName) {
        Random random=new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}

package pro.gugg.rpcclient.loadbalance;

import pro.gugg.base.extension.SPI;

import java.util.List;

@SPI
public interface LoadBalance {

    /**
     * choose one from the list of existing service address list
     * @param serviceAddresses
     * @param rpcServiceName
     * @return
     */
    String selectServiceAddress(List<String> serviceAddresses, String rpcServiceName);
}

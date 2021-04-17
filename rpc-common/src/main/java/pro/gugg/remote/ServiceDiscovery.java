package pro.gugg.remote;

import pro.gugg.base.extension.SPI;

import java.net.InetSocketAddress;

/**
 * Service discovery
 */
@SPI
public interface ServiceDiscovery {
    /**
     * look up service by rpcServiceName
     * @param rpcServiceName
     * @return service socket Address
     */
    InetSocketAddress lookupService(String rpcServiceName);
}

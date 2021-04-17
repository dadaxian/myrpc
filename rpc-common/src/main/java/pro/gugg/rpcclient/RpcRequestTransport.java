package pro.gugg.rpcclient;

import pro.gugg.base.extension.SPI;
import pro.gugg.common.entity.dto.RpcRequest;

/**
 * used to send RPC Request
 */

@SPI
public interface RpcRequestTransport {
    Object sendRpcRequest(RpcRequest rpcRequest);
}

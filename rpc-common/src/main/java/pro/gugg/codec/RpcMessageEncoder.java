package pro.gugg.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import pro.gugg.base.compress.Compress;
import pro.gugg.base.extension.ExtensionLoader;
import pro.gugg.rpcserver.constants.RpcConstants;
import pro.gugg.rpcserver.dto.RpcMessage;
import pro.gugg.rpcserver.enums.CompressTypeEnum;
import pro.gugg.rpcserver.enums.SerializationTypeEnum;
import pro.gugg.rpcserver.serialize.Serializer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * custom protocol decoder
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 * <p>
 * {@link LengthFieldBasedFrameDecoder} is a length-based decoder , used to solve TCP unpacking and sticking problems.
 * </p>
 *
 * @author wangtao
 * @createTime on 2020/10/2
 * @see <a href="https://zhuanlan.zhihu.com/p/95621344">LengthFieldBasedFrameDecoder解码器</a>
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) {
        try {
            // 1. 4B  magic code（魔法数）
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            // 2. 1B version（版本）
            out.writeByte(RpcConstants.VERSION);
            // 3. 4B full length（消息长度）
            // 先跳过这个值
            // leave a place to write the value of full length
            out.writerIndex(out.writerIndex() + 4);
            // 4. 1B messageType（消息类型）
            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType);
            // 5. 1B compress（压缩类型）
            out.writeByte(rpcMessage.getCodec());
            // 6. 1B codec（序列化类型）
            out.writeByte(CompressTypeEnum.GZIP.getCode());
            // 7. 4B  requestId（请求的Id）
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());
            // build full length
            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            // if messageType is not heartbeat message,fullLength = head length + body length
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
                    && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                // serialize the object
                // 序列化
                String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                log.info("codec name: [{}] ", codecName);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                        .getExtension(codecName);
                bodyBytes = serializer.serialize(rpcMessage.getData());
                // compress the bytes
                // 压缩
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                        .getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null) {
                out.writeBytes(bodyBytes);
            }
            int writeIndex = out.writerIndex();
            // buf置回length值的位置
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            // 写入length
            out.writeInt(fullLength);
            // index设回末尾
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("Encode request error!", e);
        }

    }
}

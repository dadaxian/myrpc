package pro.gugg.rpcserver.compress;

import pro.gugg.base.extension.SPI;

@SPI
public interface Compress {

    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);
}

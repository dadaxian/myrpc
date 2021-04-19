package pro.gugg.hooks;

import lombok.extern.slf4j.Slf4j;
import pro.gugg.remote.zk.util.CuratorUtils;
import pro.gugg.base.utils.concurrent.threadpool.ThreadPoolFactoryUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 *
 */
@Slf4j
public class CustomShutdownHook {
    private  static  final  CustomShutdownHook CUSTOM_SHUTDOWN_HOOK =new CustomShutdownHook();

    public static  CustomShutdownHook getCustomShutdownHook(){
        return  CUSTOM_SHUTDOWN_HOOK;
    }
    public void clearAll(){
        log.info("addShutDownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try{
                InetSocketAddress inetSocketAddress=new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(),9998);
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(),inetSocketAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            ThreadPoolFactoryUtils.shutDownAllThreadPool();
        }));
    }
}

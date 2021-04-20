package pro.gugg;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pro.gugg.annotations.RpcScan;

@RpcScan(basePackage = {"pro.gugg"})
public class NettyClientMain {
    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext  = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController=(HelloController) applicationContext.getBean("helloController");
        helloController.test();
    }
}

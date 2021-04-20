package pro.gugg.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import pro.gugg.Hello;
import pro.gugg.HelloService;

@Slf4j
public class HelloServiceImpl2 implements HelloService {
    static {
        System.out.println("服务端： HelloServiceImpl2 被创建！");
    }
    @Override
    public String sayHello(Hello hello) {
        log.info("HelloServiceImpl2收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getMessage();
        log.info("HelloServiceImpl2返回: {}.", result);
        return result;
    }
}

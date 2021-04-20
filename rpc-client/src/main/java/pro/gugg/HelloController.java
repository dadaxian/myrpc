package pro.gugg;

import org.springframework.stereotype.Component;
import pro.gugg.annotations.RpcReference;

@Component
public class HelloController {
    @RpcReference(version = "version2", group = "test2")
    private HelloService helloService;

    public void test() throws InterruptedException {
        String hello = this.helloService.sayHello(new Hello("111", "222"));
        //如需使用 assert 断言，需要在 VM options 添加参数：-ea
        assert "Hello description is 222".equals(hello);
        Thread.sleep(12000);
        for (int i = 0; i < 10; i++) {
            System.out.println(helloService.sayHello(new Hello("111", "222")));
        }
    }
}

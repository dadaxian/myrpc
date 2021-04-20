package pro.gugg.serviceImpl;


import pro.gugg.Hello;
import pro.gugg.HelloService;
import pro.gugg.annotations.RpcService;

@RpcService(group = "test1", version = "version1")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(Hello hello) {
        System.out.println("server : " + hello.getName()+" + "+hello.getMessage());
        return  hello.getName()+" received!";
    }
}

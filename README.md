借鉴：https://github.com/Snailclimb/guide-rpc-framework

---------------------------------

# index

├─`rpc-api`   rpc的接口   
├─`rpc-client`  rpc客户端  
├─`rpc-server`   rpc服务端  
├─`rpc-common`  核心实现  
│--└─src  
│------├─main  
│------│--├─java  
│------│--│--└─pro  
│------│--│------└─gugg  
│------│--│----------├─base  
│------│--│----------│--├─`extension`  扩展类加载  
│------│--│----------│--├─`factory`  单例工厂   
│------│--│----------│--└─utils     
│------│--│----------│------├─`concurrent`  线程池   
│------│--│----------│------│--└─`threadpool`   
│------│--│----------│------└─`file`   文件读取   
│------│--│----------├─`common`   工具实体类  
│------│--│----------├─`config`   配置（比如 zk的地址）  
│------│--│----------├─`hooks`   钩子🐕  
│------│--│----------├─`remote`   远程方法（例如服务注册/发现）  
│------│--│----------├─`rpcclient`    ⭐客户端方法  
│------│--│----------│--├─`loadbalance`   负载均衡  
│------│--│----------│--├─`proxy`   rpc接口代理   
│------│--│----------│--└─`socket`   远程的socket实现  
│------│--│----------└─`rpcserver`   ⭐服务端方法  
│------│--│--------------├─`handler`  请求处理（反射执行）  
│------│--│--------------└─`provider`   服务治理（服务的储存/注册）  
│------│--└─resources   
│------│------└─META-INF   
└─-----│----------└─`extensions`   扩展类

# 启动与配置

1. 找个zookeeper 可以远程 也可以本机
2. 要修改客户端和服务端的配置文件properties中的zk地址
3. 逐个启动server和client即可

# 服务端实现

## 主要方法

服务端的调用实现 首先服务端要注册通过 **_SocketRpcServer_** 执行 注册和 socket的监听

1. 关于注册 注册通过 **_ServiceProvider_** （单例）来实现
    * 先将 <服务的properties序列化结果，要调用的方法所属类的实例>，放入我们的map中
    * 调用Curator实现的zookeeper的客户端实现zookeeper的服务注册。

2. 关于监听 监听通过tcp/udp层面的socket来监听（while死循环），启动前执行钩子函数清除所有线程池
    * 一旦监听到，就新建一个自己创建的自定义的Runnable线程，传入socket，并在线程中传入RpcRequestHandler（单例）
    * 在实现的run（）中： 在RpcRequestHandler中，使用反射完成给定对象和方法/参数对方法的调用。

## 注意点

1. 需要注意的是 ，这里使用了单例工厂的写法，而且很多地方写的单例不安全。
2. 统一的参数没写好，比如socket本地暴露的接口定义不规范
3. 对多线程的处理，是值得借鉴的
4. 对扩展类的处理，那边看不懂

# 客户端实现

主要是通过写服务发现，到zookeeper上找服务名<组名，版本号，方法名>对应的服务地址<ip,port>, 然后通过动态代理接口类，实现在调用接口后远程调用（socket/netty）来返回数据;

1. 关于服务发现 发现是通过发现 **_ServiceRecovery_** 来实现
    * 首先根据调用的类的类名，组名 ，版本号 生成zookeeper的键值，
    * 然后获取对应的子节点（一堆socket三元组），通过一致性hash算法或者随机算法，选取一个作为目标地址
2. 关于远程调用，使用 **_RpcRequestTransport_** 接口对应的实现类（socket/netty，取决于服务端的监听方式）来发送request，并返回

## 注意点

1. 在服务发现时-> 选取服务-> 负载均衡—> 一致性hash
2. 调用服务时，通过动态代理服务接口生成代理类实现调用
3. 需要注意序列化方式！
4. 对于`一致性hash`(含虚拟节点)的实现  
```java
static class ConsistentHashSelector {
    // 使用Treemap作为哈希环的数据结构
    private final TreeMap<Long, String> virtualInvokers;
    private final int identityHashCode;
    ConsistentHashSelector(List<String> invokers, int replicaNumber, int identityHashCode) {
        this.virtualInvokers = new TreeMap<>();
        this.identityHashCode = identityHashCode;
        for (String invoker : invokers) {
            for (int i = 0; i < replicaNumber / 4; i++) {
                // 每四个一组获取摘要
               //根据md5算法为每4个结点生成一个消息摘要，摘要长为16字节128位。
                byte[] digest = md5(invoker + i);
                for (int h = 0; h < 4; h++) {
                    // 设置4个虚拟节点，对摘要进行hash计算
                   // 随后将128位分为4部分，0-31,32-63,64-95,95-128，
                   // 并生成4个32位数，存于long中，long的高32位都为0
                    long m = hash(digest, h);
                    virtualInvokers.put(m, invoker);
                }
            }
        }
    }

    static long hash(byte[] digest, int idx) {
        return ((long) (digest[3 + idx * 4] & 255) << 24 | (long) (digest[2 + idx * 4] & 255) << 16 | (long) (digest[1 + idx * 4] & 255) << 8 | (long) (digest[idx * 4] & 255)) & 4294967295L;
    }

    public String select(String rpcServiceName) {
        byte[] digest = md5(rpcServiceName);
        return selectForKey(hash(digest, 0));
    }
    public String selectForKey(long hashCode) {
        Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();
        if (entry == null) {
            entry = virtualInvokers.firstEntry();
        }
        return entry.getValue();
    }
}
```
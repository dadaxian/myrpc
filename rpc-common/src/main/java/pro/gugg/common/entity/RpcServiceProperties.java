package pro.gugg.common.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceProperties {
    /**
     * service version
     */
    private String version;
    /**
     * using when there are multiple implementation class , distinguish by group
     */
    private String group;
    /**
     * locate target service
     */
    private String serviceName;

    public String toRpcServiceName(){
        return serviceName+ group + version ;
    }

}

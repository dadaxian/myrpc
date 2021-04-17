package pro.gugg.config;

import lombok.Data;
import pro.gugg.base.utils.file.PropertiesFileUtil;
import pro.gugg.common.enums.RpcConfigEnum;

import java.util.Properties;

@Data
public class RpcConfig {
    final String zkAddress;
    public RpcConfig(){
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        if(properties!=null){
            zkAddress =properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue(),null);
        }else {
            zkAddress=null;
        }
    }
}

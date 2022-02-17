package com.hongyan.mall.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * lite file service properties
 *
 * @author chenjian
 * @date 2019-07-04 10:39
 */
@ConfigurationProperties(prefix = "lite.file-service")
@Data
@Component
public class LiteFileServiceProperties {

    public static final String HTTP_PROTOCOL ="https://";

    private String resHost;

    private Integer fdfsStoreIndex = 0;

    private String fdfsGroup = "group1";
}

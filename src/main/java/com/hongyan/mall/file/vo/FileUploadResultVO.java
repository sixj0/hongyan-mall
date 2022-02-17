package com.hongyan.mall.file.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 文件上传结果VO
 *
 * @author chenjian
 * @date 2019-07-04 09:42
 */
@Data
@Builder
public class FileUploadResultVO {

    /**
     * 访问图片的全路径
     */
    private String httpUrl;
    /**
     * 相对路径uri
     */
    private String uri;
}


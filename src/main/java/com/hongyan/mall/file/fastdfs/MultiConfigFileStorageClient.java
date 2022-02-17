package com.hongyan.mall.file.fastdfs;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.GenerateStorageClient;

import java.io.InputStream;
import java.util.Set;

/**
 * 支持自定义组和存储路径的fastdfs-client
 *
 * @author chenjian
 * @date 2019-07-04 10:01
 */
public interface MultiConfigFileStorageClient extends GenerateStorageClient {

    /**
     * 上传一般文件
     *
     * @param inputStream
     * @param group
     * @param storeIndex
     * @param fileSize
     * @param fileExtName
     * @param metaDataSet
     * @return
     */
    StorePath uploadFile(InputStream inputStream,
                         String group,
                         int storeIndex,
                         long fileSize,
                         String fileExtName,
                         Set<MetaData> metaDataSet);

    /**
     * 上传图片并且生成缩略图
     *
     * <pre>
     * 支持的图片格式包括"JPG", "JPEG", "PNG", "GIF", "BMP", "WBMP"
     * </pre>
     *
     * @param inputStream
     * @param fileSize
     * @param fileExtName
     * @param metaDataSet
     * @return
     */
    StorePath uploadImageAndCrtThumbImage(InputStream inputStream,
                                          String group,
                                          int storeIndex,
                                          long fileSize,
                                          String fileExtName,
                                          Set<MetaData> metaDataSet);

    /**
     * 删除文件
     *
     * @param filePath 文件路径(groupName/path)
     */
    void deleteFile(String filePath);
}

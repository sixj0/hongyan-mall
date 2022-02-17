package com.hongyan.mall.file.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.ErrorCodeConstants;
import com.github.tobato.fastdfs.exception.FdfsServerException;
import com.hongyan.mall.file.LiteFileServiceProperties;
import com.hongyan.mall.file.fastdfs.MultiConfigFileStorageClient;
import com.hongyan.mall.file.service.FileUploadService;
import com.hongyan.mall.file.vo.FileUploadResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件上传
 *
 * @author chenjian
 * @date 2019-07-04 09:47
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private LiteFileServiceProperties fileServiceProperties;
    @Autowired
    private MultiConfigFileStorageClient storageClient;

    @Override
    public FileUploadResultVO uploadFile(MultipartFile file) throws IOException {
        return uploadFile(file, fileServiceProperties.getFdfsGroup(), fileServiceProperties.getFdfsStoreIndex());
    }

    @Override
    public FileUploadResultVO uploadFile(MultipartFile file,
                                         String group,
                                         int storeIndex) throws IOException {
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), group, storeIndex,
                file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), null);
        return buildUploadResult(storePath);
    }

    @Override
    public FileUploadResultVO uploadFile(File file) throws Exception {
        return uploadFile(file, fileServiceProperties.getFdfsGroup(), fileServiceProperties.getFdfsStoreIndex());
    }

    @Override
    public FileUploadResultVO uploadFile(File file, String group, int storeIndex) throws Exception {
        InputStream inputStream = new FileInputStream(file);
        String fileExtName = FilenameUtils.getExtension(file.getName());

        StorePath storePath = storageClient.uploadFile(
                inputStream, group, storeIndex, inputStream.available(), fileExtName, null);
        return buildUploadResult(storePath);
    }

    @Override
    public FileUploadResultVO uploadImageAndCrtThumbImage(MultipartFile file) throws IOException {
        return uploadImageAndCrtThumbImage(file, fileServiceProperties.getFdfsGroup(), fileServiceProperties.getFdfsStoreIndex());
    }

    @Override
    public FileUploadResultVO uploadImageAndCrtThumbImage(MultipartFile file,
                                                          String group,
                                                          int storeIndex) throws IOException {
        Set<MetaData> metaDataSet = createMateData();
        String fileExtName = FilenameUtils.getExtension(file.getOriginalFilename());
        StorePath storePath = storageClient.uploadImageAndCrtThumbImage(
                file.getInputStream(), group, storeIndex, file.getSize(), fileExtName, metaDataSet);
        return buildUploadResult(storePath);
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            return;
        }
        try {
            StorePath storePath = StorePath.parseFromUrl(fileUrl);
            storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
        } catch (FdfsServerException e) {
            // 如果异常code是 ErrorCodeConstants.ERR_NO_ENOENT ，表示已删除
            if (e.getErrorCode() == ErrorCodeConstants.ERR_NO_ENOENT) {
                log.debug("fastdfs找不到节点或文件");
            } else {
                throw e;
            }
        }
    }

    private FileUploadResultVO buildUploadResult(StorePath storePath) {
        return FileUploadResultVO.builder()
                .httpUrl(getResAccessUrl(storePath))
                .uri("/" + storePath.getFullPath())
                .build();
    }

    /**
     * 封装文件完整URL地址
     *
     * @param storePath
     * @return
     */
    private String getResAccessUrl(StorePath storePath) {
        return LiteFileServiceProperties.HTTP_PROTOCOL + fileServiceProperties.getResHost()+ "/" + storePath.getFullPath();
    }

    /**
     * 创建元信息
     *
     * @return
     */
    private Set<MetaData> createMateData() {
        Set<MetaData> metaDataSet = new HashSet<>();
        metaDataSet.add(new MetaData("Author", "wdw"));
        metaDataSet.add(new MetaData("CreateDate", "2017年6月29日"));
        return metaDataSet;
    }
}

package com.hongyan.mall.file.service;

import com.hongyan.mall.file.vo.FileUploadResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件上传
 *
 * @author chenjian
 * @date 2019-07-04 09:47
 */
public interface FileUploadService {

    /**
     * 上传文件，使用配置中的group和index
     *
     * @param file 文件对象
     * @return
     * @throws IOException
     */
    FileUploadResultVO uploadFile(MultipartFile file) throws IOException;

    /**
     * 上传文件
     *
     * @param file       文件对象
     * @param group      fastdfs group
     * @param storeIndex fastdfs store index
     * @return
     * @throws IOException
     */
    FileUploadResultVO uploadFile(MultipartFile file, String group, int storeIndex) throws IOException;

    /**
     * 上传文件，使用配置中的group和index
     *
     * @param file
     * @return
     */
    FileUploadResultVO uploadFile(File file) throws Exception;

    /**
     * 上传文件
     *
     * @param file
     * @param group
     * @param storeIndex
     * @return
     */
    FileUploadResultVO uploadFile(File file, String group, int storeIndex) throws Exception;

    /**
     * 上传缩略图，使用配置中的group和index
     *
     * @param file 文件对象
     * @return
     * @throws IOException
     */
    FileUploadResultVO uploadImageAndCrtThumbImage(MultipartFile file) throws IOException;

    /**
     * 上传缩略图
     *
     * @param file       文件对象
     * @param group      fastdfs group
     * @param storeIndex fastdfs store index
     * @return
     * @throws IOException
     */
    FileUploadResultVO uploadImageAndCrtThumbImage(MultipartFile file, String group, int storeIndex) throws IOException;

    /**
     * 删除文件
     *
     * @param fileUrl 文件访问地址
     */
    void deleteFile(String fileUrl);
}

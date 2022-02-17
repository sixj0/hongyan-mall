package com.hongyan.mall.controller;

import com.hongyan.mall.common.LiteRestResponse;
import com.hongyan.mall.file.service.FileUploadService;
import com.hongyan.mall.file.util.BarCodeQrUtil;
import com.hongyan.mall.file.vo.FileUploadResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传
 *
 * @author chenjian
 * @date 2019-07-04 09:40
 */
@RestController
@RequestMapping("/mall/api/file")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/onefile/upload")
    public LiteRestResponse<FileUploadResultVO> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return LiteRestResponse.success(fileUploadService.uploadFile(file));
    }

    /**
     * 多文件上传
     *
     * @param files
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/mutifile/upload")
    public LiteRestResponse<List<FileUploadResultVO>> uploadBatchFile(@RequestParam("file") MultipartFile[] files) throws IOException {
        List<FileUploadResultVO> list = new ArrayList<>();
        for (MultipartFile file : files) {
            list.add(fileUploadService.uploadFile(file));
        }
        return LiteRestResponse.success(list);
    }

    /**
     * 上传图片和生成缩略图
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/onefile/uploadImageAndCrtThumbImage")
    public LiteRestResponse<FileUploadResultVO> uploadImageAndCrtThumbImage(@RequestParam("file") MultipartFile file) throws IOException {
        return LiteRestResponse.success(fileUploadService.uploadImageAndCrtThumbImage(file));
    }

    /**
     * 生成二维码并上传到fastDFS
     *
     * @param content
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/onefile/createAndUpload")
    public LiteRestResponse<FileUploadResultVO> createAndUploadFile(@RequestParam String content) throws Exception {
        File file = BarCodeQrUtil.writeToFile(content);
        try {
            FileUploadResultVO resultVO = fileUploadService.uploadFile(file);
            return LiteRestResponse.success(resultVO);
        } finally {
            // 删除本地图片文件
            file.delete();
        }
    }

    /**
     * 删除文件
     *
     * @param fileUri
     * @return
     */
    @PostMapping(value = "/onefile/delete")
    public LiteRestResponse<Void> uploadFile(String fileUri) {
        fileUploadService.deleteFile(fileUri);
        return LiteRestResponse.success();
    }

}

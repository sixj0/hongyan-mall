package com.hongyan.mall.file.fastdfs;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorageNode;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.fdfs.ThumbImageConfig;
import com.github.tobato.fastdfs.domain.proto.storage.StorageSetMetadataCommand;
import com.github.tobato.fastdfs.domain.proto.storage.StorageUploadFileCommand;
import com.github.tobato.fastdfs.domain.proto.storage.StorageUploadSlaveFileCommand;
import com.github.tobato.fastdfs.domain.proto.storage.enums.StorageMetadataSetType;
import com.github.tobato.fastdfs.exception.FdfsUnsupportImageTypeException;
import com.github.tobato.fastdfs.exception.FdfsUploadImageException;
import com.github.tobato.fastdfs.service.DefaultGenerateStorageClient;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 支持自定义组和存储路径的fastdfs-client
 *
 * @author chenjian
 * @date 2019-07-04 10:01
 */
@Component
public class DefaultMultiConfigFileStorageClient extends DefaultGenerateStorageClient implements MultiConfigFileStorageClient {

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    /**
     * 支持的图片类型
     */
    private static final String[] SUPPORT_IMAGE_TYPE = {"JPG", "JPEG", "PNG", "GIF", "BMP", "WBMP"};
    private static final List<String> SUPPORT_IMAGE_LIST = Arrays.asList(SUPPORT_IMAGE_TYPE);

    @Override
    public StorePath uploadFile(InputStream inputStream,
                                String group,
                                int storeIndex,
                                long fileSize,
                                String fileExtName,
                                Set<MetaData> metaDataSet) {
        Validate.notNull(inputStream, "上传文件流不能为空");
        Validate.notBlank(fileExtName, "文件扩展名不能为空");
        StorageNode client = trackerClient.getStoreStorage(group);
        client.setStoreIndex((byte) storeIndex);
        return uploadFileAndMateData(client, inputStream, fileSize, fileExtName, metaDataSet);
    }

    /**
     * 上传文件和元数据
     *
     * @param client
     * @param inputStream
     * @param fileSize
     * @param fileExtName
     * @param metaDataSet
     * @return
     */
    private StorePath uploadFileAndMateData(StorageNode client,
                                            InputStream inputStream,
                                            long fileSize,
                                            String fileExtName,
                                            Set<MetaData> metaDataSet) {
        // 上传文件
        StorageUploadFileCommand command = new StorageUploadFileCommand(
                client.getStoreIndex(), inputStream, fileExtName, fileSize, false);
        StorePath path = fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
        // 上传matadata
        if (hasMateData(metaDataSet)) {
            StorageSetMetadataCommand setMDCommand = new StorageSetMetadataCommand(
                    path.getGroup(), path.getPath(), metaDataSet, StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE);
            fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), setMDCommand);
        }
        return path;
    }

    /**
     * 检查是否有MateData
     *
     * @param metaDataSet
     * @return
     */
    private boolean hasMateData(Set<MetaData> metaDataSet) {
        return null != metaDataSet && !metaDataSet.isEmpty();
    }

    @Override
    public StorePath uploadImageAndCrtThumbImage(InputStream inputStream,
                                                 String group,
                                                 int storeIndex,
                                                 long fileSize,
                                                 String fileExtName,
                                                 Set<MetaData> metaDataSet) {
        Validate.notNull(inputStream, "上传文件流不能为空");
        Validate.notBlank(fileExtName, "文件扩展名不能为空");
        // 检查是否能处理此类图片
        if (!isSupportImage(fileExtName)) {
            throw new FdfsUnsupportImageTypeException("不支持的图片格式" + fileExtName);
        }
        StorageNode client = trackerClient.getStoreStorage(group);
        client.setStoreIndex((byte) storeIndex);
        byte[] bytes = inputStreamToByte(inputStream);

        // 上传文件和mateData
        StorePath path = uploadFileAndMateData(client, new ByteArrayInputStream(bytes), fileSize, fileExtName, metaDataSet);
        // 上传缩略图
        uploadThumbImage(client, new ByteArrayInputStream(bytes), path.getPath(), fileExtName);
        bytes = null;
        return path;
    }

    /**
     * 获取byte流
     *
     * @param inputStream
     * @return
     */
    private byte[] inputStreamToByte(InputStream inputStream) {
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            LOGGER.error("image inputStream to byte error", e);
            throw new FdfsUploadImageException("upload ThumbImage error", e.getCause());
        }
    }

    /**
     * 上传缩略图
     *
     * @param client
     * @param inputStream
     * @param masterFilename
     * @param fileExtName
     */
    private void uploadThumbImage(StorageNode client, InputStream inputStream, String masterFilename,
                                  String fileExtName) {
        ByteArrayInputStream thumbImageStream = null;
        try {
            thumbImageStream = getThumbImageStream(inputStream);
            // 获取文件大小
            long fileSize = thumbImageStream.available();
            // 获取缩略图前缀
            String prefixName = thumbImageConfig.getPrefixName();
            StorageUploadSlaveFileCommand command = new StorageUploadSlaveFileCommand(thumbImageStream, fileSize,
                    masterFilename, prefixName, fileExtName);
            fdfsConnectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);

        } catch (IOException e) {
            LOGGER.error("upload ThumbImage error", e);
            throw new FdfsUploadImageException("upload ThumbImage error", e.getCause());
        } finally {
            IOUtils.closeQuietly(thumbImageStream);
        }
    }

    /**
     * 获取缩略图
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private ByteArrayInputStream getThumbImageStream(InputStream inputStream) throws IOException {
        // 在内存当中生成缩略图
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // @formatter:off
        Thumbnails
                .of(inputStream)
                .size(thumbImageConfig.getWidth(), thumbImageConfig.getHeight())
                .toOutputStream(out);
        // @formatter:on
        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * 是否是支持的图片文件
     *
     * @param fileExtName
     * @return
     */
    private boolean isSupportImage(String fileExtName) {
        return SUPPORT_IMAGE_LIST.contains(fileExtName.toUpperCase());
    }

    @Override
    public void deleteFile(String filePath) {
        StorePath storePath = StorePath.parseFromUrl(filePath);
        super.deleteFile(storePath.getGroup(), storePath.getPath());
    }
}

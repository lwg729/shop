package com.lwg.upload.service.impl;

/**
 * 功能描述：图片上传
 *
 * @Author: lwg
 * @Date: 2021/1/14 20:46
 */

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.lwg.upload.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadServiceImpl implements UploadService {

    //定义图片上传支持的类型
    public static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg", "image/gif");
    public static final Logger LOGGER = LoggerFactory.getLogger(UploadServiceImpl.class);

    @Autowired
    private FastFileStorageClient storageClient;

    /**
     * 上传图片
     *
     * @param file
     * @return
     * @throws IOException
     */
    public String uploadImage(MultipartFile file) throws IOException {

        //得到上传时的文件名
        String originalFilename = file.getOriginalFilename();

        //检验文件的格式
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)) {
            LOGGER.info("文件格式不合法:{}", originalFilename);
            return null;
        }
        try {
            //检验文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                LOGGER.info("文件为空:{}", originalFilename);
                return null;
            }

            //保存到服务器
            //file.transferTo(new File("F:\\image" + originalFilename));
            //获得后缀
            String ext = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
            //返回url，进行回显
            //return "http://image.lshop.com/" + originalFilename;
            return "http://image.lware.com/"+storePath.getFullPath();
        }catch (IOException e){
            LOGGER.info("服务器内部错误："+originalFilename);
            e.printStackTrace();
        }

        return null;
    }
}

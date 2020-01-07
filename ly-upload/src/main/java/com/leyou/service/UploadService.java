package com.leyou.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.web.UploadController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;


@Service
public class UploadService {

    //打印日志
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    // 支持的文件类型
    private static final List<String> suffixes = Arrays.asList("image/png", "image/jpeg");

    //操作fastdfs的客户端
    @Autowired
    FastFileStorageClient storageClient;

    /**
     * 上传图片
     * @param file
     * @return
     */
    public String upload(MultipartFile file) {

        /**
         * 上传到fastdfs
         */
        try {
            // 1、图片信息校验
            // 1)校验文件类型
            String type = file.getContentType();
            if (!suffixes.contains(type)) {
                logger.info("上传失败，文件类型不匹配：{}", type);
                return null;
            }
            // 2)校验图片内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                logger.info("上传失败，文件内容不符合要求");
                return null;
            }

            // 2、将图片上传到FastDFS
            // 2.1、获取文件后缀名
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            // 2.2、上传
            StorePath storePath = this.storageClient.uploadFile(
                    file.getInputStream(), file.getSize(), extension, null);
            // 2.3、返回完整路径
            System.out.println("http://image.leyou.com/" + storePath.getFullPath());
            return "http://image.leyou.com/" + storePath.getFullPath();
        } catch (Exception e) {
            return null;
        }

        /*
        //上传到本地
        try {
            //1.图片信息校验
            //(1)校验文件类型
            String contentType = file.getContentType();
            if(!suffixes.contains(contentType)){
                logger.error("文件上传失败，文件类型不匹配",contentType);
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //(2)校验文件类型
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image == null){
                logger.error("文件上传失败，文件内容不符合要求");
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //2.保存图片
            //(1)生成保存目录
            File dir=new File("D:\\uploads");
            if(!dir.exists()){
                dir.mkdir();
            }
            //(2)保存图片
            file.transferTo(new File(dir,file.getOriginalFilename()));

            //(3)拼接图片地址
            String url="http://image.leyou.com/upload/" + file.getOriginalFilename();

            return url;
        }catch (Exception e){
            // throw new LyException(ExceptionEnum.IMAGE_UPLOAD_ERROE);
            return null;
        }*/


    }
}

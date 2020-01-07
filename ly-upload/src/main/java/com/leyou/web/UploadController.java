package com.leyou.web;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptiom.LyException;
import com.leyou.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private UploadService service;

    /**
     * 上传图片
     * @param file
     * @return
     */
    @PostMapping("image")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file){
        String url=service.upload(file);
        if(StringUtils.isBlank(url)){
            throw new LyException(ExceptionEnum.IMAGE_UPLOAD_ERROE);
        }
        return ResponseEntity.ok(url);
    }
}

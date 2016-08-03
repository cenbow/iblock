package com.iblock.service.file;

import com.iblock.common.exception.InvalidRequestException;
import com.iblock.common.utils.ImageUtils;
import org.im4java.core.IM4JavaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by baidu on 16/2/28.
 */
@Component
public class FileService {

    @Value("${file.path}")
    private String filePath;

    public String uploadImage(CommonsMultipartFile file, Integer width) throws IOException, InvalidRequestException, IM4JavaException, InterruptedException {
        String[] originName = file.getOriginalFilename().split("\\.");
        String name = UUID.randomUUID() + "."+ originName[originName.length - 1];
        String fileName = uploadFile(file, name);
        if (width != null && width > 0) {
            ImageUtils.resize(file, width, filePath + name);
        }
        return fileName;
    }

    public String uploadFile(CommonsMultipartFile file) throws IOException {
        String[] originName = file.getOriginalFilename().split("\\.");
        String name = UUID.randomUUID() + "."+ originName[originName.length - 1];
        return uploadFile(file, name);
    }

    public String uploadFile(CommonsMultipartFile file, String name) throws IOException {
        File f = new File(filePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(filePath + name);
        fos.write(file.getBytes());
        return "/data/" + name;
    }

    public File get(String name) throws IOException {
        return new File(filePath + name);
    }
}

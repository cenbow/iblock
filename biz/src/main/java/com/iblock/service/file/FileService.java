package com.iblock.service.file;

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

    public String uploadFile(CommonsMultipartFile file) throws IOException {
        File f = new File(filePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        String[] originName = file.getOriginalFilename().split("\\.");
        String name = UUID.randomUUID() + "."+ originName[originName.length - 1];
        FileOutputStream fos = new FileOutputStream(filePath + name);
        fos.write(file.getBytes());
        return "/data/" + name;
    }

    public File get(String name) throws IOException {
        return new File(filePath + name);
    }
}

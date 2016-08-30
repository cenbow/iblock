package com.iblock.web.controller;

import com.iblock.common.advice.Auth;
import com.iblock.service.file.FileService;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by baidu on 16/7/15.
 */
@Controller
@Log4j
public class FileController extends BaseController {

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/data/{name}", method = RequestMethod.GET)
    @ResponseBody
    public void getFile(@PathVariable(value = "name") String name, HttpServletRequest request, HttpServletResponse
            response) {
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            is = new FileInputStream(fileService.get(request.getPathInfo().substring(6)));
            bis = new BufferedInputStream(is);
            response.reset();
            OutputStream os = response.getOutputStream();
            byte[] data = new byte[4096];
            int size;
            while ((size = bis.read(data)) > 0) {
                os.write(data, 0, size);
            }
            os.flush();
        } catch (Exception e) {
            log.error("getFile error!", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                log.error("close is error", e);
            }


        }
    }

    @RequestMapping(value = "/file/image/new", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponse<String> newImage(@RequestParam(value = "file") CommonsMultipartFile file) {
        try {
            String name = fileService.uploadFile(file);
            return new CommonResponse<String>(name);
        } catch (Exception e) {
            log.error("new image error!", e);
        }
        return new CommonResponse<String>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/project/image/new", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponse<String> newProjectImage(@RequestParam(value = "file") CommonsMultipartFile file) {
        try {
            String name = fileService.uploadImage(file, 200);
            return new CommonResponse<String>(name);
        } catch (Exception e) {
            log.error("new image error!", e);
        }
        return new CommonResponse<String>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/work/image/new", method = RequestMethod.POST)
    @ResponseBody
    public CommonResponse<String> newWorkImage(@RequestParam(value = "file") CommonsMultipartFile file) {
        try {
            String name = fileService.uploadImage(file, 800);
            return new CommonResponse<String>(name);
        } catch (Exception e) {
            log.error("new image error!", e);
        }
        return new CommonResponse<String>(ResponseStatus.SYSTEM_ERROR);
    }
}

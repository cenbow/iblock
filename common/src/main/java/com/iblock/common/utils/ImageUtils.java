package com.iblock.common.utils;

import com.iblock.common.exception.InvalidRequestException;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by baidu on 16/8/2.
 */
public class ImageUtils {

    /**
     * 强制压缩/放大图片到固定的大小
     * @param w int 新宽度
     */
    public static void resize(CommonsMultipartFile file, int w, String originPath) throws IOException,
            InvalidRequestException, IM4JavaException, InterruptedException {
        String suffix = originPath.substring(originPath.lastIndexOf(".") + 1);
        if (!suffix.equals("png") && !suffix.equals("jpg") && !suffix.equals("jpeg")) {
            throw new InvalidRequestException("文件格式不正确");
        }
        String dest = originPath.substring(0, originPath.lastIndexOf(".")) + "-small" + originPath.substring(originPath
                .lastIndexOf("."));
        BufferedImage image = ImageIO.read(file.getInputStream());
        int h = image.getHeight();
        if (image.getWidth() > w) {
            h = w * image.getHeight() / image.getWidth();
        }
        IMOperation op = new IMOperation();
        op.addImage(originPath);
        op.addRawArgs("-resize", w + "x" + h);
        op.addImage(dest);
        ConvertCmd cmd = new ConvertCmd(false);
        cmd.run(op);
    }
}

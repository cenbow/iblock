package com.iblock.web.controller;

import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import nl.captcha.Captcha;
import nl.captcha.gimpy.BlockGimpyRenderer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.ChineseTextProducer;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.renderer.ColoredEdgesWordRenderer;
import nl.captcha.text.renderer.WordRenderer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by baidu on 16/2/10.
 */

@Controller
@Log4j
@RequestMapping("/validate")
public class ValidateController extends BaseController {

    @Value("${captcha.height}")
    private int height;

    @Value("${captcha.width}")
    private int width;

    @Value("${captcha.noise}")
    private int noise;

    @Value("${captcha.text}")
    private String text;

    @RequestMapping(value = "/check", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public CommonResponse<Boolean> check(HttpServletRequest request) {
        try {
            Captcha captcha = (Captcha) request.getSession().getAttribute(Captcha.NAME);
            String answer = request.getParameter("answer");
            return new CommonResponse<Boolean>(captcha.isCorrect(answer));
        } catch (Exception e) {
            log.error("validate.check error!", e);
        }
        return new CommonResponse<Boolean>(ResponseStatus.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public void get(HttpServletRequest request, HttpServletResponse response) {
        try {
            Captcha.Builder builder = new Captcha.Builder(width, height);
            builder.addBorder();
            if (noise == 1) {
                builder.addNoise();
            }
            WordRenderer renderer = new ColoredEdgesWordRenderer(Arrays.asList(Color.green, Color.blue),
                    Arrays.asList(new Font("Arial", Font.ITALIC, 40)));
            if (StringUtils.isBlank(text)) {
                builder.addText();
            } else {
                String[] ts = text.split(",");
                for (int i = 0; i < ts.length; i++) {
                    String[] ts1 = ts[i].split(":");
                    if ("chinese".equals(ts1[0])) {
                        builder.addText(new ChineseTextProducer(Integer.parseInt(ts1[1])), renderer);
                    } else if ("number".equals(ts1[0])) {
                        char[] numberChar = new char[]{'2', '3', '4', '5', '6', '7', '8'};
                        builder.addText(new DefaultTextProducer(Integer.parseInt(ts1[1]), numberChar), renderer);
                    } else if ("word".equals(ts1[0])) {
                        char[] numberChar = new char[]{'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D',
                                'e', 'E', 'f', 'F', 'g', 'G', 'h', 'H', 'k', 'K', 'm', 'M', 'n', 'N', 'p', 'P', 'r', 'R', 'w', 'W', 'x', 'X', 'y', 'Y'};
                        builder.addText(new DefaultTextProducer(Integer.parseInt(ts1[1]), numberChar), renderer);
                    } else {
                        builder.addText(new DefaultTextProducer(Integer.parseInt(ts1[1])), renderer);
                    }
                }
            }
            builder.gimp(new BlockGimpyRenderer(1));
            Captcha captcha =  builder .build();
            CaptchaServletUtil.writeImage(response, captcha.getImage());
            request.getSession().setAttribute(Captcha.NAME, captcha);
        } catch (Exception e) {
            log.error("validate.get error!", e);
        }
    }
}

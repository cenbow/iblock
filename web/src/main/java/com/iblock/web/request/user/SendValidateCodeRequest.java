package com.iblock.web.request.user;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by baidu on 16/2/8.
 */
@Data
public class SendValidateCodeRequest {

    @NotBlank
    private String phone;
}

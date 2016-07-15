package com.iblock.web.request.user;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by baidu on 16/7/1.
 */
@Data
public class ModifyPasswordRequest {

    @NotBlank
    private String oldpassword;
    @NotBlank
    private String newpassword;
}

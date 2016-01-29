package com.iblock.web.request.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by baidu on 16/1/27.
 */
@Data
public class UserIdRequest {

    @NotNull
    private Integer userId;
}

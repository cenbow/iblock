package com.iblock.web.request.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by baidu on 16/6/26.
 */
@Data
public class AddBrokerRequest {

    @NotNull
    private Long id;
    @NotNull
    private Long broker;
}

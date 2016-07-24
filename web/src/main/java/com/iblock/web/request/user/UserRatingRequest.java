package com.iblock.web.request.user;

import lombok.Data;

/**
 * Created by baidu on 16/7/24.
 */
@Data
public class UserRatingRequest {
    private Long id;
    private Long msgId;
    private Integer rating;
}

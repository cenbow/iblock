package com.iblock.service.info;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by baidu on 16/7/17.
 */
@Data
@AllArgsConstructor
public class KVLongInfo {

    private long id;
    private String name;

    public KVLongInfo() {}
}

package com.iblock.service.info;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by baidu on 16/6/5.
 */
@Data
@AllArgsConstructor
public class KVInfo {
    private int id;
    private String name;

    public KVInfo() {}
}

package com.iblock.service.search;

import com.iblock.common.bean.Page;

/**
 * Created by baidu on 16/7/23.
 */
public interface Search<T> {

    void init();

    void update(T entity);

//    Page<T> search(Condition condition);
}

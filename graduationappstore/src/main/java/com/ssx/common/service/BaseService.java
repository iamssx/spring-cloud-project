package com.ssx.common.service;


import com.ssx.common.domain.BaseDomain;

import java.io.Serializable;

/**
 * Created by 37 on 2017/5/5.
 */
public interface BaseService<T extends BaseDomain<ID>, ID extends Serializable> {

    void delete(T t);
    T save(T t);
    Iterable<T> save(Iterable<T> iterable);
    T find(Class key, ID id);
}

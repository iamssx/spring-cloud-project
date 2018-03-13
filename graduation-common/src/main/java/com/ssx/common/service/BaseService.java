package com.ssx.common.service;


import com.ssx.common.domain.BaseDomain;

import java.io.Serializable;

public interface BaseService<T extends BaseDomain<ID>, ID extends Serializable> {
    void delete(T t);
    void delete(Class<T> clz, ID id);
    T save(T t);
    Iterable<T> save(Iterable<T> iterable);
    T find(Class key, ID id);
}

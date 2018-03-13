package com.ssx.common.domain;

import java.io.Serializable;

/**
 * Created by 37 on 2017/5/5.
 */
public interface BaseDomain<ID extends Serializable> {
    ID getID();
}

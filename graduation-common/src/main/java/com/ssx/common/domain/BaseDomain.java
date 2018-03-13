package com.ssx.common.domain;

import java.io.Serializable;

public interface BaseDomain<ID extends Serializable> {
    ID getID();
}

package com.ssx.resource.service;

import com.ssx.common.domain.Result;
import com.ssx.common.domain.User;
import com.ssx.common.service.BaseService;
import com.ssx.resource.domain.Order;
import com.ssx.resource.domain.Resource;

public interface OrderService extends BaseService<Order, Long> {

    Result addOrder(long uid, long rid, long count);

    Result findByUser(long uid, int pageNo, int pageSize);

    Order findByUserAndResource(User user, Resource resource);
}

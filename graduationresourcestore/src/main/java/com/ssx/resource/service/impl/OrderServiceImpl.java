package com.ssx.resource.service.impl;

import com.ssx.common.domain.Result;
import com.ssx.common.domain.ResultCode;
import com.ssx.common.domain.User;
import com.ssx.common.service.ABaseService;
import com.ssx.resource.client.UserService;
import com.ssx.resource.domain.Order;
import com.ssx.resource.domain.OrderRepository;
import com.ssx.resource.domain.Resource;
import com.ssx.resource.domain.ResourceRepository;
import com.ssx.resource.service.OrderService;
import com.ssx.resource.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl extends ABaseService<Order, Long> implements OrderService {

    @Autowired
    private UserService userService;
    @Autowired
    private ResourceRepository resourceRepository;

    private OrderRepository orderRepository;
    @Value("${workerId}")
    private Long workerId;
    private IdWorker snowflakeIdWorker; //订单号生成器

    @PostConstruct
    public void init() {
        snowflakeIdWorker = new IdWorker(workerId);
    }

    @Autowired
    public OrderServiceImpl(OrderRepository repository, RedisTemplate redisTemplate) {
        super(repository, redisTemplate);
        this.orderRepository = repository;
    }

    @Override
    public Result addOrder(long uid, long rid, long count) {
        User user = userService.findByUid(uid);
        Result result = new Result();
        if (user == null) {
            result.setCode(ResultCode.USER_UN_EXISTS);
            return result;
        }
        Resource resource = resourceRepository.findOne(rid);
        if (resource == null) {
            result.setCode(ResultCode.RESOURCE_UN_EXIST);
            return result;
        }
        Order order = new Order();
        order.setUser(user);
        order.setCount(count);
        order.setResource(resource);
        order.setTime(System.currentTimeMillis());
        order.setAmount(resource.getPrice() * count);
        order.setOid(snowflakeIdWorker.nextId());
        order.setState(Order.UN_PAY);
        order = orderRepository.save(order);
        result.setEntity(order);
        return result;
    }

    @Override
    public Result findByUser(long uid, int pageNo, int pageSize) {
        User user = userService.findByUid(uid);
        Result result = new Result();
        if (user == null) {
            result.setCode(ResultCode.USER_UN_EXISTS);
            return result;
        }
        PageRequest pageRequest = new PageRequest(pageNo, pageSize);
        List<Order> orderList = orderRepository.findByUser(user, pageRequest);
        result.setEntity(orderList);
        return result;
    }

    @Override
    public Order findByUserAndResource(User user, Resource resource) {
        if (user == null || resource == null) {
            return null;
        }
        return orderRepository.findByUserAndResource(user, resource);
    }
}

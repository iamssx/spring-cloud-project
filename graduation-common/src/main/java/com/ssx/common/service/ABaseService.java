package com.ssx.common.service;

import com.ssx.common.domain.BaseDomain;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class ABaseService<T extends BaseDomain<ID>, ID extends Serializable> implements BaseService<T, ID> {

    protected final CrudRepository<T, ID> repository;
    protected final RedisTemplate<Class, T> redisTemplate;

    protected ABaseService(CrudRepository<T, ID> repository, RedisTemplate<Class, T> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public T save(T t) {
        T newOne = repository.save(t);
        HashOperations<Class, ID, T> opsForHash = redisTemplate.opsForHash();
        Class<?> aClass = newOne.getClass();
        opsForHash.delete(aClass, newOne.getID());
        return newOne;
    }

    @Override
    public void delete(T t) {
        //1. 从数据库中删除相关的实例
        repository.delete(t);
        HashOperations<Class, Object, Object> opsForHash = redisTemplate.opsForHash();
        //2. 从redis缓存中删除
        opsForHash.delete(t.getClass(), t.getID());
    }

    @Override
    public void delete(Class<T> clz, ID id) {
        repository.delete(id);
        HashOperations<Class, Object, Object> opsForHash = redisTemplate.opsForHash();
        opsForHash.delete(clz, id);
    }

    @Override
    public Iterable<T> save(Iterable<T> iterable) {
        Iterable<T> save = repository.save(iterable);
        HashOperations<Class, ID, T> opsForHash = redisTemplate.opsForHash();
        HashMap<ID, T> map = new HashMap<>();
        for (T t : save) {
            opsForHash.delete(t.getClass(), t.getID());
        }
        if (map.size() < 1) {
            return new ArrayList<T>();
        }
        return save;
    }

    @Override
    public T find(Class key, ID hashKey) {
        HashOperations<Class, ID, T> opsForHash = redisTemplate.opsForHash();
        T t = opsForHash.get(key, hashKey);
        if (t == null) {
            T one = repository.findOne(hashKey);
            if (one != null) {
                opsForHash.put(key, hashKey, t);
                t = one;
            }
        }
        return t;
    }
}

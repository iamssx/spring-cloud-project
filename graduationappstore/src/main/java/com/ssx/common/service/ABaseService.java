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
        opsForHash.put(aClass, newOne.getID(), newOne);
        return newOne;
    }

    @Override
    public void delete(T t) {
        repository.delete(t);
        HashOperations<Class, Object, Object> opsForHash = redisTemplate.opsForHash();
        opsForHash.delete(t.getClass(), t.getID());
    }

    @Override
    public Iterable<T> save(Iterable<T> iterable) {
        Iterable<T> save = repository.save(iterable);
        HashOperations<Class, ID, T> opsForHash = redisTemplate.opsForHash();
        HashMap<ID, T> map = new HashMap<>();
        for (T t : save) {
            map.put(t.getID(), t);
        }
        if (map.size() < 1) {
            return new ArrayList<T>();
        }
        opsForHash.putAll(map.get(0).getClass(), map);
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

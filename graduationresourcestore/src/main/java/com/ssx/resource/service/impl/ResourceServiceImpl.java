package com.ssx.resource.service.impl;

import com.google.common.io.Files;
import com.ssx.common.domain.Result;
import com.ssx.common.domain.ResultCode;
import com.ssx.common.domain.User;
import com.ssx.common.service.ABaseService;
import com.ssx.resource.client.UserService;
import com.ssx.resource.domain.Order;
import com.ssx.resource.domain.Resource;
import com.ssx.resource.domain.ResourceRepository;
import com.ssx.resource.service.OrderService;
import com.ssx.resource.service.ResourceService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ResourceServiceImpl extends ABaseService<Resource, Long> implements ResourceService {

    private ResourceRepository repository;
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Value("${fileServerAddress}")
    private String fileServerAddress;
    @Value("${token}")
    private String token;

    @Autowired
    public ResourceServiceImpl(ResourceRepository repository, RedisTemplate redisTemplate) {
        super(repository, redisTemplate);
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Result<Resource> findByName(String name) {
        Result result = new Result();
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Resource resource = (Resource) valueOperations.get(Resource.class.toString() + "name:" + name);
        if (resource == null) {
            resource = repository.findByName(name);
            if (resource == null) {
                result.setCode(ResultCode.RESOURCE_UNEXIST);
                return result;
            }
            valueOperations.set(Resource.class.toString() + "name:" + name, resource);
        }
        result.setEntity(resource);
        return result;
    }

    @Override
    public Result<List<Resource>> findByPriceBetween(Long low, Long high, Integer pageNo, Integer pageSize) {
        if (low < 0 || high < 0) {
            return Result.ERROR;
        }
        if (high < low) {
            return Result.ERROR;
        }
        PageRequest pageRequest = new PageRequest(pageNo, pageSize);
        List<Resource> resourceList = repository.findByPriceBetween(low, high, pageRequest);
        Result<List<Resource>> result = new Result<>();
        result.setEntity(resourceList);
        return result;
    }

    @Override
    public Result<List<Resource>> findByPriceLessThan(Long price, Integer pageNo, Integer pageSize) {
        if (price < 0) {
            return Result.ERROR;
        }
        PageRequest pageRequest = new PageRequest(pageNo, pageSize);
        List<Resource> resourceList = repository.findByPriceLessThan(price, pageRequest);
        Result<List<Resource>> result = new Result<>();
        result.setEntity(resourceList);
        return result;
    }

    @Override
    public Result findByPriceGreatThan(Long price, Integer pageNo, Integer pageSize) {
        if (price < 0) {
            return Result.ERROR;
        }
        PageRequest pageRequest = new PageRequest(pageNo, pageSize);
        List<Resource> resourceList = repository.findByPriceGreaterThan(price, pageRequest);
        Result<List<Resource>> result = new Result<>();
        result.setEntity(resourceList);
        return result;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public Result upload(String name, String description, String type, Long price, MultipartFile file) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(description) || StringUtils.isEmpty(type) || price < 0 || file == null) {
            return Result.ERROR;
        }
        User user = userService.getUser();
        if (user == null) {
            return Result.ERROR;
        }
        Result<Resource> result = findByName(name);
        if (result.getEntity() != null) {
            Result result1 = new Result();
            result1.setCode(ResultCode.RESOURCE_NAME_REPEAT);
            return result1;
        }
        result = new Result<>();
        String url = fileServerAddress;
        String originalFilename = file.getOriginalFilename();
        String fileName = name + originalFilename.substring(originalFilename.lastIndexOf("."));
        File targetFile = new File(url, fileName);
        try {
            Files.createParentDirs(targetFile);
            file.transferTo(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.ERROR;
        }
        Resource resource = new Resource();
        UUID uuid = UUID.randomUUID();
        resource.setName(name);
        resource.setDescription(description);
        resource.setPrice(price);
        resource.setUrl(targetFile.getAbsolutePath());
        resource.setUuid(uuid.toString());
        resource.setUser(user);
        Resource save = save(resource);
        result.setEntity(save);
        return result;
    }

    @Override
    public Result update(String token, Long rid, String name, String description, String type, Long price, MultipartFile file) {
        User user = userService.getUser();
        Resource resource = find(Resource.class, rid);
        if (!this.token.equals(token)) {
            if (!resource.getUser().equals(user)) {
                return Result.ERROR;
            }
        }
        if (name != null) {
            Result<Resource> result = findByName(name);
            if (result.getEntity() != null) {
                Result result1 = new Result();
                result1.setCode(ResultCode.RESOURCE_NAME_REPEAT);
                return result1;
            }
        }
        if (description != null) {
            resource.setDescription(description);
        }
        if (type != null) {
            resource.setType(type);
        }
        if (price != null) {
            resource.setPrice(price);
        }
        if (file != null) {
            String oldUrl = resource.getUrl();
            String url = fileServerAddress;
            String originalFilename = file.getOriginalFilename();
            String fileName = name + originalFilename.substring(originalFilename.lastIndexOf("."));
            File targetFile = new File(url, fileName);
            try {
                Files.createParentDirs(targetFile);
                file.transferTo(targetFile);
            } catch (IOException e) {
                e.printStackTrace();
                return Result.ERROR;
            }
            FileUtils.deleteQuietly(new File(oldUrl));
        }
        return new Result();
    }

    @Override
    public void download(String rUUID, HttpServletResponse response) {
        User user = userService.getUser();
        Resource resource = repository.findByUuid(rUUID);
        if (resource == null) {
            return;
        }
        Order order = orderService.findByUserAndResource(user, resource);
        if (order == null || order.getState().equals(Order.UN_PAY)) {
            return;
        }
        try {
            String url = resource.getUrl();
            byte[] data = IOUtils.toByteArray(new FileInputStream(url));
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + resource.getName() + url.substring(url.lastIndexOf(".")) + "\"");
            response.addHeader("Content-Length", "" + data.length);
            response.setContentType("application/octet-stream;charset=UTF-8");

            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


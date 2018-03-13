package com.ssx.resource.service;

import com.ssx.common.domain.Result;
import com.ssx.common.service.BaseService;
import com.ssx.resource.domain.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface ResourceService extends BaseService<Resource, Long> {

    Result findByName(String name);

    Result findByPriceBetween(Long low, Long high, Integer pageNo, Integer pageSize);

    Result findByPriceLessThan(Long price, Integer pageNo, Integer pageSize);

    Result findByPriceGreatThan(Long price, Integer pageNo, Integer pageSize);

    Result upload(String name, String description, String type, Long price, MultipartFile multipartFile);

    Result update(String token, Long rid, String name, String description, String type, Long price, MultipartFile file);

    void download(String rUUID, HttpServletResponse response);
}


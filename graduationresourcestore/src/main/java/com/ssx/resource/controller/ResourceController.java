package com.ssx.resource.controller;

import com.ssx.common.domain.Result;
import com.ssx.common.domain.User;
import com.ssx.resource.client.UserService;
import com.ssx.resource.domain.Resource;
import com.ssx.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserService userService;
    @Value("${token}")
    private String token;

    @PostMapping("/upload")
    public Result upload(String name, String description, String type, Long price, MultipartFile file) {
        return resourceService.upload(name, description, type, price, file);
    }

    @PostMapping("/update")
    public Result update(String token, Long rid, String name, String description, String type, Long price, MultipartFile file) {
        return resourceService.update(token, rid, name, description, type, price, file);
    }

    @GetMapping("/rid/{rid}")
    public Resource resource(@PathVariable Long rid) {
        return resourceService.find(Resource.class, rid);
    }

    @GetMapping("/price/between")
    public Result priceBetween(Long low, Long high, Integer pageNo, Integer pageSize) {
        return resourceService.findByPriceBetween(low, high, pageNo, pageSize);
    }

    @GetMapping("/price/less")
    public Result priceLessThan(Long price, Integer pageNo, Integer pageSize) {
        return resourceService.findByPriceLessThan(price, pageNo, pageSize);
    }

    @GetMapping("/price/greater")
    public Result priceGreaterThan(Long price, Integer pageNo, Integer pageSize) {
        return resourceService.findByPriceGreatThan(price, pageNo, pageSize);
    }

    @GetMapping("/download/{rUUID}")
    public void download(@PathVariable String rUUID, HttpServletResponse response) {
        resourceService.download(rUUID, response);
    }

    @DeleteMapping("rid/{rid}")
    public void delete(String token, @PathVariable long rid) {
        if (this.token.equals(token)) {
            resourceService.delete(Resource.class, rid);
        }
        Boolean test = userService.isLogin("test");

        User user = userService.getUser();
        if (user == null) {
            return;
        }
        Resource resource = resourceService.find(Resource.class, rid);
        if (user.equals(resource.getUser()) ) {
            resourceService.delete(Resource.class, rid);
        }
    }

    @GetMapping("/test")
    public String te(HttpSession session) {
        Boolean test = userService.isLogin("test");
        String id = session.getId();
        System.out.println(id);
        return id;
    }
}

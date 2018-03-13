package com.ssx.resource.service.impl;

import com.ssx.common.domain.Result;
import com.ssx.common.domain.ResultCode;
import com.ssx.common.domain.User;
import com.ssx.resource.client.UserService;
import com.ssx.resource.domain.Comment;
import com.ssx.resource.domain.CommentRepository;
import com.ssx.resource.domain.Resource;
import com.ssx.resource.domain.ResourceRepository;
import com.ssx.resource.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public Result findByRid(long rid, int pageNo, int pageSize) {
        if (pageNo < 0 || pageSize < 0) {
            return Result.ERROR;
        }
        Result result = new Result();
        if (rid <= 0) {
            return Result.ERROR;
        }
        Resource resource = resourceRepository.findOne(rid);
        if (resource == null) {
            result.setCode(ResultCode.RESOURCE_UN_EXIST);
            return result;
        }
        PageRequest pageRequest = new PageRequest(pageNo, pageSize);
        List<Comment> commentList = repository.findByResource(resource, pageRequest);
        result.setEntity(commentList);
        return result;
    }

    @Override
    public Result addComment(String comment, Long rid) {
        User user = userService.getUser();
        if (user == null) {
            return Result.ERROR;
        }
        Result result = new Result();
        if (comment.length() > 100) {
            result.setCode(ResultCode.COMMENT_TOO_LONG);
            return result;
        }
        Resource resource = resourceRepository.findOne(rid);
        if (resource == null) {
            result.setCode(ResultCode.RESOURCE_UN_EXIST);
            return result;
        }

        Comment newComment = new Comment();
        newComment.setContent(comment);
        newComment.setResource(resource);
        newComment.setUser(user);
        repository.save(newComment);
        result.setEntity(comment);
        return result;
    }
}

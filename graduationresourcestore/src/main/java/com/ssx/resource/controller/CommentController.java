package com.ssx.resource.controller;

import com.ssx.common.domain.Result;
import com.ssx.resource.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{rid}")
    public Result comment(@PathVariable int rid, int pageNo, int pageSize) {
        return commentService.findByRid(rid, pageNo, pageSize);
    }

    @PostMapping("/add")
    public Result addComment(String comment, Long rid) {
        return commentService.addComment(comment, rid);
    }

}

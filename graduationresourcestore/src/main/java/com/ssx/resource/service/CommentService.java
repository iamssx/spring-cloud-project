package com.ssx.resource.service;

import com.ssx.common.domain.Result;

public interface CommentService {
    Result findByRid(long rid, int pageNo, int pageSize);

    Result addComment(String comment, Long rid);


}

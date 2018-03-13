package com.ssx.domain;

import com.ssx.common.domain.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findBySrcAndSrcId(String src, String srcId);

    List<User> findBySrc(String src, PageRequest pageRequest);

}

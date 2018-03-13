package com.ssx.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppCommentRepository extends JpaRepository<AppComment, Long> {

    @Query("select c.cid, c.user.uid, c.content from AppComment c where c.app = ?1")
    List<AppComment> findByApp(App app, Pageable pageable);

    void deleteByApp(App app);
}

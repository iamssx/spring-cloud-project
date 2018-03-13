package com.ssx.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppPicUrlRepository extends JpaRepository<AppPicUrl, Long> {

    List<AppPicUrl> findByApp(App app);

    void deleteByApp(App app);

}

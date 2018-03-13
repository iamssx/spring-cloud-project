package com.ssx.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppRepository extends JpaRepository<App, Long> {

    List<App> findByAppType(String appType, Pageable pageable);

    List<App> findByScore(Short score, Pageable pageable);

    List<App> findByScoreLessThan(Short score, Pageable pageable);

    List<App> findByScoreGreaterThan(Short score, Pageable pageable);

    List<App> findByScoreBetween(Short low, Short high, Pageable pageable);

    App findByName(String name);

    App findByUuid(String uuid);

    @Modifying
    @Query(value = "update App a set a.totalPeople = a.totalPeople + :alter where a.aid= :aid")
    void incrTotalPeople(@Param("alter") Long alter, @Param("aid") Long aid);

    @Modifying
    @Query(value = "update App a set a.score = a.score + :alter where a. aid = :aid")
    void incrScore(@Param("alter") Short alter, @Param("aid") Long aid);
}

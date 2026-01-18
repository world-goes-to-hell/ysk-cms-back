package com.ysk.cms.domain.admin.analytics.repository;

import com.ysk.cms.domain.admin.analytics.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    Optional<Visitor> findBySiteCodeAndVisitorId(String siteCode, String visitorId);

    // 신규 방문자 수 (특정 기간에 첫 방문)
    @Query("SELECT COUNT(v) FROM Visitor v WHERE v.siteCode = :siteCode " +
           "AND v.firstVisitAt BETWEEN :startDate AND :endDate")
    Long countNewVisitors(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // 재방문자 수 (첫 방문이 기간 이전, 마지막 방문이 기간 내)
    @Query("SELECT COUNT(v) FROM Visitor v WHERE v.siteCode = :siteCode " +
           "AND v.firstVisitAt < :startDate AND v.lastVisitAt BETWEEN :startDate AND :endDate")
    Long countReturningVisitors(
            @Param("siteCode") String siteCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}

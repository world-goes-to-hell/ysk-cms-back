package com.ysk.cms.domain.chat.repository;

import com.ysk.cms.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 채팅방의 메시지 조회 (최신순)
     */
    @Query("SELECT cm FROM ChatMessage cm " +
           "JOIN FETCH cm.sender " +
           "WHERE cm.chatRoom.id = :roomId " +
           "ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findByRoomIdOrderByCreatedAtDesc(@Param("roomId") Long roomId, Pageable pageable);

    /**
     * 특정 시간 이후의 읽지 않은 메시지 수
     */
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
           "WHERE cm.chatRoom.id = :roomId " +
           "AND cm.createdAt > :lastReadAt " +
           "AND cm.sender.id != :userId")
    int countUnreadMessages(
            @Param("roomId") Long roomId,
            @Param("lastReadAt") LocalDateTime lastReadAt,
            @Param("userId") Long userId
    );

    /**
     * 읽지 않은 메시지 수 (lastReadAt이 null인 경우 - 모든 메시지)
     */
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
           "WHERE cm.chatRoom.id = :roomId " +
           "AND cm.sender.id != :userId")
    int countAllUnreadMessages(
            @Param("roomId") Long roomId,
            @Param("userId") Long userId
    );
}

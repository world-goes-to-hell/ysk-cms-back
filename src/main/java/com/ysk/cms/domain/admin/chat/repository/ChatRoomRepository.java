package com.ysk.cms.domain.admin.chat.repository;

import com.ysk.cms.domain.admin.chat.entity.ChatRoom;
import com.ysk.cms.domain.admin.chat.entity.ChatRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * 사용자가 참여중인 모든 채팅방 조회 (lastMessageAt 기준 최신순)
     * participants와 user를 함께 FETCH하여 N+1 문제 방지
     */
    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
           "LEFT JOIN FETCH cr.participants p " +
           "LEFT JOIN FETCH p.user " +
           "WHERE p.user.id = :userId AND p.isActive = true " +
           "ORDER BY cr.lastMessageAt DESC NULLS LAST")
    List<ChatRoom> findAllByUserIdOrderByLastMessageAtDesc(@Param("userId") Long userId);

    /**
     * 채팅방 ID 목록으로 읽지 않은 메시지 수 일괄 조회를 위한 메서드
     */
    @Query("SELECT cr FROM ChatRoom cr " +
           "LEFT JOIN FETCH cr.participants p " +
           "LEFT JOIN FETCH p.user " +
           "WHERE cr.id IN :roomIds")
    List<ChatRoom> findAllByIdWithParticipants(@Param("roomIds") List<Long> roomIds);

    /**
     * 두 사용자 간의 1:1 채팅방 조회
     */
    @Query("SELECT cr FROM ChatRoom cr " +
           "WHERE cr.type = :type " +
           "AND EXISTS (SELECT 1 FROM ChatRoomUser cru1 WHERE cru1.chatRoom = cr AND cru1.user.id = :userId1 AND cru1.isActive = true) " +
           "AND EXISTS (SELECT 1 FROM ChatRoomUser cru2 WHERE cru2.chatRoom = cr AND cru2.user.id = :userId2 AND cru2.isActive = true) " +
           "AND (SELECT COUNT(cru) FROM ChatRoomUser cru WHERE cru.chatRoom = cr AND cru.isActive = true) = 2")
    Optional<ChatRoom> findPrivateChatRoom(
            @Param("type") ChatRoomType type,
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2
    );
}

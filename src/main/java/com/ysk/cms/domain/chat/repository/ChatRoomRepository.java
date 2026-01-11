package com.ysk.cms.domain.chat.repository;

import com.ysk.cms.domain.chat.entity.ChatRoom;
import com.ysk.cms.domain.chat.entity.ChatRoomType;
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
     */
    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
           "JOIN cr.participants p " +
           "WHERE p.user.id = :userId AND p.isActive = true " +
           "ORDER BY cr.lastMessageAt DESC NULLS LAST")
    List<ChatRoom> findAllByUserIdOrderByLastMessageAtDesc(@Param("userId") Long userId);

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

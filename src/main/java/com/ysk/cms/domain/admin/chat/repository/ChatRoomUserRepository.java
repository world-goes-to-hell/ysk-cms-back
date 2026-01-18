package com.ysk.cms.domain.admin.chat.repository;

import com.ysk.cms.domain.admin.chat.entity.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {

    /**
     * 채팅방의 활성 참여자 조회
     */
    @Query("SELECT cru FROM ChatRoomUser cru " +
           "JOIN FETCH cru.user " +
           "WHERE cru.chatRoom.id = :roomId AND cru.isActive = true")
    List<ChatRoomUser> findActiveParticipantsByRoomId(@Param("roomId") Long roomId);

    /**
     * 채팅방의 모든 참여자 조회 (활성 + 비활성)
     * 1:1 채팅에서 나간 사용자 정보도 표시하기 위함
     */
    @Query("SELECT cru FROM ChatRoomUser cru " +
           "JOIN FETCH cru.user " +
           "WHERE cru.chatRoom.id = :roomId")
    List<ChatRoomUser> findAllParticipantsByRoomId(@Param("roomId") Long roomId);

    /**
     * 특정 사용자의 채팅방 참여 정보 조회
     */
    @Query("SELECT cru FROM ChatRoomUser cru " +
           "WHERE cru.chatRoom.id = :roomId AND cru.user.id = :userId")
    Optional<ChatRoomUser> findByRoomIdAndUserId(
            @Param("roomId") Long roomId,
            @Param("userId") Long userId
    );

    /**
     * 사용자가 채팅방 참여자인지 확인
     */
    @Query("SELECT CASE WHEN COUNT(cru) > 0 THEN true ELSE false END " +
           "FROM ChatRoomUser cru " +
           "WHERE cru.chatRoom.id = :roomId AND cru.user.id = :userId AND cru.isActive = true")
    boolean isParticipant(@Param("roomId") Long roomId, @Param("userId") Long userId);
}

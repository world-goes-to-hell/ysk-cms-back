package com.ysk.cms.domain.chat.service;

import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.chat.dto.*;
import com.ysk.cms.domain.chat.entity.*;
import com.ysk.cms.domain.chat.repository.*;
import com.ysk.cms.domain.user.entity.User;
import com.ysk.cms.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * username으로 userId 조회
     */
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getId();
    }

    /**
     * 내 채팅방 목록 조회
     */
    public List<ChatRoomDto> getMyChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByUserIdOrderByLastMessageAtDesc(userId);

        return chatRooms.stream()
                .map(room -> {
                    List<ChatRoomUserDto> participants = getParticipants(room.getId());
                    int unreadCount = getUnreadCount(room.getId(), userId);
                    return ChatRoomDto.from(room, participants, unreadCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * 1:1 채팅방 생성 또는 조회
     */
    @Transactional
    public ChatRoomDto getOrCreatePrivateRoom(Long currentUserId, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "자기 자신과 채팅할 수 없습니다");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 기존 1:1 채팅방 조회
        return chatRoomRepository.findPrivateChatRoom(ChatRoomType.PRIVATE, currentUserId, targetUserId)
                .map(room -> {
                    List<ChatRoomUserDto> participants = getParticipants(room.getId());
                    int unreadCount = getUnreadCount(room.getId(), currentUserId);
                    return ChatRoomDto.from(room, participants, unreadCount);
                })
                .orElseGet(() -> createPrivateRoom(currentUser, targetUser));
    }

    private ChatRoomDto createPrivateRoom(User user1, User user2) {
        ChatRoom chatRoom = ChatRoom.builder()
                .type(ChatRoomType.PRIVATE)
                .build();
        chatRoomRepository.save(chatRoom);

        ChatRoomUser participant1 = ChatRoomUser.builder()
                .chatRoom(chatRoom)
                .user(user1)
                .build();
        ChatRoomUser participant2 = ChatRoomUser.builder()
                .chatRoom(chatRoom)
                .user(user2)
                .build();

        chatRoomUserRepository.save(participant1);
        chatRoomUserRepository.save(participant2);

        List<ChatRoomUserDto> participants = List.of(
                ChatRoomUserDto.from(participant1),
                ChatRoomUserDto.from(participant2)
        );

        ChatRoomDto roomDto = ChatRoomDto.from(chatRoom, participants, 0);

        // 상대방에게 새 채팅방 알림
        notifyNewRoom(user2.getId(), roomDto);

        return roomDto;
    }

    /**
     * 그룹 채팅방 생성
     */
    @Transactional
    public ChatRoomDto createGroupRoom(Long creatorId, ChatRoomCreateRequest request) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ChatRoom chatRoom = ChatRoom.builder()
                .name(request.getName())
                .type(ChatRoomType.GROUP)
                .build();
        chatRoomRepository.save(chatRoom);

        // 생성자 추가
        ChatRoomUser creatorParticipant = ChatRoomUser.builder()
                .chatRoom(chatRoom)
                .user(creator)
                .build();
        chatRoomUserRepository.save(creatorParticipant);

        // 초대된 사용자들 추가
        List<Long> invitedUserIds = request.getParticipantIds().stream()
                .filter(id -> !id.equals(creatorId))
                .collect(Collectors.toList());

        List<ChatRoomUser> participants = invitedUserIds.stream()
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                    return ChatRoomUser.builder()
                            .chatRoom(chatRoom)
                            .user(user)
                            .build();
                })
                .collect(Collectors.toList());
        chatRoomUserRepository.saveAll(participants);

        List<ChatRoomUserDto> participantDtos = getParticipants(chatRoom.getId());
        ChatRoomDto roomDto = ChatRoomDto.from(chatRoom, participantDtos, 0);

        // 초대된 사용자들에게 새 채팅방 알림
        invitedUserIds.forEach(userId -> notifyNewRoom(userId, roomDto));

        return roomDto;
    }

    /**
     * 채팅방 상세 조회
     */
    public ChatRoomDto getChatRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "채팅방을 찾을 수 없습니다"));

        if (!chatRoomUserRepository.isParticipant(roomId, userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "채팅방 참여자가 아닙니다");
        }

        List<ChatRoomUserDto> participants = getParticipants(roomId);
        int unreadCount = getUnreadCount(roomId, userId);
        return ChatRoomDto.from(chatRoom, participants, unreadCount);
    }

    /**
     * 메시지 목록 조회
     */
    public Page<ChatMessageDto> getMessages(Long roomId, Long userId, int page, int size) {
        if (!chatRoomUserRepository.isParticipant(roomId, userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "채팅방 참여자가 아닙니다");
        }

        return chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, PageRequest.of(page, size))
                .map(ChatMessageDto::from);
    }

    /**
     * 메시지 전송
     */
    @Transactional
    public ChatMessageDto sendMessage(Long roomId, Long senderId, ChatMessageRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "채팅방을 찾을 수 없습니다"));

        if (!chatRoomUserRepository.isParticipant(roomId, senderId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "채팅방 참여자가 아닙니다");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .type(request.getType())
                .content(request.getContent())
                .fileName(request.getFileName())
                .fileUrl(request.getFileUrl())
                .fileSize(request.getFileSize())
                .build();
        chatMessageRepository.save(message);

        // 채팅방 lastMessage 업데이트
        String lastMessagePreview = request.getType() == MessageType.TEXT
                ? request.getContent()
                : "[" + request.getType().name() + "]";
        chatRoom.updateLastMessage(lastMessagePreview);

        ChatMessageDto messageDto = ChatMessageDto.from(message);

        // WebSocket으로 메시지 브로드캐스트
        messagingTemplate.convertAndSend("/topic/chat/room/" + roomId, messageDto);

        // 참여자들에게 새 메시지 알림 및 읽지 않은 메시지 수 업데이트
        notifyNewMessage(roomId, senderId, messageDto);
        notifyUnreadCount(roomId, senderId);

        return messageDto;
    }

    /**
     * 읽음 처리
     */
    @Transactional
    public void markAsRead(Long roomId, Long userId) {
        ChatRoomUser participant = chatRoomUserRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED, "채팅방 참여자가 아닙니다"));

        participant.updateLastReadAt();
    }

    /**
     * 채팅방 나가기
     */
    @Transactional
    public void leaveRoom(Long roomId, Long userId) {
        ChatRoomUser participant = chatRoomUserRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED, "채팅방 참여자가 아닙니다"));

        participant.leave();

        // 시스템 메시지 전송
        User user = participant.getUser();
        sendSystemMessage(roomId, user.getName() + "님이 채팅방을 나갔습니다.");
    }

    /**
     * 채팅방 이름 변경 (그룹 채팅)
     */
    @Transactional
    public ChatRoomDto updateRoomName(Long roomId, Long userId, String newName) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "채팅방을 찾을 수 없습니다"));

        if (chatRoom.getType() != ChatRoomType.GROUP) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "1:1 채팅방은 이름을 변경할 수 없습니다");
        }

        if (!chatRoomUserRepository.isParticipant(roomId, userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "채팅방 참여자가 아닙니다");
        }

        chatRoom.updateName(newName);

        List<ChatRoomUserDto> participants = getParticipants(roomId);
        int unreadCount = getUnreadCount(roomId, userId);
        return ChatRoomDto.from(chatRoom, participants, unreadCount);
    }

    /**
     * 채팅 가능한 사용자 목록 (관리자 목록)
     */
    public List<ChatRoomUserDto> getAvailableUsers(Long currentUserId) {
        return userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .filter(User::isActive)
                .map(user -> ChatRoomUserDto.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .nickname(user.getName())
                        .build())
                .collect(Collectors.toList());
    }

    // === Private Helper Methods ===

    private List<ChatRoomUserDto> getParticipants(Long roomId) {
        return chatRoomUserRepository.findActiveParticipantsByRoomId(roomId).stream()
                .map(ChatRoomUserDto::from)
                .collect(Collectors.toList());
    }

    private int getUnreadCount(Long roomId, Long userId) {
        return chatRoomUserRepository.findByRoomIdAndUserId(roomId, userId)
                .map(participant -> {
                    LocalDateTime lastReadAt = participant.getLastReadAt();
                    if (lastReadAt == null) {
                        return chatMessageRepository.countAllUnreadMessages(roomId, userId);
                    }
                    return chatMessageRepository.countUnreadMessages(roomId, lastReadAt, userId);
                })
                .orElse(0);
    }

    private void sendSystemMessage(Long roomId, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom == null) return;

        // 시스템 메시지는 sender가 필요하므로 별도 처리
        ChatMessageDto systemMessage = ChatMessageDto.builder()
                .roomId(roomId)
                .type(MessageType.SYSTEM)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/chat/room/" + roomId, systemMessage);
    }

    private void notifyNewRoom(Long userId, ChatRoomDto room) {
        messagingTemplate.convertAndSend(
                "/topic/chat/user/" + userId + "/room",
                room
        );
        log.debug("Sent new room notification to user {}: roomId={}", userId, room.getId());
    }

    private void notifyNewMessage(Long roomId, Long senderId, ChatMessageDto message) {
        List<ChatRoomUser> participants = chatRoomUserRepository.findActiveParticipantsByRoomId(roomId);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom == null) return;

        for (ChatRoomUser participant : participants) {
            if (!participant.getUser().getId().equals(senderId)) {
                Long userId = participant.getUser().getId();
                // 채팅방 이름 결정 (1:1이면 발신자 이름, 그룹이면 채팅방 이름)
                String roomName = chatRoom.getType() == ChatRoomType.GROUP
                        ? (chatRoom.getName() != null ? chatRoom.getName() : "그룹 채팅")
                        : message.getSenderNickname();

                NewMessageNotification notification = NewMessageNotification.builder()
                        .roomId(roomId)
                        .roomName(roomName)
                        .roomType(chatRoom.getType().name())
                        .senderNickname(message.getSenderNickname())
                        .content(message.getContent())
                        .messageType(message.getType().name())
                        .createdAt(message.getCreatedAt())
                        .build();

                messagingTemplate.convertAndSend(
                        "/topic/chat/user/" + userId + "/message",
                        notification
                );
                log.debug("Sent new message notification to user {}", userId);
            }
        }
    }

    private void notifyUnreadCount(Long roomId, Long senderId) {
        List<ChatRoomUser> participants = chatRoomUserRepository.findActiveParticipantsByRoomId(roomId);

        for (ChatRoomUser participant : participants) {
            if (!participant.getUser().getId().equals(senderId)) {
                Long userId = participant.getUser().getId();
                int unreadCount = getUnreadCount(roomId, userId);
                // 사용자별 토픽으로 안읽음 알림 전송
                messagingTemplate.convertAndSend(
                        "/topic/chat/user/" + userId + "/unread",
                        new UnreadCountNotification(roomId, unreadCount)
                );
                log.debug("Sent unread notification to user {}: roomId={}, count={}", userId, roomId, unreadCount);
            }
        }
    }

    // 읽지 않은 메시지 수 알림용 내부 클래스
    @lombok.Getter
    @lombok.AllArgsConstructor
    private static class UnreadCountNotification {
        private Long roomId;
        private int unreadCount;
    }

    // 새 메시지 알림용 내부 클래스
    @lombok.Getter
    @lombok.Builder
    private static class NewMessageNotification {
        private Long roomId;
        private String roomName;
        private String roomType;
        private String senderNickname;
        private String content;
        private String messageType;
        private LocalDateTime createdAt;
    }
}

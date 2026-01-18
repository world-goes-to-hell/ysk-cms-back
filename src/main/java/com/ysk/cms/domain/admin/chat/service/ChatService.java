package com.ysk.cms.domain.admin.chat.service;

import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.admin.chat.dto.*;
import com.ysk.cms.domain.admin.chat.entity.*;
import com.ysk.cms.domain.admin.chat.repository.ChatMessageRepository;
import com.ysk.cms.domain.admin.chat.repository.ChatRoomRepository;
import com.ysk.cms.domain.admin.chat.repository.ChatRoomUserRepository;
import com.ysk.cms.domain.admin.chat.dto.*;
import com.ysk.cms.domain.admin.chat.entity.*;
import com.ysk.cms.domain.admin.chat.repository.*;
import com.ysk.cms.domain.admin.user.entity.User;
import com.ysk.cms.domain.admin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
     * 내 채팅방 목록 조회 (N+1 최적화 - FETCH JOIN 활용)
     */
    public List<ChatRoomDto> getMyChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByUserIdOrderByLastMessageAtDesc(userId);

        return chatRooms.stream()
                .map(room -> {
                    // 1:1 채팅은 모든 참여자 (나간 사용자 포함), 그룹 채팅은 활성 참여자만
                    List<ChatRoomUserDto> participants;
                    if (room.getType() == ChatRoomType.PRIVATE) {
                        // 1:1 채팅: 별도 쿼리로 모든 참여자 조회 (나간 사용자 이름 표시 위해)
                        participants = chatRoomUserRepository.findAllParticipantsByRoomId(room.getId()).stream()
                                .map(ChatRoomUserDto::from)
                                .collect(Collectors.toList());
                    } else {
                        // 그룹 채팅: FETCH JOIN으로 가져온 활성 참여자만 사용
                        participants = room.getParticipants().stream()
                                .filter(ChatRoomUser::getIsActive)
                                .map(ChatRoomUserDto::from)
                                .collect(Collectors.toList());
                    }

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

        // 1:1 채팅은 모든 참여자 (나간 사용자 포함), 그룹은 활성 참여자만
        List<ChatRoomUserDto> participants = chatRoom.getType() == ChatRoomType.PRIVATE
                ? getAllParticipants(roomId)
                : getParticipants(roomId);

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

    // 파일 업로드 제한 상수
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "jpg", "jpeg", "png", "gif", "webp",  // 이미지
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt",  // 문서
            "zip", "rar", "7z"  // 압축
    );

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

        // 1:1 채팅에서 나간 상대방 다시 활성화 (rejoin)
        List<Long> rejoinedUserIds = new java.util.ArrayList<>();
        if (chatRoom.getType() == ChatRoomType.PRIVATE) {
            rejoinedUserIds = rejoinInactiveParticipants(roomId, senderId);
        }

        // 파일 첨부 시 검증
        if (request.getType() == MessageType.FILE || request.getType() == MessageType.IMAGE) {
            validateFileUpload(request);
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

        // 트랜잭션 커밋 후 WebSocket 전송 (DB 커넥션 점유 시간 최소화)
        final Long finalRoomId = roomId;
        final Long finalSenderId = senderId;
        final List<Long> finalRejoinedUserIds = rejoinedUserIds;
        final ChatRoomType roomType = chatRoom.getType();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // WebSocket으로 메시지 브로드캐스트
                messagingTemplate.convertAndSend("/topic/chat/room/" + finalRoomId, messageDto);
                // 참여자들에게 새 메시지 알림 및 읽지 않은 메시지 수 업데이트
                notifyNewMessage(finalRoomId, finalSenderId, messageDto);
                notifyUnreadCount(finalRoomId, finalSenderId);

                // 재참여한 사용자가 있으면 채팅방 목록 갱신 알림
                if (!finalRejoinedUserIds.isEmpty()) {
                    notifyParticipantsChanged(finalRoomId, roomType);
                    // 재참여한 사용자들에게 채팅방 목록 추가 알림
                    notifyRoomToRejoinedUsers(finalRoomId, finalRejoinedUserIds);
                }
            }
        });

        return messageDto;
    }

    /**
     * 1:1 채팅에서 비활성 참여자 재활성화
     * @return 재참여한 사용자 ID 목록
     */
    private List<Long> rejoinInactiveParticipants(Long roomId, Long senderId) {
        List<ChatRoomUser> allParticipants = chatRoomUserRepository.findAllParticipantsByRoomId(roomId);
        List<Long> rejoinedUserIds = new java.util.ArrayList<>();

        for (ChatRoomUser participant : allParticipants) {
            // 보낸 사람이 아니고 비활성 상태인 경우 재활성화
            if (!participant.getUser().getId().equals(senderId) && !participant.getIsActive()) {
                participant.rejoin();
                rejoinedUserIds.add(participant.getUser().getId());
                log.info("[Chat] User {} rejoined room {} by message from user {}",
                        participant.getUser().getId(), roomId, senderId);
            }
        }
        return rejoinedUserIds;
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
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "채팅방을 찾을 수 없습니다"));

        ChatRoomUser participant = chatRoomUserRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCESS_DENIED, "채팅방 참여자가 아닙니다"));

        participant.leave();

        // 시스템 메시지 전송
        User user = participant.getUser();
        String userName = user.getName();

        // 트랜잭션 커밋 후 WebSocket 알림
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                sendSystemMessage(roomId, userName + "님이 채팅방을 나갔습니다.");

                // 참여자 목록 변경 알림 (그룹 채팅의 경우 인원수 업데이트용)
                notifyParticipantsChanged(roomId, chatRoom.getType());
            }
        });
    }

    /**
     * 참여자 목록 변경 알림
     */
    private void notifyParticipantsChanged(Long roomId, ChatRoomType roomType) {
        // 1:1 채팅은 모든 참여자, 그룹 채팅은 활성 참여자만
        List<ChatRoomUserDto> participants = roomType == ChatRoomType.PRIVATE
                ? getAllParticipants(roomId)
                : getParticipants(roomId);

        // 채팅방 참여자 변경 알림 브로드캐스트
        messagingTemplate.convertAndSend(
                "/topic/chat/room/" + roomId + "/participants",
                participants
        );
    }

    /**
     * 모든 참여자 조회 (활성 + 비활성) - 1:1 채팅용
     */
    private List<ChatRoomUserDto> getAllParticipants(Long roomId) {
        return chatRoomUserRepository.findAllParticipantsByRoomId(roomId).stream()
                .map(ChatRoomUserDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 채팅방에 사용자 초대
     * - 1:1 채팅방: 새 그룹 채팅방 생성 (기존 1:1 유지)
     * - 그룹 채팅방: 기존 채팅방에 사용자 추가
     */
    @Transactional
    public ChatRoomDto inviteUsers(Long roomId, Long inviterId, ChatInviteRequest request) {
        log.info("[Chat] inviteUsers called - roomId: {}, inviterId: {}, userIds: {}",
                roomId, inviterId, request.getUserIds());

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "채팅방을 찾을 수 없습니다"));

        log.info("[Chat] Room found - type: {}", chatRoom.getType());

        boolean isParticipant = chatRoomUserRepository.isParticipant(roomId, inviterId);
        log.info("[Chat] isParticipant check - result: {}", isParticipant);

        if (!isParticipant) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "채팅방 참여자가 아닙니다");
        }

        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Long> newUserIds = request.getUserIds().stream()
                .filter(id -> !id.equals(inviterId))
                .collect(Collectors.toList());

        if (newUserIds.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "초대할 사용자를 선택해주세요");
        }

        if (chatRoom.getType() == ChatRoomType.PRIVATE) {
            // 1:1 채팅방 → 새 그룹 채팅방 생성
            return createGroupFromPrivate(chatRoom, inviter, newUserIds, request.getGroupName());
        } else {
            // 그룹 채팅방 → 기존 채팅방에 사용자 추가
            return addUsersToGroup(chatRoom, inviter, newUserIds);
        }
    }

    /**
     * 1:1 채팅방에서 그룹 채팅방 생성
     */
    private ChatRoomDto createGroupFromPrivate(ChatRoom privateRoom, User inviter, List<Long> newUserIds, String groupName) {
        // 기존 1:1 채팅방의 참여자 조회
        List<ChatRoomUser> existingParticipants = chatRoomUserRepository.findAllParticipantsByRoomId(privateRoom.getId());
        List<Long> existingUserIds = existingParticipants.stream()
                .map(p -> p.getUser().getId())
                .collect(Collectors.toList());

        // 새 그룹 채팅방 생성
        String roomName = groupName;
        if (roomName == null || roomName.isBlank()) {
            // 그룹명이 없으면 참여자 이름으로 생성
            List<String> names = new java.util.ArrayList<>();
            for (ChatRoomUser p : existingParticipants) {
                names.add(p.getUser().getName());
            }
            for (Long userId : newUserIds) {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    names.add(user.getName());
                }
            }
            roomName = String.join(", ", names);
        }

        ChatRoom newRoom = ChatRoom.builder()
                .name(roomName)
                .type(ChatRoomType.GROUP)
                .build();
        chatRoomRepository.save(newRoom);

        // 기존 참여자들 추가
        for (Long userId : existingUserIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            ChatRoomUser participant = ChatRoomUser.builder()
                    .chatRoom(newRoom)
                    .user(user)
                    .build();
            chatRoomUserRepository.save(participant);
        }

        // 새로 초대된 사용자들 추가
        List<Long> addedUserIds = new java.util.ArrayList<>();
        for (Long userId : newUserIds) {
            if (!existingUserIds.contains(userId)) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                ChatRoomUser participant = ChatRoomUser.builder()
                        .chatRoom(newRoom)
                        .user(user)
                        .build();
                chatRoomUserRepository.save(participant);
                addedUserIds.add(userId);
            }
        }

        List<ChatRoomUserDto> participantDtos = getParticipants(newRoom.getId());
        ChatRoomDto roomDto = ChatRoomDto.from(newRoom, participantDtos, 0);

        // 트랜잭션 커밋 후 알림
        final Long newRoomId = newRoom.getId();
        final String inviterName = inviter.getName();
        final List<Long> allOtherUserIds = existingUserIds.stream()
                .filter(id -> !id.equals(inviter.getId()))
                .collect(Collectors.toList());
        allOtherUserIds.addAll(addedUserIds);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 시스템 메시지
                sendSystemMessage(newRoomId, inviterName + "님이 그룹 채팅방을 만들었습니다.");

                // 다른 참여자들에게 새 채팅방 알림
                ChatRoomDto finalRoomDto = ChatRoomDto.from(
                        chatRoomRepository.findById(newRoomId).orElse(null),
                        getParticipants(newRoomId),
                        0
                );
                for (Long userId : allOtherUserIds) {
                    notifyNewRoom(userId, finalRoomDto);
                }
            }
        });

        return roomDto;
    }

    /**
     * 그룹 채팅방에 사용자 추가
     */
    private ChatRoomDto addUsersToGroup(ChatRoom chatRoom, User inviter, List<Long> newUserIds) {
        List<Long> addedUserIds = new java.util.ArrayList<>();
        List<String> addedUserNames = new java.util.ArrayList<>();

        for (Long userId : newUserIds) {
            // 이미 참여 중인지 확인
            java.util.Optional<ChatRoomUser> existingParticipant =
                    chatRoomUserRepository.findByRoomIdAndUserId(chatRoom.getId(), userId);

            if (existingParticipant.isPresent()) {
                ChatRoomUser participant = existingParticipant.get();
                if (!participant.getIsActive()) {
                    // 비활성 상태면 재활성화
                    participant.rejoin();
                    addedUserIds.add(userId);
                    addedUserNames.add(participant.getUser().getName());
                }
                // 이미 활성 상태면 무시
            } else {
                // 새 참여자 추가
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                ChatRoomUser newParticipant = ChatRoomUser.builder()
                        .chatRoom(chatRoom)
                        .user(user)
                        .build();
                chatRoomUserRepository.save(newParticipant);
                addedUserIds.add(userId);
                addedUserNames.add(user.getName());
            }
        }

        if (addedUserIds.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 모든 사용자가 채팅방에 참여 중입니다");
        }

        List<ChatRoomUserDto> participantDtos = getParticipants(chatRoom.getId());
        int unreadCount = getUnreadCount(chatRoom.getId(), inviter.getId());
        ChatRoomDto roomDto = ChatRoomDto.from(chatRoom, participantDtos, unreadCount);

        // 트랜잭션 커밋 후 알림
        final Long roomId = chatRoom.getId();
        final String inviterName = inviter.getName();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 시스템 메시지
                String message = inviterName + "님이 " + String.join(", ", addedUserNames) + "님을 초대했습니다.";
                sendSystemMessage(roomId, message);

                // 참여자 목록 변경 알림
                notifyParticipantsChanged(roomId, ChatRoomType.GROUP);

                // 새 참여자들에게 채팅방 알림
                ChatRoomDto finalRoomDto = ChatRoomDto.from(
                        chatRoomRepository.findById(roomId).orElse(null),
                        getParticipants(roomId),
                        0
                );
                for (Long userId : addedUserIds) {
                    notifyNewRoom(userId, finalRoomDto);
                }
            }
        });

        return roomDto;
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

    /**
     * 파일 업로드 검증
     */
    private void validateFileUpload(ChatMessageRequest request) {
        // 파일 크기 검증
        if (request.getFileSize() != null && request.getFileSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                    "파일 크기는 10MB를 초과할 수 없습니다");
        }

        // 파일 확장자 검증
        if (request.getFileName() != null) {
            String fileName = request.getFileName().toLowerCase();
            String extension = fileName.contains(".")
                    ? fileName.substring(fileName.lastIndexOf(".") + 1)
                    : "";

            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "허용되지 않는 파일 형식입니다. 허용 형식: " + String.join(", ", ALLOWED_EXTENSIONS));
            }
        }
    }

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

    /**
     * 재참여한 사용자들에게 채팅방 목록 추가 알림
     */
    private void notifyRoomToRejoinedUsers(Long roomId, List<Long> rejoinedUserIds) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom == null) return;

        List<ChatRoomUserDto> participants = getAllParticipants(roomId);

        for (Long userId : rejoinedUserIds) {
            int unreadCount = getUnreadCount(roomId, userId);
            ChatRoomDto roomDto = ChatRoomDto.from(chatRoom, participants, unreadCount);
            notifyNewRoom(userId, roomDto);
            log.info("[Chat] Sent room notification to rejoined user {}: roomId={}", userId, roomId);
        }
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

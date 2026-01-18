package com.ysk.cms.domain.admin.chat.controller;

import com.ysk.cms.domain.admin.chat.dto.ChatMessageRequest;
import com.ysk.cms.domain.admin.chat.dto.TypingNotification;
import com.ysk.cms.domain.admin.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 메시지 전송
     * 클라이언트 → /app/chat.send/{roomId}
     * 브로드캐스트 → /topic/chat/room/{roomId}
     */
    @MessageMapping("/chat.send/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload ChatMessageRequest request,
            Principal principal
    ) {
        log.debug("Message received for room {}: {}", roomId, request.getContent());
        Long senderId = extractUserId(principal);
        chatService.sendMessage(roomId, senderId, request);
    }

    /**
     * 타이핑 상태 알림
     * 클라이언트 → /app/chat.typing/{roomId}
     * 브로드캐스트 → /topic/chat/room/{roomId}/typing
     */
    @MessageMapping("/chat.typing/{roomId}")
    public void typing(
            @DestinationVariable Long roomId,
            @Payload TypingNotification notification,
            Principal principal
    ) {
        log.debug("Typing notification for room {}: {}", roomId, notification.isTyping());
        messagingTemplate.convertAndSend(
                "/topic/chat/room/" + roomId + "/typing",
                notification
        );
    }

    /**
     * 채팅방 입장 (읽음 처리)
     * 클라이언트 → /app/chat.join/{roomId}
     */
    @MessageMapping("/chat.join/{roomId}")
    @SendToUser("/queue/chat/joined")
    public String joinRoom(
            @DestinationVariable Long roomId,
            Principal principal
    ) {
        log.debug("User joined room: {}", roomId);
        Long userId = extractUserId(principal);
        chatService.markAsRead(roomId, userId);
        return "Joined room " + roomId;
    }

    private Long extractUserId(Principal principal) {
        // principal.getName()은 username을 반환하므로 ChatService를 통해 userId 조회
        String username = principal.getName();
        return chatService.getUserIdByUsername(username);
    }
}

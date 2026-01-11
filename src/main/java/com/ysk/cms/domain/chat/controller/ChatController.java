package com.ysk.cms.domain.chat.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.domain.chat.dto.*;
import com.ysk.cms.domain.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 내 채팅방 목록 조회
     */
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomDto>>> getMyChatRooms(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = chatService.getUserIdByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(chatService.getMyChatRooms(userId)));
    }

    /**
     * 채팅방 상세 조회
     */
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<ChatRoomDto>> getChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = chatService.getUserIdByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(chatService.getChatRoom(roomId, userId)));
    }

    /**
     * 1:1 채팅방 생성/조회
     */
    @PostMapping("/rooms/private/{targetUserId}")
    public ResponseEntity<ApiResponse<ChatRoomDto>> getOrCreatePrivateRoom(
            @PathVariable Long targetUserId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = chatService.getUserIdByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(chatService.getOrCreatePrivateRoom(userId, targetUserId)));
    }

    /**
     * 그룹 채팅방 생성
     */
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoomDto>> createGroupRoom(
            @Valid @RequestBody ChatRoomCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = chatService.getUserIdByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(chatService.createGroupRoom(userId, request)));
    }

    /**
     * 채팅방 이름 변경 (그룹 채팅)
     */
    @PatchMapping("/rooms/{roomId}/name")
    public ResponseEntity<ApiResponse<ChatRoomDto>> updateRoomName(
            @PathVariable Long roomId,
            @RequestBody String newName,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = chatService.getUserIdByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(chatService.updateRoomName(roomId, userId, newName)));
    }

    /**
     * 메시지 목록 조회 (페이징)
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<Page<ChatMessageDto>>> getMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = chatService.getUserIdByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(chatService.getMessages(roomId, userId, page, size)));
    }

    /**
     * 읽음 처리
     */
    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = chatService.getUserIdByUsername(userDetails.getUsername());
        chatService.markAsRead(roomId, userId);
        return ResponseEntity.ok(ApiResponse.success("읽음 처리 완료"));
    }

    /**
     * 채팅방 나가기
     */
    @PostMapping("/rooms/{roomId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = chatService.getUserIdByUsername(userDetails.getUsername());
        chatService.leaveRoom(roomId, userId);
        return ResponseEntity.ok(ApiResponse.success("채팅방 나가기 완료"));
    }

    /**
     * 채팅 가능한 사용자 목록
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<ChatRoomUserDto>>> getAvailableUsers(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = chatService.getUserIdByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(chatService.getAvailableUsers(userId)));
    }
}

package com.app.notificationservice.service;

import com.app.notificationservice.dto.NotificationDto;
import com.app.notificationservice.entity.Notification;
import com.app.notificationservice.enums.NotificationType;
import com.app.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrUserIdIsNullOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToDto);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public void saveUserUpgradeNotification(Long userId, String username, String newRole) {
        Notification notification = Notification.builder()
                .title("Premium Upgrade!")
                .message("Congratulations " + username + "! Your account has been upgraded to " + newRole + ".")
                .type(NotificationType.ROLE_UPGRADE)
                .userId(userId)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        notificationRepository.save(notification);
        log.info("Saved upgrade notification for user: {}", userId);
    }

    @Transactional
    public void saveNewTemplateNotification(String templateName) {
        Notification notification = Notification.builder()
                .title("New Template Available")
                .message("Check out our latest resume template: " + templateName)
                .type(NotificationType.NEW_TEMPLATE)
                .userId(null) // Global notification
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        notificationRepository.save(notification);
        log.info("Saved global template notification: {}", templateName);
    }

    public NotificationDto createNotification(Notification notification) {
        Notification saved = notificationRepository.save(notification);
        log.info("Created custom notification: {}", saved.getTitle());
        return mapToDto(saved);
    }

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .userId(notification.getUserId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}


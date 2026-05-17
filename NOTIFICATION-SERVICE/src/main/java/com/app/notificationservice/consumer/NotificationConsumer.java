package com.app.notificationservice.consumer;

import com.app.notificationservice.config.RabbitMQConfig;
import com.app.notificationservice.dto.TemplateEvent;
import com.app.notificationservice.dto.UserRoleEvent;
import com.app.notificationservice.entity.Notification;
import com.app.notificationservice.enums.NotificationType;
import com.app.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.USER_ROLE_UPGRADE_QUEUE)
    public void consumeUserRoleUpgrade(UserRoleEvent event) {
        log.info("Consumed user role upgrade event for user {}: {}", event.getUserId(), event.getNewRole());
        notificationService.saveUserUpgradeNotification(event.getUserId(), event.getUsername(), event.getNewRole());
    }

    @RabbitListener(queues = RabbitMQConfig.NEW_TEMPLATE_QUEUE)
    public void consumeNewTemplateAdded(TemplateEvent event) {
        log.info("Consumed new template added event: {}", event.getTemplateName());
        notificationService.saveNewTemplateNotification(event.getTemplateName());
    }
}


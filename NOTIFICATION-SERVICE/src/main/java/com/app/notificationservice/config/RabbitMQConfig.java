package com.app.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String NOTIFICATION_EXCHANGE = "notification_exchange";
    public static final String USER_ROLE_UPGRADE_QUEUE = "user_role_upgrade_queue";
    public static final String NEW_TEMPLATE_QUEUE = "new_template_queue";
    
    public static final String USER_ROLE_ROUTING_KEY = "user.role.upgrade";
    public static final String NEW_TEMPLATE_ROUTING_KEY = "template.new";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue userRoleUpgradeQueue() {
        return QueueBuilder.durable(USER_ROLE_UPGRADE_QUEUE).build();
    }

    @Bean
    public Queue newTemplateQueue() {
        return QueueBuilder.durable(NEW_TEMPLATE_QUEUE).build();
    }

    @Bean
    public Binding userRoleUpgradeBinding(Queue userRoleUpgradeQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(userRoleUpgradeQueue).to(notificationExchange).with(USER_ROLE_ROUTING_KEY);
    }

    @Bean
    public Binding newTemplateBinding(Queue newTemplateQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(newTemplateQueue).to(notificationExchange).with(NEW_TEMPLATE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}

package com.onion.backend.infrastructure.rabbitmq;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqSubscriber {


    @RabbitListener(queues = "onion-notification")
    public void receiveNotificationMessage(Message message) {
        System.out.println("Received message: " + message);
        String messageId = message.getMessageProperties().getMessageId();
        System.out.println("messageId = " + messageId);
    }
}

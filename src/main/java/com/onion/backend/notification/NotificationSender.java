package com.onion.backend.notification;


import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class NotificationSender {

    private final RabbitTemplate rabbitTemplate;

    public NotificationSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;

        this.rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("메시지 발행 실패: {}", cause);
                handlePublishingFailure(correlationData, cause);
            }
        });

        this.rabbitTemplate.setReturnsCallback(returned -> {
            log.error("메시지 반환됨: {}\n교환기: {}\n라우팅 키: {}\n응답 코드: {}\n응답 텍스트: {}",
                    returned.getMessage(),
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getReplyCode(),
                    returned.getReplyText());
            handleReturnedMessage(returned);
        });
    }

    public <T> void sendNotification(String exchange, String routingKey, T message) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        try {
            rabbitTemplate.convertAndSend(
                    exchange,
                    routingKey,
                    message,
                    correlationData
            );
        }catch (Exception e) {
            log.error("메세지 발행 중 예외 발생 : {}", e.getMessage());
        }
    }

    // TODO
    private void handlePublishingFailure(CorrelationData correlationData, String cause) {
        // 발행 실패 시 비즈니스 로직 처리
        // 예: 실패한 메시지 저장, 관리자 알림 등
    }

    private void handleReturnedMessage(ReturnedMessage returned) {
        // 반환된 메시지 처리
        // 예: 대체 큐로 전송, 실패 로그 저장 등
    }
}

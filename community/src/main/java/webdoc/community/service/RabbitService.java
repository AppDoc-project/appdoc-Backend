package webdoc.community.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/*
* Rabbit MQ 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitService {
    @Value("${rabbitmq.queue.name}")
    private String queueName;
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    private final RabbitTemplate rabbitTemplate;

    // 레슨 종료 사실을 소켓서버로 전달
    public void sendClose(Long lessonId, Long tutorId, Long tuteeId,String message) {
        this.rabbitTemplate.convertAndSend(exchangeName,routingKey,new Message(lessonId,tutorId,tuteeId,message,"close"));
    }

    // 레슨 오픈 사실을 소켓서버로 전달
    public void sendOpen(Long lessonId, Long tutorId, Long tuteeId){
        this.rabbitTemplate.convertAndSend(exchangeName,routingKey,new Message(lessonId,tutorId,tuteeId,null,"open"));
    }

    // rabbitMQ로 전달할 메세지 규격
    @RequiredArgsConstructor
    @Getter
    @Setter
    static class Message{
        private final Long LessonId;
        private final Long tutorId;
        private final Long tuteeId;

        private final String message;
        private final String type;
    }

}

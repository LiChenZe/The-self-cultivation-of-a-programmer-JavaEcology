package cn.itcast.mq.spring;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringAmqpTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage2SimpleQueue() throws InterruptedException {
        // 准备发送的消息
        String message = "hello, spring amqp!";
        // 准备RoutingKey
        String routingKey = "simple.test";
        // 准备CorrelationData
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        // 准备ConfirmCallback（这里可以用lambda表达式来表示，但是为了能看清参数这里就不用lambda的方式了）
        correlationData.getFuture().addCallback(new SuccessCallback<CorrelationData.Confirm>() {
            @Override
            public void onSuccess(CorrelationData.Confirm confirm) {
                // 判断结果
                if (confirm.isAck()) {
                    // ACK
                    log.debug("消息投递到交换机成功！消息ID：{}", correlationData.getId());
                } else {
                    // NACK
                    log.error("消息投递到交换机失败！消息ID：{}", correlationData.getId());
                }
            }
        }, new FailureCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                // 记录日志
                log.error("消息投递到交换机成功，但是路由到队列失败", throwable);
                // 重发消息

            }
        });

        // 发送消息
        rabbitTemplate.convertAndSend("amq.topic", routingKey, message, correlationData);

    }

    /**
     * 发送持久化的消息
     */
    @Test
    public void testDurableMessage() {
        // 准备消息
        Message message = MessageBuilder
                .withBody("hello, exchange and queue for durable".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
        // 发送消息（注意这里直接发送到队列中，不通过交换机了，两个参数为队列名 和 发布的消息）
        rabbitTemplate.convertAndSend("simple.queue",message);
    }

}

package com.junlaninfo.comsuner;

import com.junlaninfo.constants.MailConstants;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import com.rabbitmq.client.Channel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by 辉 on 2020/9/5.
 * https://www.jianshu.com/p/2c5eebfd0e95
 * https://zhuanlan.zhihu.com/p/102472438
 */
@RestController
public class msgCon {
    @Autowired
    StringRedisTemplate redisTemplate;

    @RabbitListener(queues = MailConstants.MAIL_QUEUE_NAME)
    public void msgCon(Message message, Channel channel) throws IOException {
        Object payload = message.getPayload();  //获取到消息中的内容
        MessageHeaders headers = message.getHeaders();//获得消息中的头
        //获得这条消息的tag标记
        Long tag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        //获得这条消息的msgId
        String msgId = (String) headers.get("spring_returned_message_correlation");
        String msgIdRedis = redisTemplate.opsForValue().get("msgId");
        if (msgId.equals(msgIdRedis)){
            System.out.println("消息已经被消费过了");
            channel.basicAck(tag, false);//确认消息已消费
            return;
        }
        System.out.println("当前消息id:"+msgId+"消息内容："+payload);//模拟消费消息
        redisTemplate.opsForValue().set("msgId",msgId);//把消息的id存入到redis中
        channel.basicAck(tag,false);
    }
}

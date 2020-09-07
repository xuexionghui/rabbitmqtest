package com.junlaninfo.config;


import com.junlaninfo.constants.MailConstants;
import com.rabbitmq.client.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;



/**
 * Created by 辉 on 2020/9/5.
 */
@Component
@Configuration
public class RabbitConfig{
    // implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback
    public final static Logger log= LoggerFactory.getLogger(RabbitConfig.class);
    @Autowired
    CachingConnectionFactory cachingConnectionFactory;

    /*

     */
    @Bean
    RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        rabbitTemplate.setConfirmCallback((data, ack, cause) -> {
            String msgId = data.getId();
            if (ack) {
                log.info(msgId + ":消息发送成功");

            } else {
                log.info(msgId + ":消息发送失败");
            }
        });
        rabbitTemplate.setReturnCallback((msg, repCode, repText, exchange, routingkey) -> {
            log.info("消息发送失败");
        });
        return rabbitTemplate;

    }

    /*
    correlationData ：confirm的数据
    boolean b:        true:已经发送到队列中 false：未发送到队列中
    String cause：    投递失败的原因

     */
//    @Override
//    public void confirm(CorrelationData correlationData, boolean b, String s) {
//        String id = correlationData.getId();//这个是消息的唯一id  全局id
//        log.info("全局消息id：" + id);
//        if (b) {
//            log.info("投递消息到队列中");
//        } else {
//            log.info("投递消息失败");
//        }
//
//    }
//
//    @Override
//    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
//        log.info("投递消息的返回信息-------------------------------------：开始");
//        log.info("投递消息的返回信息：" + message);
//        log.info("投递消息的返回信息：" + i);
//        log.info("投递消息的返回信息：" + s);
//        log.info("投递消息的返回信息：" + s1);
//        log.info("投递消息的返回信息：" + s2);
//        log.info("投递消息的返回信息-------------------------------------：结束");
//    }
    @Bean
    Queue mailQueue() {
        //true  持久化
        return new Queue(MailConstants.MAIL_QUEUE_NAME, true);
    }

    /*
     * true  持久化
     * false： 否自动删除
     */
    @Bean
    DirectExchange mailExchange() {
        return new DirectExchange(MailConstants.MAIL_EXCHANGE_NAME, true, false);
    }

    /*
     * 绑定交换机和队列
     */
    @Bean
    Binding mailBinding() {
        return BindingBuilder.bind(mailQueue()).to(mailExchange()).with(MailConstants.MAIL_ROUTING_KEY_NAME);
    }



}

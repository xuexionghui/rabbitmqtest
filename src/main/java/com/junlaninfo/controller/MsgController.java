package com.junlaninfo.controller;

import com.junlaninfo.constants.MailConstants;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by 辉 on 2020/9/5.
 * 发送消息
 */
@RestController
public class MsgController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMsg")
    public String sendMsg() {
        UUID msgId = UUID.randomUUID();

        rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME, MailConstants.MAIL_ROUTING_KEY_NAME, "消息内容：1111", new CorrelationData(msgId.toString()));
        return "消息发送成功";
    }
}

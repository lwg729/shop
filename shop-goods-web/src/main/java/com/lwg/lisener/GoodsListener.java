package com.lwg.lisener;

import com.lwg.service.impl.GoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/28 16:18
 */

@Component
public class GoodsListener {

    @Autowired
    private GoodsHtmlService goodsHtmlService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "lware.create.web.queue", durable = "true"),
            exchange = @Exchange(
                    value = "lware.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}))
    public void listenCreate(Long id) throws Exception {
        if (id == null) {
            return;
        }
        // 创建页面
        goodsHtmlService.createHtml(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "lware.delete.web.queue", durable = "true"),
            exchange = @Exchange(
                    value = "lware.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = "item.delete"))
    public void listenDelete(Long id) {
        if (id == null) {
            return;
        }
        // 删除页面
        goodsHtmlService.deleteHtml(id);
    }
}

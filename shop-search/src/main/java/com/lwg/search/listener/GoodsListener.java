package com.lwg.search.listener;

import com.lwg.search.client.GoodsClient;
import com.lwg.search.service.impl.SearchServiceImpl;
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
 * @Date: 2021/2/28 15:57
 */

@Component
public class GoodsListener {

    @Autowired
    private SearchServiceImpl searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "lware.item.index.queue",durable = "true"),
            exchange = @Exchange(value = "lware.item.exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}))
    public void lisenerCreate(Long id) throws Exception{
        if (id==null){
            return;
        }
        searchService.createIndex(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "lware.item.index.queue",durable = "true"),
            exchange = @Exchange(value = "lware.item.exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.delete"}))
    public void lisenerDelete(Long id) {
        if (id==null){
            return;
        }
        searchService.deleteIndex(id);
    }
}

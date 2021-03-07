package com.lwg.order.service.api;

import com.lwg.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "shop-gateway", path = "/api/item")
public interface GoodsService extends GoodsApi {
}

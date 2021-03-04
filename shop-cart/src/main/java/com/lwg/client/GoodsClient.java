package com.lwg.client;

import com.lwg.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/3/5 0:02
 */

@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}

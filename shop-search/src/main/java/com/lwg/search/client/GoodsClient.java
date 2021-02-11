package com.lwg.search.client;

import com.lwg.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/9 21:28
 */

@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {

}

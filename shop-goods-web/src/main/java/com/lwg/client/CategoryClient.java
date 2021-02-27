package com.lwg.client;

import com.lwg.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/9 21:54
 */
@FeignClient(value = "item-service")
public interface CategoryClient extends CategoryApi {
}

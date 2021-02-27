package com.lwg.client;

import com.lwg.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/9 21:57
 */
@FeignClient(value = "item-service")
public interface BrandClient extends BrandApi {
}

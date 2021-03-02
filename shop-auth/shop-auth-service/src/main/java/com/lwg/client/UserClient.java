package com.lwg.client;

import com.lwg.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/3/2 22:27
 */
@FeignClient(value = "user-service")
public interface UserClient extends UserApi {
}

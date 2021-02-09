package com.lwg.search.client;


import com.lwg.search.ShopSearchApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * 功能描述：
 *
 * @Author: lwg
 * @Date: 2021/2/9 22:04
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopSearchApplication.class)
public class CategoryClientTest {
    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void testQueryCategories(){
        List<String> names = categoryClient.queryNamesByIds(Arrays.asList(1L, 2L, 3L));
        names.forEach(System.out::println);
    }
}
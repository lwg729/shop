package com.lwg.controller;

import com.lwg.service.impl.GoodsHtmlService;
import com.lwg.service.impl.GoodsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 功能描述：返回商品详情页
 *
 * @Author: lwg
 * @Date: 2021/2/27 16:33
 */

@Controller
public class GoodsController {

    @Autowired
    private GoodsServiceImpl goodsService;

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @GetMapping("item/{id}.html")
    public String toItemPage(Model model, @PathVariable("id")Long id){
        // 加载所需的数据
        Map<String, Object> modelMap = this.goodsService.loadData(id);
        // 放入模型
        model.addAllAttributes(modelMap);

        goodsHtmlService.asyncExcute(id);
        //页面静态化
        return "item";
    }
}

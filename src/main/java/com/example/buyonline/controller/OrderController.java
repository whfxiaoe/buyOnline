package com.example.buyonline.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.buyonline.entity.Order;
import com.example.buyonline.entity.Person;
import com.example.buyonline.repository.OrderRepository;
import com.example.buyonline.repository.PersonRepository;
import com.example.buyonline.util.MKOResponse;
import com.example.buyonline.util.MKOResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("order")
public class OrderController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(BaseController.class.getName());

    @Autowired
    EntityManager entityManager;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PersonRepository personRepository;

    /**
     * @Description: 客户订单详情(管理)
     * @Param: id(Integer)
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/18
     */
    @GetMapping("info")
    public MKOResponse info(@RequestParam Integer userId) {
        try {
            Person person = personRepository.chooseById(userId);
            if (person == null) {
                return makeResponse(MKOResponseCode.DataNotFound, "", "此id用户不存在");
            }
            Order order = orderRepository.chooseById(userId);
            if (order == null) {
                return makeResponse(MKOResponseCode.DataNotFound, "", "此id无订单");
            }
            String[] arr = order.getOrderInfo().split("~");
            Map<String,Object> result = new HashMap<>();
            List<Map<String,Object>> ls = new ArrayList<>();
            for(int i = 0 ;i < arr.length;i++){
                JSONObject rs = JSONObject.parseObject(arr[i]);
                ls.add(rs);
            }
            result.put("orderId",order.getOrderId());
            result.put("id",order.getUserId());
            result.put("orderValue",order.getOrderValue());
            result.put("orderCreate",order.getOrderCreate());
            result.put("orderState",order.getState());
            result.put("data",ls);
            return makeSuccessResponse(result);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误！");
        }
    }


}

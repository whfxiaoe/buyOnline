package com.example.buyonline.controller;

import com.example.buyonline.entity.Cart;
import com.example.buyonline.entity.Goods;
import com.example.buyonline.entity.Order;
import com.example.buyonline.entity.Person;
import com.example.buyonline.repository.CartRepository;
import com.example.buyonline.repository.GoodsRepository;
import com.example.buyonline.repository.OrderRepository;
import com.example.buyonline.repository.PersonRepository;
import com.example.buyonline.util.MKOResponse;
import com.example.buyonline.util.MKOResponseCode;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("goods")
public class GoodsController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(BaseController.class.getName());

    @Autowired
    EntityManager entityManager;
    @Autowired
    GoodsRepository goodsRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    OrderRepository orderRepository;

    /**
     * @Description: 商品详情
     * @Param: id(Integer)
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/18
     */
    @GetMapping("goodsInfo")
    public MKOResponse goodsInfo(@RequestParam Integer id) {
        try {
            Goods goods = goodsRepository.chooseById(id);
            if (goods == null) {
                return makeResponse(MKOResponseCode.DataNotFound, "", "查不到信息");
            }
//            Map<String,Object> result = new HashMap<>();
//            result.put("goodsId",goods.getGoodsId());
//            result.put("goodsName",goods.getGoodsName());
//            result.put("goodsPrice",goods.getGoodsPrice());
//            result.put("goodsTotal",goods.getGoodsTotal());
//            result.put("goodsCreate",goods.getGoodsCreate());
//            result.put("goodsUpdate",goods.getGoodsUpdate());
//            result.put("goodsState",goods.getGoodsState());
//            return makeSuccessResponse(result);
            return makeSuccessResponse(goods);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误！");
        }
    }

    /**
     * @Description: 商品列表
     * @Param:  goodsNameTel(String)  priceTop(Double)  priceBottom(Double)   page(Integer)   count(Integer)
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/18
     */
    @GetMapping("goodsList")
    public MKOResponse goodsList(@RequestParam(defaultValue = "") String goodsName,
                                 @RequestParam(defaultValue = "-1") Double priceTop,
                                 @RequestParam(defaultValue = "-1") Double priceBottom,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer count) {
        try {
            String scount = "SELECT COUNT(*) FROM goods WHERE goods_state = 1 ";
            String sel = "SELECT * FROM goods WHERE goods_state = 1 ";

            if (goodsName.length() != 0) {
                sel += "AND name LIKE '%" + goodsName + "%' ";
                scount += "AND name LIKE '%" + goodsName + "%' ";
            }
            if(priceTop != -1){
                sel += "AND goods_price >= "+ priceTop +" ";
                scount += "AND goods_price >= "+ priceTop +" ";
            }
            if(priceBottom != -1){
                sel += "AND goods_price <= "+ priceBottom +" ";
                scount += "AND goods_price <= "+ priceBottom +" ";
            }

            sel += "LIMIT "+(page-1)*count +"," + count;
            Query queryC = entityManager.createNativeQuery(sel);
            Query queryX = entityManager.createNativeQuery(scount);

            @SuppressWarnings("unchecked")
            Map<String, BigInteger> si = (Map<String,BigInteger>)((SQLQuery)queryX.unwrap(SQLQuery.class)).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getSingleResult();
            List<Map<String, Object>> list = ((SQLQuery)queryC.unwrap(SQLQuery.class)).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

            Map<String,Object> result = new HashMap<>();
            //当前页数
            result.put("page",page);
            //总页数
            result.put("allPage",(si.get("COUNT(*)").intValue()-1)/count+1);
            //每页数量
            result.put("count",count);
            //总数量
            result.put("countNum",si.get("COUNT(*)"));
            //数据
            result.put("data",list);
            return makeSuccessResponse(result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误");
        }
    }

    /**
     * @Description: 添加商品
     * @Param:
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/18
     */
    @PostMapping("add")
    public MKOResponse add(@RequestBody Goods goodsData) {
        try {
            if(goodsData.getGoodsName() == null && goodsData.getGoodsName().length() == 0){
                return makeResponse(MKOResponseCode.ParamsLack,"","缺少参数[goodsName]");
            }
            if(goodsData.getGoodsPrice() == null && goodsData.getGoodsPrice() < 0){
                return makeResponse(MKOResponseCode.ParamsLack,"","缺少参数[goodsPrice]");
            }
            Goods goods = new Goods();
            goods.setGoodsName(goodsData.getGoodsName());
            goods.setGoodsPrice(goodsData.getGoodsPrice());
            goods.setGoodsTotal(goodsData.getGoodsTotal());
            goods.setGoodsState(goods.getGoodsState());
            goods.setGoodsCreate(new Date());
            goods.setGoodsUpdate(new Date());
            goodsRepository.saveAndFlush(goods);

            return makeSuccessResponse("商品添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误");
        }
    }

    /**
     * @Description: 修改商品信息
     * @Param:
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/18
     */
    @PostMapping("update")
    public MKOResponse update(@RequestBody Goods goodsData) {
        try {
            if(goodsData.getGoodsId() == null){
                return makeResponse(MKOResponseCode.ParamsLack,"","缺少参数[id]");
            }
            Goods goods = goodsRepository.chooseById(goodsData.getGoodsId());
            if(goods == null){
                return makeResponse(MKOResponseCode.DataNotFound,"","此[id]无数据");
            }
            goods.setGoodsName(goodsData.getGoodsName() == null? goods.getGoodsName() : goodsData.getGoodsName());
            goods.setGoodsPrice(goodsData.getGoodsPrice() == null? goods.getGoodsPrice() : goodsData.getGoodsPrice());
            goods.setGoodsTotal(goodsData.getGoodsTotal() == null? goods.getGoodsTotal() : goodsData.getGoodsTotal());
            goods.setGoodsState(goodsData.getGoodsState() == null? goodsData.getGoodsState(): goods.getGoodsState());
            goods.setGoodsUpdate(new Date());
            goodsRepository.saveAndFlush(goods);

            return makeSuccessResponse("商品修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误");
        }
    }

    /**
     * @Description: 删除商品
     * @Param:  goodsId
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/18
     */
    @GetMapping("delete")
    public MKOResponse delete(@RequestParam Integer goodsId) {
        try {
            Goods goods = goodsRepository.chooseById(goodsId);
            if (goods == null) {
                return makeResponse(MKOResponseCode.DataNotFound, "", "查无数据无需删除");
            }
            goodsRepository.delete(goods);
            return makeSuccessResponse("成功删除");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误");
        }
    }

    /**
     * @Description: 切换商品状态(1:在售;0:下架)
     * @Param:  state(Integer)      goodsId
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/18
     */
    @GetMapping("switchState")
    MKOResponse switchState(@RequestParam Integer goodsState,
                            @RequestParam Integer goodsId){
        try {
            Goods goods = goodsRepository.chooseById(goodsId);
            if(goods == null){
                return makeResponse(MKOResponseCode.DataNotFound,"","此[id]无数据");
            }
            goods.setGoodsState(goodsState);
            goods.setGoodsUpdate(new Date());
            goodsRepository.saveAndFlush(goods);
            return makeSuccessResponse("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误");
        }
    }

    /**
     * @Description: 购物车的添加(客户)
     * @Param: id(Integer)
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/19
     */
    @GetMapping("addCart")
    public MKOResponse addCart(@RequestParam Integer userId,
                               @RequestParam Integer goodsId,
                               @RequestParam Integer goodsCount) {
        try {
            Person person = personRepository.chooseById(userId);
            if (person == null) {
                return makeResponse(MKOResponseCode.DataNotFound, "", "此id用户不存在");
            }
            Goods goods = goodsRepository.chooseById(goodsId);
            if (goods == null) {
                return makeResponse(MKOResponseCode.DataNotFound, "", "此id无商品");
            }
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setGoodsId(goodsId);
            cart.setGoodsCount(goodsCount);
            cart.setCartCreate(new Date());
            cart.setCartState(1);
            cartRepository.saveAndFlush(cart);
            return makeSuccessResponse("添加购物车成功");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误！");
        }
    }

    /**
     * @Description: 清空购物车(真删)
     * @Param:  id
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/19
     */
    @GetMapping("clearCart")
    public MKOResponse clearCart(@RequestParam Integer userId) {
        try {
            int i = cartRepository.delById(userId);
//            if (cart == null) {
//                return makeResponse(MKOResponseCode.DataNotFound, "", "查无数据无需删除");
//            }q
//            cartRepository.delete(cart);
            return makeSuccessResponse("已清空用户id="+userId+"的"+i+"条数据");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误");
        }
    }

    /**
     * @Description: 单件购买
     * @Param:  id(客户)      goodsId     goodsCount
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/19
     */
    @GetMapping("orderCreate")
    public MKOResponse orderCreate(@RequestParam Integer userId,
                                   @RequestParam Integer goodsId,
                                   @RequestParam Integer goodsCount) {
        try {
            Person person = personRepository.chooseById(userId);
            if (person == null) {
                return makeResponse(MKOResponseCode.DataNotFound, "", "此id用户不存在");
            }
            Goods goods = goodsRepository.chooseById(userId);
            if(goods == null){
                return makeResponse(MKOResponseCode.DataNotFound, "", "此id无商品");
            }
            Order order = new Order();
            order.setUserId(userId);
            order.setOrderInfo("["+goods.getGoodsName()+","+goodsCount+","+goods.getGoodsPrice()+"]");
            order.setOrderValue(goods.getGoodsPrice()*goodsCount);
            order.setOrderCreate(new Date());
            order.setOrderUpdate(new Date());
            order.setState(0);
   //         return makeSuccessResponse(order);
            orderRepository.saveAndFlush(order);
            return makeSuccessResponse("购买单件成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误");
        }
    }

    /**
     * @Description: 购买购物车所有商品
     * @Param:  id(客户)
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/19
     */
    @GetMapping("cartCreate")
    public MKOResponse cartCreate(@RequestParam Integer userId) {
        try {

            String sel = "SELECT goods.goods_name,shopping_cart.goods_count,goods.goods_price FROM goods,shopping_cart WHERE shopping_cart.id = "+ userId;
            Query queryC = entityManager.createNativeQuery(sel);
            List<Map<String, Object>> lm = ((SQLQuery)queryC.unwrap(SQLQuery.class)).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

            Order order = new Order();
            String[] arr = createOrder(lm);                     //调用订单生成方法

            order.setUserId(userId);
            order.setOrderInfo(arr[0]);
            order.setOrderValue(Double.parseDouble(arr[1]));
            order.setOrderCreate(new Date());
            order.setOrderUpdate(new Date());
            order.setState(0);
            orderRepository.saveAndFlush(order);
            return makeSuccessResponse("购物车订单已成功生成");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误");
        }
    }

    /**
     *  @Description: 生成订单+ 计算总价
     *  @Param:   lm(List<Map<String,Object>>)
     *  @Author: xiaoe
     *  @Date: 2019/03/19
     */
    public String[] createOrder(List<Map<String,Object>> lm){

        String[] arr = new String[2];
        String json = "";
        double xiaoe = 0;
        double totalPrice = 0;
        for(Map<String,Object> data : lm){

            xiaoe = Double.valueOf(data.get("goods_price").toString());
            DecimalFormat df = new DecimalFormat("#.00");           //保留2位小数，填写#.00
            String temp = df.format(xiaoe);
            totalPrice += Double.valueOf(temp) * Double.parseDouble(data.get("goods_count").toString());
            json += data.toString() + "~";

        }
        arr[0] = json;
        arr[1] = String.valueOf(totalPrice);
        return arr;
    }
}

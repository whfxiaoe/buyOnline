package com.example.buyonline.controller;

import com.example.buyonline.entity.Person;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("person")
public class PersonController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(BaseController.class.getName());
    @Autowired
    EntityManager entityManager;
    @Autowired
    PersonRepository personRepository;

    /**
     * @Description: 登录
     * @Param: tel(String)     password(String)
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/18
     */
    @GetMapping("login")
    public MKOResponse login(@RequestParam String tel, @RequestParam String password) {
        try {
            Person person = personRepository.chooseTP(tel, password);
            if(person == null) {
                return makeResponse(MKOResponseCode.DataNotFound, "", "用户名或密码错误");
            }
            if(person.getState() == 0) {
                return makeResponse(MKOResponseCode.DataNotFound, "", "该用户已禁用");
            }
            Map<String,Object> result = new HashMap<>();
            result.put("id",person.getUserId());
            result.put("tel",person.getTel());
            result.put("name",person.getName());
            result.put("role",person.getRole());
//            result.put("sex",person.getSex());
//            result.put("age",person.getAge());
            result.put("state",person.getState());
            result.put("gmtCreate",person.getGmtCreate());
            return makeSuccessResponse(result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误！");
        }
    }

    /**
     * @Description: 客户列表
     * @Param:  nameTel(String)     state(Integer)      page(Integer)      count(Integer)
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/18
     */
    @GetMapping("customerList")
    public MKOResponse list(@RequestParam(defaultValue = "3") Integer state,
                            @RequestParam(defaultValue = "") String nameTel,
                            @RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "10") Integer count) {
        try {
            String scount = "SELECT COUNT(*) FROM person WHERE 1 = 1 ";
            String sel = "SELECT id,name,tel,state,role,gmt_create FROM person WHERE 1 = 1 ";
            if (state != 3) {
                sel += "AND state = " + state + " ";
                scount += "AND state = " + state + " ";
            }

            if (nameTel.length() != 0) {
                sel += "AND (name LIKE '%" + nameTel + "%' OR tel LIKE '%" + nameTel + "%') ";
                scount += "AND (name LIKE '%" + nameTel + "%' OR tel LIKE '%" + nameTel + "%') ";
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
     * @Description: 客户详情
     * @Param: id(Integer)
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/18
     */
    @GetMapping("customerInfo")
    public MKOResponse customerInfo(@RequestParam Integer userId) {
        try {
            Person person = personRepository.chooseById(userId);
            if (person == null) {
                return makeResponse(MKOResponseCode.DataNotFound, "", "查不到信息");
            }
            Map<String,Object> result = new HashMap<>();
            result.put("id",person.getUserId());
            result.put("tel",person.getTel());
            result.put("name",person.getName());
            result.put("role",person.getRole());
//            result.put("sex",person.getSex());
//            result.put("age",person.getAge());
            result.put("state",person.getState());
            result.put("gmtCreate",person.getGmtCreate());
            return makeSuccessResponse(result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误！");
        }
    }

    /**
     * @Description: 删除客户(真删)
     * @Param:  id
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/18
     */
    @GetMapping("delete")
    public MKOResponse delete(@RequestParam Integer userId) {
        try {
            Person person = personRepository.chooseById(userId);
            if (person == null) {
                return makeResponse(MKOResponseCode.DataNotFound, "", "查无数据无需删除");
            }
            personRepository.delete(person);
            return makeSuccessResponse("已删除");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误");
        }
    }

    /**
     * @Description: 切换客户启用禁用状态
     * @Param:  state(Integer)      id
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/18
     */
    @GetMapping("switchState")
    MKOResponse switchState(@RequestParam Integer state,
                            @RequestParam Integer userId){
        try {
            Person person = personRepository.chooseById(userId);
            if(person == null){
                return makeResponse(MKOResponseCode.DataNotFound,"","此[id]无数据");
            }
            person.setState(state);
            person.setGmtCreate(new Date());
            personRepository.saveAndFlush(person);
            return makeSuccessResponse("已切换");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误");
        }
    }

    /**
     * @Description: 用户注册
     * @Param:
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/18
     */
    @PostMapping("join")
    public MKOResponse join(@RequestBody Person personData) {
        try {
            if(personData.getTel() == null || personData.getTel().length() != 11){
                return makeResponse(MKOResponseCode.DataFormatError,"","手机号格式不正确");
            }
            if(personRepository.validateTel(personData.getTel()) != null)
            {
                return makeResponse(MKOResponseCode.DataExist,"","手机号已存在");
            }
            if(personData.getTel() == null && personData.getTel().length() == 0){
                return makeResponse(MKOResponseCode.ParamsLack,"","缺少参数[tel]");
            }
            if(personData.getPassword() == null && personData.getPassword().length() == 0){
                return makeResponse(MKOResponseCode.ParamsLack,"","缺少参数[password]");
            }
            Person person = new Person();
            person.setName(personData.getName());
            person.setTel(personData.getTel());
            person.setPassword(personData.getPassword());
            person.setRole(personData.getRole() == null? 0: personData.getRole());
            person.setState(1);
            person.setGmtCreate(new Date());
            personRepository.saveAndFlush(person);
            return makeSuccessResponse("已添加");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误");
        }
    }

    /**
     * @Description: 客户修改信息
     * @Param:
     * @return:
     * @Author: xiaoe
     * @Date: 2019/03/18
     */
    @PostMapping("update")
    public MKOResponse update(@RequestBody Person personData) {
        try {
            if(personData.getUserId() == null){
                return makeResponse(MKOResponseCode.ParamsLack,"","缺少参数[id]");
            }

            Person person = personRepository.chooseById(personData.getUserId());
            if(person == null){
                return makeResponse(MKOResponseCode.DataNotFound,"","此[id]无数据");
            }

            person.setName(personData.getName() == null? person.getName() : personData.getName());
//            person.setAge(personData.getAge() == null? person.getAge() : personData.getAge());
            person.setPassword(personData.getPassword() == null? person.getPassword() : personData.getPassword());
//            person.setSex(personData.getSex() == null? 1: personData.getSex());
            person.setRole(personData.getRole() == null? 0: personData.getRole());
            person.setState(personData.getState() == null? 1:personData.getState());
            person.setGmtCreate(new Date());
            personRepository.saveAndFlush(person);
            return makeSuccessResponse("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return makeBussessErrorResponse("未知错误");
        }
    }
}

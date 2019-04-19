package com.ontology.schedulers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ontology.dao.Order;
import com.ontology.mapper.OrderMapper;
import com.ontology.secure.SecureConfig;
import com.ontology.utils.Helper;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


@Component
@Slf4j
@EnableScheduling
public class OrderScheduler extends BaseScheduler {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SDKUtil sdk;
    @Autowired
    private SecureConfig secureConfig;


    /**
     * 买家购买的链上信息
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 1000)
    public void boughtSchedule() {
        // 操作类型:bought;buyerCancel;delivered;sellerRecvToken;buyerRecvMsg
        log.info("bought schedule : {}", Thread.currentThread().getName());
        Example example = new Example(Order.class);
//        example.setOrderByClause("create_time asc");
        example.createCriteria().andEqualTo("state", "bought").andLessThanOrEqualTo("checkTime",new Date());

        RowBounds rowBounds = new RowBounds(0, 100);
        List<Order> orders = orderMapper.selectByExampleAndRowBounds(example, rowBounds);

        for (Order order : orders) {
            try {
                Object event = sdk.checkEvent(order.getBuyTx());
                if (Helper.isEmptyOrNull(event)) {
                    // 未找到链上信息,过几轮再查
                    order.setCheckTime(new Date(new Date().getTime()+30*1000));
                } else {
                    String eventStr = JSON.toJSONString(event);
                    String exchangeId = null;
                    JSONObject jsonObject = JSONObject.parseObject(eventStr);
                    JSONArray notify = jsonObject.getJSONArray("Notify");
                    for (int j = 0;j<notify.size();j++) {
                        JSONObject obj = notify.getJSONObject(j);
                        if (secureConfig.getContractHash().equals(obj.getString("ContractAddress"))) {
                            exchangeId = obj.getJSONArray("States").getString(1);
                        }
                    }
                    order.setBuyEvent(eventStr);
                    order.setExchangeId(exchangeId);
                    order.setState("boughtOnchain");
                    order.setBuyDate(new Date());
                    order.setCheckTime(new Date());
                }
                orderMapper.updateByPrimaryKeySelective(order);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 买家取消的链上信息
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 1000)
    public void cancelSchedule() {
        // 操作类型:bought;buyerCancel;delivered;sellerRecvToken;buyerRecvMsg
        log.info("cancel schedule : {}", Thread.currentThread().getName());
        Example example = new Example(Order.class);
//        example.setOrderByClause("create_time asc");
        example.createCriteria().andEqualTo("state", "buyerCancel").andLessThanOrEqualTo("checkTime",new Date());

        RowBounds rowBounds = new RowBounds(0, 100);
        List<Order> orders = orderMapper.selectByExampleAndRowBounds(example, rowBounds);
        for (Order order : orders) {
            try {
                Object event = sdk.checkEvent(order.getCancelTx());
                if (Helper.isEmptyOrNull(event)) {
                    // 未找到链上信息,过几轮再查
                    order.setCheckTime(new Date(new Date().getTime()+30*1000));
                } else {
                    order.setCancelEvent(JSON.toJSONString(event));
                    order.setState("buyerCancelOnchain");
                    order.setCancelDate(new Date());
                    order.setCheckTime(new Date());
                }
                orderMapper.updateByPrimaryKeySelective(order);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 买家取货的链上信息
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 1000)
    public void recvMsgSchedule() {
        // 操作类型:bought;buyerCancel;delivered;sellerRecvToken;buyerRecvMsg
        log.info("recvMsg schedule : {}", Thread.currentThread().getName());
        Example example = new Example(Order.class);
//        example.setOrderByClause("create_time asc");
        example.createCriteria().andEqualTo("state", "buyerRecvMsg").andLessThanOrEqualTo("checkTime",new Date());

        RowBounds rowBounds = new RowBounds(0, 100);
        List<Order> orders = orderMapper.selectByExampleAndRowBounds(example, rowBounds);
        for (Order order : orders) {
            try {
                Object event = sdk.checkEvent(order.getRecvMsgTx());
                if (Helper.isEmptyOrNull(event)) {
                    // 未找到链上信息,过几轮再查
                    order.setCheckTime(new Date(new Date().getTime()+30*1000));
                } else {
                    order.setRecvMsgEvent(JSON.toJSONString(event));
                    order.setState("buyerRecvMsgOnchain");
                    order.setRecvMsgDate(new Date());
                    order.setCheckTime(new Date());
                }
                orderMapper.updateByPrimaryKeySelective(order);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 卖家发货的链上信息
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 1000)
    public void deliveredSchedule() {
        // 操作类型:bought;buyerCancel;delivered;sellerRecvToken;buyerRecvMsg
        log.info("delivered schedule : {}", Thread.currentThread().getName());
        Example example = new Example(Order.class);
//        example.setOrderByClause("create_time asc");
        example.createCriteria().andEqualTo("state", "delivered").andLessThanOrEqualTo("checkTime",new Date());

        RowBounds rowBounds = new RowBounds(0, 100);
        List<Order> orders = orderMapper.selectByExampleAndRowBounds(example, rowBounds);
        for (Order order : orders) {
            try {
                Object event = sdk.checkEvent(order.getSellTx());
                if (Helper.isEmptyOrNull(event)) {
                    // 未找到链上信息,过几轮再查
                    order.setCheckTime(new Date(new Date().getTime()+30*1000));
                } else {
                    order.setState("deliveredOnchain");
                    order.setSellEvent(JSON.toJSONString(event));
                    order.setSellDate(new Date());
                    order.setCheckTime(new Date());
                }
                orderMapper.updateByPrimaryKeySelective(order);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 卖家收取token的链上信息
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 1000)
    public void recvTokenSchedule() {
        // 操作类型:bought;buyerCancel;delivered;sellerRecvToken;buyerRecvMsg
        log.info("recvToken schedule : {}", Thread.currentThread().getName());
        Example example = new Example(Order.class);
//        example.setOrderByClause("create_time asc");
        example.createCriteria().andEqualTo("state", "sellerRecvToken").andLessThanOrEqualTo("checkTime",new Date());

        RowBounds rowBounds = new RowBounds(0, 100);
        List<Order> orders = orderMapper.selectByExampleAndRowBounds(example, rowBounds);
        for (Order order : orders) {
            try {
                Object event = sdk.checkEvent(order.getRecvTokenTx());
                if (Helper.isEmptyOrNull(event)) {
                    // 未找到链上信息,过几轮再查
                    order.setCheckTime(new Date(new Date().getTime()+30*1000));
                } else {
                    order.setState("sellerRecvTokenOnchain");
                    order.setRecvTokenEvent(JSON.toJSONString(event));
                    order.setRecvTokenDate(new Date());
                    order.setCheckTime(new Date());
                }
                orderMapper.updateByPrimaryKeySelective(order);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

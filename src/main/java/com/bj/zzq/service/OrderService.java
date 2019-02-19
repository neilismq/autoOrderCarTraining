package com.bj.zzq.service;

import com.bj.zzq.dao.OrderInfoDao;
import com.bj.zzq.dao.UserDao;
import com.bj.zzq.model.OrderInfoEntity;
import com.bj.zzq.model.OrderInfoEntityExample;
import com.bj.zzq.model.UserEntity;
import com.bj.zzq.model.UserEntityExample;
import com.bj.zzq.utils.StringUtils;
import com.sun.org.apache.xml.internal.resolver.readers.ExtendedXMLCatalogReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/31
 * @Description:
 */
@Service
public class OrderService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private OrderInfoDao orderInfoDao;

    public void insertUser(UserEntity userEntity) {
        userDao.insert(userEntity);
    }

    public List<UserEntity> selectAllUsers(String keyword) {
        UserEntityExample example = new UserEntityExample();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(keyword)) {
            example.createCriteria().andUsernameLike(StringUtils.likeStr(keyword)).andPasswordLike(StringUtils.likeStr(keyword)).andEmailLike(StringUtils.likeStr(keyword)).andCnbhLike(StringUtils.likeStr(keyword));
        }
        return userDao.selectExample(new UserEntityExample());
    }

    public List<UserEntity> selectUserByUsername(String username) {
        UserEntityExample example = new UserEntityExample();
        example.createCriteria().andUsernameEqualTo(username);
        return userDao.selectExample(example);
    }

    public void insertOrderInfo(OrderInfoEntity orderInfoEntity) {
        orderInfoDao.insert(orderInfoEntity);
    }

    public List<OrderInfoEntity> selectOrderInfoUnique(String userId, OrderInfoEntity orderInfoEntity) {
        OrderInfoEntityExample example = new OrderInfoEntityExample();
        example.createCriteria().andUserIdEqualTo(userId).andOrderDateEqualTo(orderInfoEntity.getOrderDate()).andTimeSlotEqualTo(orderInfoEntity.getTimeSlot());
        return orderInfoDao.selectExample(example);
    }

    public OrderInfoEntity selectOrderbyId(String id) {
        OrderInfoEntityExample example = new OrderInfoEntityExample();
        example.createCriteria().andIdEqualTo(id);
        List<OrderInfoEntity> list = orderInfoDao.selectExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public void deleteUserById(String id) {
        userDao.deleteById(id);
    }

    public void deleteOrderById(String id) {
        orderInfoDao.deleteById(id);
    }


    public void updateOrderStatusSuccess(OrderInfoEntity orderInfoEntity) {
        orderInfoDao.update(orderInfoEntity);
    }


    public List<OrderInfoEntity> selectOrderByUserId(String userId) {
        OrderInfoEntityExample example = new OrderInfoEntityExample();
        example.createCriteria().andUserIdEqualTo(userId);
        return orderInfoDao.selectExample(example);
    }

    public UserEntity selectUserByUserId(String id) {
        return userDao.selectByPrimaryKey(id);
    }

    public List<OrderInfoEntity> selectAllExecutableOrders() {
        OrderInfoEntityExample example = new OrderInfoEntityExample();
        example.createCriteria().andStatusEqualTo("0").andIsStopEqualTo("0").andOrderDateGreaterThan(new Date());
        return orderInfoDao.selectExample(example);
    }

    public void updateOrder(OrderInfoEntity orderInfoEntity) {
        orderInfoDao.update(orderInfoEntity);
    }
}

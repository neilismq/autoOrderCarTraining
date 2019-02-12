package com.bj.zzq.dao;

import com.bj.zzq.core.OrderInfo;
import com.bj.zzq.core.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private JdbcTemplate jdbcTemplate;

    public void insertUser(UserInfo userInfo) {
        String sql = "insert into car_user(id,username,password,email,create_time) values (?,?,?,?,?)";
        jdbcTemplate.update(sql, userInfo.getId(), userInfo.getUsername(), userInfo.getPassword(), userInfo.getEmail(), new Date());
    }

    public List<UserInfo> selectAllUsers() {
        String sql = "select id,username,password,email,cnbh,create_time from car_user ";
        BeanPropertyRowMapper<UserInfo> instance = BeanPropertyRowMapper.newInstance(UserInfo.class);
        List<UserInfo> userInfos = jdbcTemplate.query(sql, instance);
        //List<UserInfo> userInfos = jdbcTemplate.queryForList(sql, UserInfo.class);
        return userInfos;
    }

    public List<UserInfo> selectUserByUsername(String username) {
        String sql = "select * from car_user where username=?";
        BeanPropertyRowMapper<UserInfo> rowMapper = BeanPropertyRowMapper.newInstance(UserInfo.class);
        List<UserInfo> userInfo = jdbcTemplate.query(sql, new Object[]{username}, rowMapper);
        //UserInfo userInfo = jdbcTemplate.queryForObject(sql, UserInfo.class, username);
        return userInfo;
    }

    public void insertOrderInfo(OrderInfo orderInfo) {
        String sql = "insert car_orderinfo (id,user_id,order_date,time_slot,create_time,status) values (?,?,?,?,?,?)";
        jdbcTemplate.update(sql, orderInfo.getId(), orderInfo.getUser_id(), orderInfo.getOrderDate(), orderInfo.getOrderType(), orderInfo.getCreate_time(), orderInfo.getStatus());
    }

    public List<OrderInfo> selectOrderInfoUnique(OrderInfo orderInfo) {
        String sql = "select * from car_orderinfo where user_id =? and order_date=? and time_slot=?";
        BeanPropertyRowMapper<OrderInfo> rowMapper = BeanPropertyRowMapper.newInstance(OrderInfo.class);
        return jdbcTemplate.query(sql, new Object[]{orderInfo.getUser_id(), orderInfo.getOrderDate(), orderInfo.getOrderType()}, rowMapper);
    }

    public OrderInfo selectOrderbyId(String id) {
        String sql = "select * from car_orderinfo where id=?";
        return jdbcTemplate.queryForObject(sql, OrderInfo.class, id);
    }

    public void deleteUserById(String id) {
        String sql = "delete from car_user where id=?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteOrderById(String id) {
        String sql = "delete from car_orderinfo where id=?";
        jdbcTemplate.update(sql, id);
    }

    public void updateOrderStatusSuccess(OrderInfo orderInfo) {
        String sql = "update car_orderinfo set status=? where id=?";
        jdbcTemplate.update(sql, "1", orderInfo.getId());
    }


    public List<OrderInfo> selectOrderByUserId(String user_id) {
        String sql = "select * from car_orderinfo where user_id=?";
        return jdbcTemplate.queryForList(sql, OrderInfo.class, user_id);
    }

    public List<UserInfo> selectUserByUserId(OrderInfo orderInfo) {
        String sql = "select * from car_user where id=?";
        BeanPropertyRowMapper<UserInfo> rowMapper = BeanPropertyRowMapper.newInstance(UserInfo.class);
        return jdbcTemplate.query(sql, new Object[]{orderInfo.getUser_id()}, rowMapper);
    }
}

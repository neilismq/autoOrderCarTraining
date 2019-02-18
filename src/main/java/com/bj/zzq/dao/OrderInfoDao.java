package com.bj.zzq.dao;

import com.bj.zzq.mapper.OrderInfoEntityMapper;
import com.bj.zzq.mapper.UserEntityMapper;
import com.bj.zzq.model.OrderInfoEntity;
import com.bj.zzq.model.OrderInfoEntityExample;
import com.bj.zzq.model.UserEntity;
import com.bj.zzq.model.UserEntityExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderInfoDao {
    @Autowired
    private OrderInfoEntityMapper orderInfoEntityMapper;

    public void insert(OrderInfoEntity orderInfoEntity) {
        orderInfoEntityMapper.insertSelective(orderInfoEntity);
    }

    public void update(OrderInfoEntity orderInfoEntity) {
        orderInfoEntityMapper.updateByPrimaryKeySelective(orderInfoEntity);
    }

    public List<OrderInfoEntity> selectExample(OrderInfoEntityExample example) {
        return orderInfoEntityMapper.selectByExample(example);
    }

    public void deleteById(String id) {
        orderInfoEntityMapper.deleteByPrimaryKey(id);
    }
}

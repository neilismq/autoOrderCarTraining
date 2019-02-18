package com.bj.zzq.mapper;

import com.bj.zzq.model.OrderInfoEntity;
import com.bj.zzq.model.OrderInfoEntityExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderInfoEntityMapper {
    int countByExample(OrderInfoEntityExample example);

    int deleteByExample(OrderInfoEntityExample example);

    int deleteByPrimaryKey(String id);

    int insert(OrderInfoEntity record);

    int insertSelective(OrderInfoEntity record);

    List<OrderInfoEntity> selectByExample(OrderInfoEntityExample example);

    OrderInfoEntity selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") OrderInfoEntity record, @Param("example") OrderInfoEntityExample example);

    int updateByExample(@Param("record") OrderInfoEntity record, @Param("example") OrderInfoEntityExample example);

    int updateByPrimaryKeySelective(OrderInfoEntity record);

    int updateByPrimaryKey(OrderInfoEntity record);
}
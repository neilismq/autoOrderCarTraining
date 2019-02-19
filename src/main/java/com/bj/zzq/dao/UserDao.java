package com.bj.zzq.dao;

import com.bj.zzq.mapper.UserEntityMapper;
import com.bj.zzq.model.UserEntity;
import com.bj.zzq.model.UserEntityExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {
    @Autowired
    private UserEntityMapper userEntityMapper;

    public void insert(UserEntity userEntity) {
        userEntityMapper.insertSelective(userEntity);
    }

    public void update(UserEntity userEntity) {
        userEntityMapper.updateByPrimaryKeySelective(userEntity);
    }

    public List<UserEntity> selectExample(UserEntityExample example) {
        return userEntityMapper.selectByExample(example);
    }

    public UserEntity selectByPrimaryKey(String id) {
        return userEntityMapper.selectByPrimaryKey(id);
    }

    public void deleteById(String id) {
        userEntityMapper.deleteByPrimaryKey(id);
    }

}

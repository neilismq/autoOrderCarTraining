package com.bj.zzq.core;

import com.bj.zzq.core.UserInfo;
import org.omg.CORBA.PRIVATE_MEMBER;

public class OrderInfo extends UserInfo {
    private String orderDate;
    private String orderType;
    private String user_id;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

}

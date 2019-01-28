package com.bj.zzq.core;

import com.bj.zzq.core.UserInfo;

public class OrderInfo extends UserInfo {
    private String orderDate;
    private String orderType;


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

    @Override
    public String toString() {
        return "username:" + this.getUsername() + ",password:" + this.getPassword() + ",email:" +
                this.getEmail() + ",cnbh:" + this.getCnbh() + ",orderDate:" + this.getOrderDate() + ",orderType:"
                + this.getOrderType();
    }
}

package com.bj.zzq.core;

import java.io.Serializable;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/28
 * @Description:
 */
public class UserInfo implements Serializable {
    private String username;
    private String password;
    private String email;
    private String cnbh;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCnbh() {
        return cnbh;
    }

    public void setCnbh(String cnbh) {
        this.cnbh = cnbh;
    }

}

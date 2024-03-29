package com.example.zhanghao.woaisiji.bean;

import java.util.List;

/**
 * Created by Cheng on 2019/7/16.
 */

public class CancellationOrder {
    /**
     * code : 200
     * data : []
     * msg : 成功取消订单!
     */

    private int code;
    private String msg;
    private List<?> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}

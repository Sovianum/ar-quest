package com.google.ar.core.examples.java.helloar.network;

import com.google.gson.annotations.SerializedName;

public class ServerResponse<T> {
    @SerializedName("err_msg")
    private String errMsg;

    @SerializedName("data")
    private T data;

    public String getErrMsg() {
        return errMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

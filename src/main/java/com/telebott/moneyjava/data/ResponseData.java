package com.telebott.moneyjava.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseData {
    private  int code = 200;
    private  String message;
    private  Object data;

    public ResponseData() {}
    public ResponseData(String message) {
        this.message = message;
    }
    public ResponseData(JSONObject data) {
        this.data = data;
    }
    public ResponseData(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public ResponseData(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public ResponseData(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseData success() {
        return new ResponseData();
    }
    public static ResponseData success(Object data) {
        return new ResponseData(200, data);
    }
    public static ResponseData success(JSONArray array, long total) {
        JSONObject object = new ObjectData("list", array).put("total",total).getObject();
        return new ResponseData(object);
    }

    public static ResponseData fail(String message) {
        return new ResponseData(404,message);
    }

    public static ResponseData success(String message, Object data) {
        return new ResponseData(200, message, data);
    }
}

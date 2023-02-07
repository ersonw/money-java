package com.telebott.moneyjava.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

@Getter
public class ObjectData {
    private final JSONObject object = new JSONObject();
    public ObjectData(){}
    public ObjectData(String key, Object val){
        object.put(key, val);
    }
    public ObjectData put(String key, Object val){
        object.put(key, val);
        return this;
    }
    public ObjectData remove(String key){
        object.remove(key);
        return this;
    }

    public JSONObject getObject() {
        return object;
    }
    public static ObjectData object(String key, Object val){
        return new ObjectData(key, val);
    }
    @Override
    public String toString() {
        return object.toJSONString();
    }
}

package com.telebott.moneyjava.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(includeFieldNames = true)
public class RequestHeader {
    private String ip;
    private String userAgent;
    private String token;
    private String serverName;
    private String host;
    private int serverPort;
    private String uri;
    private String url;
    private String schema;
    private String query;
    private String referer;
    private String user;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

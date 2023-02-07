package com.telebott.moneyjava.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class postData {
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("passwordOld")
    private String passwordOld;
    @JsonProperty("codeId")
    private String codeId;
    @JsonProperty("filePath")
    private String filePath;

    @JsonProperty("name")
    private String name;
    @JsonProperty("sfz")
    private String sfz;
    @JsonProperty("front")
    private String front;
    @JsonProperty("reverse")
    private String reverse;
    @JsonProperty("hand")
    private String hand;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("khh")
    private String khh;
    @JsonProperty("yhk")
    private String yhk;
    @JsonProperty("url")
    private String url;
    @JsonProperty("money")
    private double money;
    @JsonProperty("installments")
    private long installments;

}

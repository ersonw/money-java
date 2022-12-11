package com.telebott.moneyjava.control;

import com.telebott.moneyjava.data.ResponseData;
import com.telebott.moneyjava.data.postData;
import com.telebott.moneyjava.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class ApiControl {
    @Autowired
    private ApiService service;
    @GetMapping("/login/{phone}")
    public ResponseData loginPhoneSms(@PathVariable String phone, HttpServletRequest request){
        return service.loginPhoneSms(phone,request);
    }
    @PostMapping("/login/phone")
    public ResponseData loginPhone(@ModelAttribute postData data, HttpServletRequest request){
        return service.loginPhone(data.getUsername(),data.getPassword(),data.getCodeId(),request);
    }
    @GetMapping("/test")
    public String info(){
        return "index";
    }
}

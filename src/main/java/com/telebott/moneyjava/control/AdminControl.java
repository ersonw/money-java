package com.telebott.moneyjava.control;

import com.telebott.moneyjava.data.ResponseData;
import com.telebott.moneyjava.data.postData;
import com.telebott.moneyjava.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin")
public class AdminControl {
    @Autowired
    private AdminService service;
    @PostMapping("/login")
    public ResponseData login(@ModelAttribute postData data, HttpServletRequest request){
        return service.login(data.getUsername(),data.getPassword(),request);
    }
}

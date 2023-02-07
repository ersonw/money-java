package com.telebott.moneyjava.control;

import com.telebott.moneyjava.data.ResponseData;
import com.telebott.moneyjava.data.postData;
import com.telebott.moneyjava.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/user/changePassword")
    public ResponseData userChangePassword(@ModelAttribute postData data, HttpServletRequest request){
        return service.userChangePassword(data.getPasswordOld(),data.getPassword(),request);
    }
    @PostMapping("/upload")
    public ResponseData upload(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        return service.upload(file,request);
    }
    @PostMapping("/upload/delete")
    public ResponseData uploadDelete(@ModelAttribute postData data, HttpServletRequest request){
        return service.uploadDelete(data.getFilePath(),request);
    }
    @PostMapping("/details/sfz")
    public ResponseData detailsSfz(@ModelAttribute postData data, HttpServletRequest request){
        return service.detailsSfz(data.getName(),data.getSfz(),data.getFront(),data.getReverse(),data.getHand(),request);
    }
    @GetMapping("/details/getSfz")
    public ResponseData detailsSfzGet(HttpServletRequest request){
        return service.detailsSfzGet(request);
    }
    @GetMapping("/details/getYhk")
    public ResponseData detailsYhkGet(HttpServletRequest request){
        return service.detailsYhkGet(request);
    }
    @PostMapping("/details/yhk")
    public ResponseData detailsYhk(@ModelAttribute postData data, HttpServletRequest request){
        return service.detailsYhk(data.getPhone(),data.getKhh(),data.getYhk(),request);
    }
    @GetMapping("/details/getSign")
    public ResponseData detailsSignGet(HttpServletRequest request){
        return service.detailsSignGet(request);
    }
    @PostMapping("/details/sign")
    public ResponseData detailsSign(@ModelAttribute postData data, HttpServletRequest request){
        return service.detailsSign(data.getUrl(),request);
    }
    @PostMapping("/batch/upload")
    public ResponseData batchUpload(HttpServletRequest request){
        return service.batchUpload(request);
    }
    @GetMapping("/apply/get")
    public ResponseData applyGet(HttpServletRequest request){
        return service.applyGet(request);
    }
    @PostMapping("/apply")
    public ResponseData apply(@ModelAttribute postData data,HttpServletRequest request){
        return service.apply(data.getMoney(),data.getInstallments(),request);
    }
    @GetMapping("/order/{page}")
    public ResponseData order(@PathVariable int page,HttpServletRequest request){
        return service.order(page,request);
    }
    @GetMapping("/my/get")
    public ResponseData myGet(HttpServletRequest request){
        return service.myGet(request);
    }
    @GetMapping("/order/{orderNo}")
    public ResponseData orderNo(@PathVariable String orderNo,HttpServletRequest request){
        return service.orderNo(orderNo,request);
    }
//    @GetMapping("/order/get/{page}")
//    public ResponseData orderGet(@PathVariable int page,HttpServletRequest request){
//        return service.orderGet(page,request);
//    }
    @GetMapping("/test")
    public String test(){
        return service.test();
    }
}

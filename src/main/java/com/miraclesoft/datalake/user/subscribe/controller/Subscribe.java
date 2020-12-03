package com.miraclesoft.datalake.user.subscribe.controller;

import com.miraclesoft.datalake.user.message.response.ResponseMessage;
import com.miraclesoft.datalake.user.subscribe.model.SubscribeModel;
import com.miraclesoft.datalake.user.subscribe.repository.SubscribeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class Subscribe {

    @Autowired
    private SubscribeRepository subscribeRepository;
    @PostMapping("/subscribe")
    public ResponseEntity<?> saveSubscription(@Valid @RequestBody SubscribeModel subscribe){
        if(subscribe.getCarrier().equals("ATT")){
            String att = "@txt.att.net";
            subscribe.setMobile(subscribe.getMobile()+att);
        }else if(subscribe.getCarrier().equals("Tmobile")){
            String tmob = "@tmomail.net";
            subscribe.setMobile(subscribe.getMobile()+tmob);
        }else if(subscribe.getCarrier().equals("Sprint")){
            String sprint = "@messaging.sprintpcs.com";
            subscribe.setMobile(subscribe.getMobile()+sprint);
        }else if(subscribe.getCarrier().equals("Verizon")){
            String verizon = "@vtext.com";
            subscribe.setMobile(subscribe.getMobile()+verizon);
        }
        subscribeRepository.save(subscribe);
        return new ResponseEntity<>(new ResponseMessage("Subscription added successfully!"), HttpStatus.OK);
    }
    @DeleteMapping("/sub/{id}")
    public String deleteSub(@PathVariable Long id){
        SubscribeModel sub = subscribeRepository.getOne(id);
        subscribeRepository.delete(sub);
        return "Deleted Subscription";
    }
    @GetMapping("/subscribers/{job_name}")
    public List<SubscribeModel> getSubscribers(@PathVariable String job_name){
        return subscribeRepository.getAll(job_name);
    }

}


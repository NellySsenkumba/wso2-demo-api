package org.nelly.wso2demoapi.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import org.nelly.wso2demoapi.services.ApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Controller {
    private final ApiService apiService;

    @PostMapping("/createCustomer")
    public ResponseEntity<JSONObject> createCustomer(@RequestBody JSONObject request) {
        return new ResponseEntity<>(apiService.createCustomer(request), HttpStatus.CREATED);
    }

    @PostMapping("/createAccount")
    public ResponseEntity<JSONObject> createAccount(@RequestBody JSONObject request) {
        return new ResponseEntity<>(apiService.createAccount(request), HttpStatus.CREATED);
    }

    @PostMapping("/activateAccount")
    public ResponseEntity<JSONObject> activateAccount(@RequestBody JSONObject request) {
        return new ResponseEntity<>(apiService.activateAccount(request), HttpStatus.OK);
    }

    @PostMapping("/deposit")
    public ResponseEntity<JSONObject> deposit(@RequestBody JSONObject request) {
        return new ResponseEntity<>(apiService.deposit(request), HttpStatus.CREATED);
    }

    @PostMapping("/accountDetails")
    public ResponseEntity<JSONObject> getAccountDetails(@RequestBody JSONObject request) {
        return new ResponseEntity<>(apiService.getAccountDetails(request), HttpStatus.OK);
    }

    @PostMapping("/ministatement")
    public ResponseEntity<JSONArray> ministatement(@RequestBody JSONObject request) {
        return new ResponseEntity<>(apiService.ministatement(request), HttpStatus.CREATED);
    }

    @PostMapping("/transfer")
    public ResponseEntity<JSONObject> transfer(@RequestBody JSONObject request) {
        return new ResponseEntity<>(apiService.transfer(request), HttpStatus.CREATED);
    }

    @GetMapping("/accounts")
    public ResponseEntity<JSONArray> accounts() {
        return new ResponseEntity<>(apiService.accounts(), HttpStatus.OK);
    }

}

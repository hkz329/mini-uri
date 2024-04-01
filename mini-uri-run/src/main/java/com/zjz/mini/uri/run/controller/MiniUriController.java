package com.zjz.mini.uri.run.controller;

import com.zjz.mini.uri.framework.common.core.R;
import com.zjz.mini.uri.run.application.MiniUriService;
import com.zjz.mini.uri.run.domain.dto.GenerateUrlReq;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


/**
 * @author hkz329
 */
@RestController
@RequestMapping("/mini/uri")
public class MiniUriController {

    @Value("${server.host}")
    private String host;

    @Resource
    private MiniUriService miniUriService;


    @PostMapping("/generate")
    @ResponseBody
    public R generateShortURL(@RequestBody GenerateUrlReq req) {
        String shortURL = miniUriService.generateShortURL(req);
        return R.ok(host + shortURL);
    }
}

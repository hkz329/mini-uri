package com.zjz.mini.uri.run.controller;

import com.zjz.mini.uri.common.core.BusinessException;
import com.zjz.mini.uri.common.core.R;
import com.zjz.mini.uri.run.application.MiniUriService;
import com.zjz.mini.uri.run.domain.dto.GenerateUrlReq;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


/**
 * @author hkz329
 */
@RestController
@RequestMapping("/mini/uri")
public class MiniUriController {


    @Resource
    private MiniUriService miniUriService;


    @PostMapping("/generate")
    @ResponseBody
    public R generateShortURL(@RequestBody GenerateUrlReq req) {
        return miniUriService.generateShortURL(req);
    }
}

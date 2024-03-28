package com.zjz.mini.uri.run.controller;

import com.zjz.mini.uri.common.core.R;
import com.zjz.mini.uri.run.domain.dto.GenerateUrlReq;
import org.springframework.web.bind.annotation.*;


/**
 * @author hkz329
 */
@RestController
@RequestMapping("/mini/uri")
public class MiniUriController {


    @PostMapping("/generate")
    @ResponseBody
    public R generateShortURL(@RequestBody GenerateUrlReq req) {
        String longURL = req.getOriginalUrl();

        return R.ok();
    }
}

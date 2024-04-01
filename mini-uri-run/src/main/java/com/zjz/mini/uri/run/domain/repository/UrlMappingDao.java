package com.zjz.mini.uri.run.domain.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.mini.uri.run.domain.entity.UrlMapping;
import org.springframework.stereotype.Service;

@Service
public class UrlMappingDao extends ServiceImpl<UrlMappingMapper, UrlMapping>
    implements IService<UrlMapping> {

}

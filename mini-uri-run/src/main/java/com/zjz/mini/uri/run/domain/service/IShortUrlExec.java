package com.zjz.mini.uri.run.domain.service;


public interface IShortUrlExec {

    String generateShortUrl(String url);

    String generateShortUrl(String url, Integer expireTime);

    String redirect(String url);
}

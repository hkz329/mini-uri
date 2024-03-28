package com.zjz.mini.uri.run.domain.dto;


import java.util.Objects;

/**
 * 生成短链请求
 * @author hkz329
 */
public class GenerateUrlReq {

    private String originalUrl;

    public GenerateUrlReq() {
    }

    public GenerateUrlReq(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        GenerateUrlReq that = (GenerateUrlReq) object;
        return Objects.equals(originalUrl, that.originalUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalUrl);
    }
}

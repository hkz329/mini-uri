package com.zjz.mini.uri.run.domain.service;

import com.zjz.mini.uri.common.core.R;

public interface IShortUrlExec {

    String generateShortUrl(String url);

    R redirect(String url);
}

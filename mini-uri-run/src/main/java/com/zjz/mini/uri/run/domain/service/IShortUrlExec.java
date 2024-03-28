package com.zjz.mini.uri.run.domain.service;

import com.zjz.mini.uri.common.core.R;

public interface IShortUrlExec {

    R generateShortUrl(String url);

    R redirect(String url);
}

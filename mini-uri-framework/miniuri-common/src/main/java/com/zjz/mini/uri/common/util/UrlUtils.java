package com.zjz.mini.uri.common.util;

import java.util.regex.Pattern;

/**
 * url 工具类
 * @author 19002
 */
public class UrlUtils {
    private static final Pattern URL_REG = Pattern.compile("^(((ht|f)tps?):\\/\\/)?[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?$");

    public static boolean checkURL(String url) {
        return URL_REG.matcher(url).matches();
    }
}

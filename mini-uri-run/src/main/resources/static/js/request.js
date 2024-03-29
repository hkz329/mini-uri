/**
 * httpRequest
 */
const httpRequest = (url, method = 'GET', data = null, customOptions = {}) => {
    return new Promise((resolve, reject) => {
        const defaultOptions = {
            url: url,
            type: method,
            contentType: "application/json",
            dataType: "json",
            success: resolve,
            error: (jqXHR, textStatus, errorThrown) => reject(new Error(textStatus)),
            ...customOptions // 这里允许覆盖默认设置或添加新的设置
        };

        // 如果传递了data，转换为JSON字符串
        if (data) {
            defaultOptions.data = JSON.stringify(data);
        }

        $.ajax(defaultOptions);
    });
};


/**
 * httpRequest (native fetch)
 */
const httpRequest = (url, method = 'GET', data = null, customOptions = {}) => {
    const headers = {
        'Content-Type': 'application/json',
        ...(customOptions.headers || {})
    };

    const options = {
        method,
        headers,
        ...customOptions
    };

    if (data !== null && data !== undefined) {
        options.body = JSON.stringify(data);
    }

    return fetch(url, options).then(async (res) => {
        if (!res.ok) {
            const text = await res.text();
            throw new Error(text || res.statusText);
        }
        const ct = res.headers.get('content-type') || '';
        if (ct.includes('application/json')) {
            return res.json();
        }
        return res.text();
    });
};


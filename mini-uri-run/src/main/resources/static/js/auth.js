/**
 * 认证相关工具函数
 */

// 获取存储的访问令牌
function getAccessToken() {
    return localStorage.getItem('accessToken');
}

// 获取存储的刷新令牌
function getRefreshToken() {
    return localStorage.getItem('refreshToken');
}

// 获取存储的用户信息
function getUserInfo() {
    const userInfoStr = localStorage.getItem('userInfo');
    return userInfoStr ? JSON.parse(userInfoStr) : null;
}

// 检查用户是否已登录
function isLoggedIn() {
    const token = getAccessToken();
    const userInfo = getUserInfo();
    return !!(token && userInfo);
}

// 清除认证信息
function clearAuthInfo() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userInfo');
}

// 带认证的请求
async function authenticatedRequest(url, options = {}) {
    const token = getAccessToken();
    
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    const response = await fetch(url, {
        ...options,
        headers
    });
    
    // 如果返回401，尝试刷新令牌
    if (response.status === 401) {
        const refreshed = await refreshAccessToken();
        if (refreshed) {
            // 重新发送请求
            headers['Authorization'] = `Bearer ${getAccessToken()}`;
            return fetch(url, {
                ...options,
                headers
            });
        } else {
            // 刷新失败，跳转到登录页
            redirectToLogin();
            throw new Error('认证失败，请重新登录');
        }
    }
    
    return response;
}

// 刷新访问令牌
async function refreshAccessToken() {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
        return false;
    }
    
    try {
        const response = await fetch('/api/auth/refresh', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ refreshToken })
        });
        
        const data = await response.json();
        
        if (data.code === 200) {
            localStorage.setItem('accessToken', data.data.accessToken);
            localStorage.setItem('userInfo', JSON.stringify(data.data.userInfo));
            return true;
        } else {
            clearAuthInfo();
            return false;
        }
    } catch (error) {
        console.error('Refresh token failed:', error);
        clearAuthInfo();
        return false;
    }
}

// 用户登出
async function logout() {
    try {
        await authenticatedRequest('/api/auth/logout', {
            method: 'POST'
        });
    } catch (error) {
        console.error('Logout error:', error);
    } finally {
        clearAuthInfo();
        window.location.href = '/';
    }
}

// 跳转到登录页
function redirectToLogin(redirectUrl) {
    const currentUrl = redirectUrl || window.location.pathname + window.location.search;
    const loginUrl = `/auth/login?redirect=${encodeURIComponent(currentUrl)}`;
    window.location.href = loginUrl;
}

// 检查用户权限
function hasPermission(requiredUserType) {
    const userInfo = getUserInfo();
    if (!userInfo) {
        return false;
    }
    
    // 用户类型：1-免费用户，2-付费用户，3-VIP用户
    return userInfo.userType >= requiredUserType;
}

// 检查是否为付费用户
function isPremiumUser() {
    return hasPermission(2);
}

// 检查是否为VIP用户
function isVipUser() {
    return hasPermission(3);
}

// 显示用户信息
function displayUserInfo() {
    const userInfo = getUserInfo();
    if (!userInfo) {
        return;
    }
    
    // 可以在页面上显示用户信息
    const userDisplayElements = document.querySelectorAll('.user-display');
    userDisplayElements.forEach(element => {
        element.textContent = userInfo.nickname || userInfo.username;
    });
    
    // 显示用户类型标识
    const userTypeElements = document.querySelectorAll('.user-type');
    userTypeElements.forEach(element => {
        let typeText = '免费用户';
        let typeClass = 'user-type-free';
        
        if (userInfo.userType === 3) {
            typeText = 'VIP用户';
            typeClass = 'user-type-vip';
        } else if (userInfo.userType === 2) {
            typeText = '付费用户';
            typeClass = 'user-type-premium';
        }
        
        element.textContent = typeText;
        element.className = `user-type ${typeClass}`;
    });
}

// 页面加载时初始化
document.addEventListener('DOMContentLoaded', function() {
    // 如果用户已登录，显示用户信息
    if (isLoggedIn()) {
        displayUserInfo();
    }
    
    // 为登出按钮添加事件监听
    const logoutBtns = document.querySelectorAll('.logout-btn');
    logoutBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            if (confirm('确定要登出吗？')) {
                logout();
            }
        });
    });
});
# 访问统计功能使用说明

## 📊 功能概述

这是一个**轻量级、高性能**的网站访问统计系统，专为 Mini URI 短链接项目设计。

### 核心特性

✅ **高性能** - Redis + MySQL 双存储，异步处理，不影响页面响应  
✅ **自动统计** - 拦截器自动记录，无需手动调用  
✅ **PV/UV 统计** - 支持访问次数(PV)和独立访客数(UV)统计  
✅ **实时查询** - Redis 提供实时数据，定时同步到 MySQL  
✅ **数据自主** - 不依赖第三方，数据完全自主可控  
✅ **详细日志** - 可选的访问日志表，记录 IP、UA、Referer 等详细信息  

## 🚀 快速开始

### 1. 执行数据库脚本

```sql
-- 在你的 MySQL 数据库中执行
source docs/visitor_stats.sql
```

这将创建两张表：
- `visitor_stats` - 访问统计表（必须）
- `visitor_log` - 访问日志表（可选，用于详细分析）

### 2. 启动应用

无需额外配置，启动应用即可自动生效！

拦截器会自动统计所有页面访问（排除静态资源和 API 接口）。

### 3. 访问测试

访问首页：`http://localhost:8882/`

系统会自动记录访问数据到 Redis。

## 📡 API 接口

### 1. 获取今日实时统计

```bash
GET /stats/today?pagePath=/

# 响应示例
{
  "code": 200,
  "data": {
    "pv": 156,    // 今日访问次数
    "uv": 89      // 今日独立访客数
  },
  "message": "success"
}
```

### 2. 获取历史统计

```bash
GET /stats/history?pagePath=/&days=7

# 响应示例
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "pagePath": "/",
      "pageName": "首页",
      "visitCount": 156,
      "uniqueCount": 89,
      "statDate": "2025-10-06"
    },
    // ...更多历史数据
  ],
  "message": "success"
}
```

### 3. 获取总统计

```bash
GET /stats/total?pagePath=/

# 响应示例
{
  "code": 200,
  "data": {
    "totalPv": 5678,   // 总访问次数
    "totalUv": 1234    // 总独立访客数
  },
  "message": "success"
}
```

### 4. 获取综合统计

```bash
GET /stats/summary?pagePath=/&days=7

# 响应示例
{
  "code": 200,
  "data": {
    "today": {
      "pv": 156,
      "uv": 89
    },
    "history": [ /* 历史数据列表 */ ],
    "total": {
      "totalPv": 5678,
      "totalUv": 1234
    }
  },
  "message": "success"
}
```

### 5. 手动同步数据

```bash
POST /stats/sync

# 响应示例
{
  "code": 200,
  "data": "数据同步成功",
  "message": "success"
}
```

## ⚙️ 工作原理

### 数据流程

```
用户访问页面
    ↓
拦截器捕获请求
    ↓
异步记录到 Redis（PV +1，UV Set 去重）
    ↓
定时任务（每天凌晨 2 点）
    ↓
同步 Redis 数据到 MySQL
    ↓
持久化存储，供历史查询
```

### 技术实现

1. **拦截器** (`VisitorStatsInterceptor`)
   - 拦截所有页面请求
   - 自动排除静态资源和 API
   - 异步记录，不阻塞请求

2. **Redis 存储**
   - PV: String 类型，自增计数
   - UV: Set 类型，自动去重
   - Key 格式: `visitor:pv:/index:2025-10-06`
   - 数据保留 7 天

3. **定时同步** (`VisitorStatsSyncJob`)
   - 每天凌晨 2 点自动执行
   - 读取 Redis 数据同步到 MySQL
   - 支持增量更新

4. **访客识别**
   - 基于 `IP + User-Agent` 生成唯一标识
   - MD5 加密存储
   - 同一天内同一访客只计数一次 UV

## 📈 前端展示示例

### 在首页显示访问统计

你可以在 `index.html` 中添加以下代码，实时显示访问量：

```html
<!-- 在页脚或其他位置添加 -->
<div class="visitor-stats">
  <span>👁️ 今日访问：<strong id="today-pv">-</strong></span>
  <span>👥 今日访客：<strong id="today-uv">-</strong></span>
  <span>📊 总访问量：<strong id="total-pv">-</strong></span>
</div>

<script>
// 加载访问统计
fetch('/stats/summary?pagePath=/')
  .then(res => res.json())
  .then(data => {
    if (data.code === 200) {
      document.getElementById('today-pv').textContent = data.data.today.pv;
      document.getElementById('today-uv').textContent = data.data.today.uv;
      document.getElementById('total-pv').textContent = data.data.total.totalPv;
    }
  })
  .catch(err => console.error('加载统计数据失败', err));
</script>
```

## 🔧 高级配置

### 自定义统计规则

修改 `VisitorStatsInterceptor.java` 中的 `shouldRecord()` 方法：

```java
private boolean shouldRecord(String path) {
    // 添加你想排除的路径
    if (path.startsWith("/admin/")) {
        return false;
    }
    // 添加你想统计的路径
    if (path.startsWith("/product/")) {
        return true;
    }
    return true;
}
```

### 调整同步时间

修改 `VisitorStatsSyncJob.java` 中的 cron 表达式：

```java
@Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨2点
// 改为
@Scheduled(cron = "0 0 * * * ?")  // 每小时执行一次
```

### 调整 Redis 数据保留时间

修改 `VisitorStatsService.java`：

```java
stringRedisTemplate.expire(pvKey, 7, TimeUnit.DAYS);
// 改为
stringRedisTemplate.expire(pvKey, 30, TimeUnit.DAYS);  // 保留30天
```

## 🎯 性能优化

1. **异步处理** - 使用 `@Async` 注解，统计不阻塞主线程
2. **Redis 缓存** - 实时数据存 Redis，减少数据库压力
3. **批量同步** - 定时批量同步到 MySQL，而非实时写入
4. **索引优化** - 数据库表已添加必要索引
5. **过期清理** - Redis 自动过期，无需手动清理

## 📊 数据查询示例

### SQL 查询

```sql
-- 查询最近7天首页访问量
SELECT stat_date, visit_count, unique_count
FROM visitor_stats
WHERE page_path = '/' AND stat_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
ORDER BY stat_date DESC;

-- 查询访问量最高的页面
SELECT page_path, page_name, SUM(visit_count) as total_pv
FROM visitor_stats
GROUP BY page_path, page_name
ORDER BY total_pv DESC
LIMIT 10;

-- 查询访问日志详情
SELECT * FROM visitor_log
WHERE page_path = '/' AND DATE(visit_time) = CURDATE()
ORDER BY visit_time DESC
LIMIT 100;
```

## ⚠️ 注意事项

1. **Redis 必须启动** - 统计功能依赖 Redis
2. **定时任务** - 确保应用启动时启用了 `@EnableScheduling`
3. **异步支持** - 确保启用了 `@EnableAsync`
4. **访客识别** - 基于 IP+UA，同一设备不同浏览器会被识别为不同访客
5. **数据一致性** - Redis 数据同步到 MySQL 前可能有延迟

## 🚨 故障排查

### 统计不生效

1. 检查 Redis 是否正常连接
2. 检查拦截器是否注册成功
3. 查看日志是否有异常

### 数据不同步

1. 检查定时任务是否执行
2. 手动调用 `/stats/sync` 接口测试
3. 检查数据库表是否创建成功

### UV 统计偏高

- 如果用户使用不同设备访问，会被计为不同访客
- 可以考虑使用 Cookie 或 LocalStorage 优化访客识别

## 📝 后续扩展

可以继续扩展的功能：

1. **地域统计** - 根据 IP 解析地理位置
2. **设备统计** - 解析 User-Agent，统计设备类型
3. **时段分析** - 统计不同时段的访问量
4. **来源统计** - 分析 Referer，了解访客来源
5. **实时大屏** - WebSocket 推送实时数据
6. **报表导出** - 导出 Excel/PDF 报表

## 🎉 总结

现在你拥有了一个：
- ✅ 完全自主可控的访问统计系统
- ✅ 高性能、低延迟的统计方案
- ✅ 支持 PV/UV 双指标
- ✅ 实时查询 + 历史分析
- ✅ 易于扩展的架构设计

相比第三方统计服务，你获得了：
- 🔒 数据隐私保护
- 🚀 更快的加载速度
- 💰 零额外成本
- 🎨 完全自定义的能力

Have fun! 🎊


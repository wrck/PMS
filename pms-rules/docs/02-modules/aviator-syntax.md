# Aviator 表达式语法手册

> 本文档是 pms-rules 模块中 Aviator 5.4.3 表达式语法的参考手册，涵盖运算符、函数、变量引用、集合操作和条件表达式。

---

## 1. 基本语法

### 1.1 字面量

| 类型 | 语法 | 示例 | 说明 |
|------|------|------|------|
| 整数 | 数字 | `42`、`-7` | 默认 long 类型 |
| 浮点数 | 带小数点 | `3.14`、`-0.5` | 默认 double 类型 |
| 布尔 | `true`/`false` | `true` | — |
| 字符串 | 单引号/双引号 | `'hello'`、`"hello"` | 单引号为字面量；双引号支持 `#{}` 插值 |
| nil | `nil` | `nil` | 空值 |
| 长整数 | 数字+`L` | `100L` | 显式 long |
| 大整数 | 数字+`N` | `100N` | BigInteger |
| 大小数 | 数字+`M` | `3.14M` | BigDecimal |

### 1.2 变量引用

```
// 普通变量名：字母/数字/下划线，不以数字开头
price
userName
order_id

// 中文变量名：需反引号包裹
`价格`
`用户名`
```

变量从 `env` Map 中查找，未找到则为 `nil`。

---

## 2. 运算符

### 2.1 算术运算符

| 运算符 | 说明 | 示例 | 结果 |
|--------|------|------|------|
| `+` | 加法 | `1 + 2` | `3` |
| `-` | 减法 | `5 - 3` | `2` |
| `*` | 乘法 | `2 * 3` | `6` |
| `/` | 除法 | `10 / 3` | `3`（整数除法） |
| `%` | 取模 | `10 % 3` | `1` |

**类型提升规则**：

```
long + long     → long
long + double   → double
double + BigDecimal → BigDecimal
BigInteger + long → BigInteger
```

### 2.2 比较运算符

| 运算符 | 说明 | 示例 | 结果 |
|--------|------|------|------|
| `>` | 大于 | `5 > 3` | `true` |
| `>=` | 大于等于 | `5 >= 5` | `true` |
| `<` | 小于 | `3 < 5` | `true` |
| `<=` | 小于等于 | `3 <= 3` | `true` |
| `==` | 等于 | `1 == 1` | `true` |
| `!=` | 不等于 | `1 != 2` | `true` |

> **注意**：`==` 对字符串比较使用 `equals` 语义，非引用比较。

### 2.3 逻辑运算符

| 运算符 | 说明 | 示例 | 结果 |
|--------|------|------|------|
| `&&` | 逻辑与（短路） | `true && false` | `false` |
| `\|\|` | 逻辑或（短路） | `true \|\| false` | `true` |
| `!` | 逻辑非 | `!true` | `false` |

### 2.4 位运算符

| 运算符 | 说明 | 示例 |
|--------|------|------|
| `&` | 按位与 | `0xFF & 0x0F` |
| `\|` | 按位或 | `0x0F \| 0xF0` |
| `^` | 按位异或 | `0xFF ^ 0x0F` |
| `~` | 按位取反 | `~0xFF` |
| `<<` | 左移 | `1 << 4` |
| `>>` | 右移 | `256 >> 4` |

### 2.5 三元运算符

```
条件 ? 真值 : 假值

// 嵌套
score >= 90 ? 'A' : (score >= 60 ? 'B' : 'C')
```

### 2.6 运算符优先级

从高到低：

| 优先级 | 运算符 |
|--------|--------|
| 1 | `.` `[]` `()` `fn()` |
| 2 | `!` `~` `-`（一元） |
| 3 | `*` `/` `%` |
| 4 | `+` `-` |
| 5 | `<<` `>>` |
| 6 | `&` |
| 7 | `^` |
| 8 | `\|` |
| 9 | `<` `<=` `>` `>=` |
| 10 | `==` `!=` |
| 11 | `&&` |
| 12 | `\|\|` |
| 13 | `? :` |

---

## 3. 集合操作

### 3.1 List

```
// 创建
[1, 2, 3, 4, 5]
['a', 'b', 'c']

// 范围创建
[1..10]     // [1, 2, 3, ..., 10]
[10..1]     // [10, 9, 8, ..., 1]

// 索引访问
list[0]         // 第一个元素
list[-1]        // 最后一个元素
list[1..3]      // 子列表 [list[1], list[2], list[3]]
```

### 3.2 Map

```
// 创建
{"name": "张三", "age": 25}
{1: "one", 2: "two"}

// 访问
map["name"]     // 通过键访问
map.name        // 通过属性访问（键为字符串时）
```

### 3.3 seq 函数库

| 函数 | 说明 | 示例 |
|------|------|------|
| `seq.count(coll)` | 元素个数 | `seq.count([1,2,3])` → 3 |
| `seq.empty(coll)` | 是否为空 | `seq.empty([])` → true |
| `seq.contains(coll, item)` | 是否包含 | `seq.contains([1,2,3], 2)` → true |
| `seq.get(coll, idx)` | 按索引取值 | `seq.get([1,2,3], 0)` → 1 |
| `seq.add(coll, item)` | 添加元素 | — |
| `seq.map(coll, fn)` | 映射 | `seq.map([1,2,3], lambda(x) -> x*2 end)` |
| `seq.filter(coll, fn)` | 过滤 | `seq.filter([1,2,3,4], lambda(x) -> x>2 end)` |
| `seq.reduce(coll, fn, init)` | 归约 | `seq.reduce([1,2,3], lambda(a,b) -> a+b end, 0)` |
| `seq.sort(coll)` | 排序 | `seq.sort([3,1,2])` → [1,2,3] |
| `seq.every(coll, fn)` | 全部满足 | — |
| `seq.some(coll, fn)` | 任一满足 | — |

---

## 4. 字符串函数

### 4.1 string 函数库

| 函数 | 说明 | 示例 |
|------|------|------|
| `string.length(s)` | 长度 | `string.length("hello")` → 5 |
| `string.contains(s, sub)` | 是否包含 | `string.contains("hello", "ell")` → true |
| `string.startsWith(s, prefix)` | 前缀 | `string.startsWith("hello", "he")` → true |
| `string.endsWith(s, suffix)` | 后缀 | `string.endsWith("hello", "lo")` → true |
| `string.substring(s, start)` | 子串 | `string.substring("hello", 1)` → "ello" |
| `string.substring(s, start, end)` | 子串 | `string.substring("hello", 1, 3)` → "el" |
| `string.indexOf(s, sub)` | 查找位置 | `string.indexOf("hello", "l")` → 2 |
| `string.split(s, regex)` | 分割 | `string.split("a,b,c", ",")` → ["a","b","c"] |
| `string.join(s1, s2)` | 拼接 | `string.join("你好, ", "张三")` → "你好, 张三" |
| `string.replace(s, old, new)` | 替换 | `string.replace("hello", "l", "L")` → "heLLo" |
| `string.toUpperCase(s)` | 大写 | `string.toUpperCase("hello")` → "HELLO" |
| `string.toLowerCase(s)` | 小写 | `string.toLowerCase("HELLO")` → "hello" |
| `string.trim(s)` | 去空格 | `string.trim("  hi  ")` → "hi" |

---

## 5. 数学函数

通过 `JavaMethodReflectionFunctionMissing` 反射调用 Java 静态方法：

| 函数 | 说明 | 示例 |
|------|------|------|
| `Math.max(a, b)` | 最大值 | `Math.max(3, 5)` → 5 |
| `Math.min(a, b)` | 最小值 | `Math.min(3, 5)` → 3 |
| `Math.abs(a)` | 绝对值 | `Math.abs(-3)` → 3 |
| `Math.round(a)` | 四舍五入 | `Math.round(3.6)` → 4 |
| `Math.floor(a)` | 向下取整 | `Math.floor(3.7)` → 3.0 |
| `Math.ceil(a)` | 向上取整 | `Math.ceil(3.2)` → 4.0 |
| `Math.pow(a, b)` | 幂运算 | `Math.pow(2, 10)` → 1024.0 |
| `Math.sqrt(a)` | 平方根 | `Math.sqrt(16)` → 4.0 |

---

## 6. 类型判断与转换

> **重要**：以下函数清单基于 Aviator 5.4.3 官方内置函数库。注意 Aviator 与 JavaScript 的差异：Aviator 使用 `type(x)`（非 `typeof`），判断 nil 使用 `x == nil` 比较运算符（无 `is_nil` 函数）。

| 函数/运算符 | 说明 | 示例 |
|------|------|------|
| `x == nil` | 判断是否为 nil（**运算符，非函数**） | `nil == nil` → true |
| `is_def(x)` | 是否已定义 | `is_def(undefined_var)` → false |
| `type(x)` | 类型名 | `type(1)` → "long" |
| `is_a(x, type)` | 类型判断 | — |

> ⚠️ **避坑**：Aviator 5.4.3 **不存在** `is_nil(x)` 和 `typeof(x)` 函数（`typeof` 是 JavaScript 运算符）。如需判断 nil，必须使用 `x == nil`；如需获取类型名，必须使用 `type(x)`。

**类型名对照**：

| Java 类型 | Aviator 类型名 |
|-----------|----------------|
| `null` | `nil` |
| `Long` | `long` |
| `Double` | `double` |
| `Boolean` | `boolean` |
| `String` | `string` |
| `List` | `list` |
| `Map` | `map` |
| `BigDecimal` | `decimal` |
| `BigInteger` | `bigint` |

---

## 7. 条件表达式

### 7.1 三元表达式

```
// 简单条件
amount > 1000 ? '大额' : '小额'

// 嵌套条件
amount > 10000 ? '特大额' : (amount > 1000 ? '大额' : '小额')
```

### 7.2 逻辑组合

```
// AND
status == 'approved' && amount > 0

// OR
state == 'draft' || state == 'pending'

// NOT
!isClosed

// 复合
(status == 'approved' || status == 'paid') && amount > 1000 && !cancelled
```

### 7.3 nil 安全判断

```
// 判断非 nil
entity != nil && entity.amount > 0

// 判断为 nil
invoice_number == nil

// 安全访问（Aviator 5.x 不支持 ?. 操作符，需显式判断）
entity != nil ? entity.name : ''
```

---

## 8. PMS 业务表达式示例

### 8.1 发票类型判断

```
// 判断是否为发票（entity.entity 为嵌套结构）
entity.entity.invoice_number != nil && entity.entity.amount > 0

// 判断发票类型
entity.entity.invoice_type == '增值税专用发票'
```

### 8.2 项目状态更新条件

```
// 项目状态为创建状态且已指定服务经理
entity.projectState == 30 && entity.serviceManagerCode != nil

// 维护记录已激活
entity.maintenanceState == 'ACTIVE' && entity.effectiveTo == nil
```

### 8.3 分包验收审批人条件

```
// 金额大于阈值需要部门经理审批
entity.amount > 10000 && config.role == 'DEPT_MANAGER'

// 特定项目类型需要技术审批
entity.projectType == '工程类' && taskVars.getVariable('needTechReview') == true
```

### 8.4 售前项目自动启动脚本

```
// 根据办事处设置项目类型（依赖 context 反射调用）
presales.officeName == '战略合作部' ? setProjectType(presales, '战略合作') : nil
```

---

## 9. Lambda 表达式（Aviator 5.x）

```
// 基本语法
lambda(x) -> x * 2 end

// 多参数
lambda(x, y) -> x + y end

// 配合 seq.map 使用
seq.map([1, 2, 3, 4, 5], lambda(x) -> x * x end)
// 结果: [1, 4, 9, 16, 25]

// 配合 seq.filter 使用
seq.filter([1, 2, 3, 4, 5], lambda(x) -> x > 3 end)
// 结果: [4, 5]
```

> **注意**：PMS 当前业务表达式中未发现 Lambda 使用，但 Aviator 5.4.3 支持。

---

## 10. 表达式编写规范

### 10.1 命名规范

- 变量名使用驼峰命名：`projectState`、`invoiceNumber`
- 布尔变量以 `is`/`has`/`need` 开头：`isApproved`、`needVerify`
- 避免单字母变量名（循环变量除外）

### 10.2 复杂度控制

- 单个表达式不超过 100 字符
- 三元嵌套不超过 2 层
- 逻辑组合不超过 4 个条件
- 超过限制时拆分为多个表达式

### 10.3 nil 安全

```
// ❌ 危险：entity 可能为 nil
entity.amount > 0

// ✅ 安全：先判断非 nil
entity != nil && entity.amount > 0
```

### 10.4 类型注意

```
// 整数除法
10 / 3        // 结果: 3 (long)
10.0 / 3      // 结果: 3.333... (double)

// 字符串比较
status == 'approved'    // 使用 == 进行值比较
```

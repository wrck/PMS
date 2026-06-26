# 验证码组件

## 1. 概述

`CaptchaUtil` 提供图形验证码生成能力，生成 80×30 像素的 PNG 图片，包含 4 位随机字符和 50 条干扰线。

---

## 2. 类定义

```java
package com.dp.plat.security.util;

public class CaptchaUtil {
    private static final String RANDOM_STRS = "123456789ABCDEFGHIJKLMNPQRSTUVWXYZ";
    private static final String FONT_NAME = "Fixedsys";
    private static final int FONT_SIZE = 20;
    private Random random = new SecureRandom();
    
    private int width = 80;
    private int height = 30;
    private int lineNum = 50;
    private int strNum = 4;
}
```

---

## 3. 配置参数

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `RANDOM_STRS` | `123456789ABCDEFGHIJKLMNPQRSTUVWXYZ` | 字符池（无 O、0 等易混淆字符） |
| `FONT_NAME` | `Fixedsys` | 字体名称 |
| `FONT_SIZE` | 20 | 字体大小 |
| `width` | 80 | 图片宽度（像素） |
| `height` | 30 | 图片高度（像素） |
| `lineNum` | 50 | 干扰线数量 |
| `strNum` | 4 | 验证码字符数 |

> 使用 `SecureRandom`（非 `Math.random()`），保证随机数安全性。

---

## 4. 方法签名

| 方法 | 返回值 | 说明 |
|------|--------|------|
| `genRandomCode()` | `String` | 生成 4 位随机码（不绘图） |
| `genRandomCodeImage(String randomCode)` | `BufferedImage` | 根据指定随机码生成图片 |
| `genRandomCodeImage(StringBuffer randomCode)` | `BufferedImage` | 生成图片并填充 randomCode |
| `getRandomString(int num)` | `String` | 获取字符池中指定位置的字符 |
| `responseCaptcha(HttpServletRequest, HttpServletResponse, String KEY_CAPTCHA)` | `void`（static） | 生成验证码并写入 Response |
| `getRandColor(int fc, int bc)` | `Color`（private） | 生成随机颜色 |
| `drowString(Graphics, int)` | `String`（private） | 绘制随机字符 |
| `drowString(Graphics, String, int)` | `String`（private） | 绘制指定字符 |
| `drowLine(Graphics)` | `void`（private） | 绘制干扰线 |

---

## 5. 核心实现

### 5.1 genRandomCodeImage（StringBuffer 版本）

```java
public BufferedImage genRandomCodeImage(StringBuffer randomCode) {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
    Graphics g = image.getGraphics();
    // 背景色
    g.setColor(getRandColor(250, 255));
    g.fillRect(0, 0, width, height);
    // 干扰线
    for (int i = 0; i <= lineNum; i++) {
        drowLine(g);
    }
    // 字符
    g.setFont(new Font(FONT_NAME, Font.ROMAN_BASELINE, FONT_SIZE));
    for (int i = 1; i <= strNum; i++) {
        randomCode.append(drowString(g, i));
    }
    g.dispose();
    return image;
}
```

### 5.2 drowString（绘制字符）

```java
private String drowString(Graphics g, String rand, int offset) {
    g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
    g.translate(random.nextInt(3), random.nextInt(3));
    g.drawString(rand, 13 * offset, 20);
    return rand;
}
```

- 字符颜色：RGB 各分量 0-199 随机
- 字符位置：`x = 13 * offset`，`y = 20`，附加 0-2 像素随机偏移

### 5.3 drowLine（干扰线）

```java
private void drowLine(Graphics g) {
    int x = random.nextInt(width);
    int y = random.nextInt(height);
    int x0 = random.nextInt(16);
    int y0 = random.nextInt(16);
    g.setColor(getRandColor(0, 255));
    g.drawLine(x, y, x + x0, y + y0);
}
```

### 5.4 responseCaptcha（静态方法）

```java
public static void responseCaptcha(HttpServletRequest req, HttpServletResponse resp, 
                                   String KEY_CAPTCHA) {
    resp.setContentType("image/png");
    resp.setHeader("Pragma", "No-cache");
    resp.setHeader("Cache-Control", "no-cache");
    resp.setDateHeader("Expire", 0);
    try {
        HttpSession session = req.getSession();
        CaptchaUtil tool = new CaptchaUtil();
        StringBuffer code = new StringBuffer();
        BufferedImage image = tool.genRandomCodeImage(code);
        session.removeAttribute(KEY_CAPTCHA);
        session.setAttribute(KEY_CAPTCHA, code.toString());
        ImageIO.write(image, "png", resp.getOutputStream());
        resp.getOutputStream().close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

---

## 6. 使用示例

### 6.1 生成验证码图片并写入 Session

```java
// 在 Controller/Action 中
CaptchaUtil.responseCaptcha(request, response, "captchaKey");
// 验证码已存入 session.getAttribute("captchaKey")
```

### 6.2 校验验证码

```java
String inputCode = request.getParameter("captcha");
String sessionCode = (String) request.getSession().getAttribute("captchaKey");
if (inputCode != null && inputCode.equalsIgnoreCase(sessionCode)) {
    // 验证通过
} else {
    // 验证失败
}
```

### 6.3 独立生成图片

```java
CaptchaUtil tool = new CaptchaUtil();
StringBuffer code = new StringBuffer();
BufferedImage image = tool.genRandomCodeImage(code);
System.out.println("验证码：" + code.toString());
ImageIO.write(image, "png", new FileOutputStream("captcha.png"));
```

---

## 7. 字符池说明

`RANDOM_STRS = "123456789ABCDEFGHIJKLMNPQRSTUVWXYZ"`

- 包含数字 1-9（无 0）、大写字母 A-Z（无 O）
- 排除易混淆字符：`0`（与 O 混淆）、`O`（与 0 混淆）
- 共 34 个字符

---

## 8. 相关文档

| 文档 | 说明 |
|------|------|
| [class-reference.md](class-reference.md) | 类参考清单 |

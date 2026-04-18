# 自镜 MirrorMe

> 你的手机比你更了解你 — 被动感知 · 本地隐私 · 深度人格洞察

---

## 目录

- [项目简介](#项目简介)
- [环境要求](#环境要求)
- [方式一：一键脚本构建（推荐）](#方式一一键脚本构建推荐)
- [方式二：Android Studio 构建](#方式二android-studio-构建)
- [安装到手机](#安装到手机)
- [首次使用授权](#首次使用授权)
- [项目结构](#项目结构)
- [常见问题](#常见问题)

---

## 项目简介

MirrorMe 是一款 Android 行为画像 App，通过被动采集手机使用数据（屏幕时长、App 使用分类、步数、位置熵值等），在设备本地生成用户的情绪状态、Big Five 人格评分和行为习惯画像。

**所有数据完全本地存储，不上传任何服务器。**

---

## 环境要求

| 项目 | 最低要求 |
|------|---------|
| 操作系统 | Ubuntu 20.04+ / macOS 12+ / Windows 10+ WSL2 |
| 磁盘空间 | 8 GB（SDK + 依赖） |
| 内存 | 4 GB |
| 手机系统 | Android 8.0（API 26）及以上 |

---

## 方式一：一键脚本构建（推荐）

适用于 **Linux / WSL2** 环境。

### 第一步：克隆或进入项目目录

```bash
cd ~/my_project/mirrorme
```

### 第二步：运行构建脚本

```bash
bash build_apk.sh
```

脚本会自动完成：

1. 安装 OpenJDK 17（需要 sudo 密码）
2. 下载 Android 命令行工具（约 150 MB）
3. 安装 Android SDK 组件（约 500 MB，首次约 10 分钟）
4. 编译生成 APK（首次约 15 分钟，含 Gradle 依赖下载）

构建完成后，APK 路径为：

```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 方式二：Android Studio 构建

适合 Windows / macOS 用户。

### 第一步：安装 Android Studio

前往官网下载并安装：
```
https://developer.android.com/studio
```

### 第二步：打开项目

1. 启动 Android Studio
2. 点击 **File → Open**
3. 选择 `mirrorme/` 目录（即包含 `settings.gradle.kts` 的文件夹）
4. 等待 Gradle Sync 完成（首次约 10~20 分钟，自动下载依赖）

### 第三步：构建 APK

菜单栏选择：

```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

构建完成后，Android Studio 右下角会弹出提示，点击 **locate** 找到 APK 文件位置：

```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 安装到手机

### 方式 A：USB 线安装（adb）

**第一步：手机开启开发者模式**

```
设置 → 关于手机 → 连续点击「版本号」7次 → 返回设置 → 开发者选项 → 开启 USB 调试
```

**第二步：连接手机并安装**

```bash
# 确认手机已连接
adb devices

# 安装 APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 方式 B：直接传输安装

1. 将 `app-debug.apk` 文件传输到手机（微信/QQ/USB 拷贝均可）
2. 手机上打开文件管理器，找到 APK 文件
3. 点击安装（若提示"未知来源"，进入设置允许即可）

---

## 首次使用授权

App 安装后，**必须完成以下授权**，否则数据无法采集：

### 1. 使用情况访问权限（最重要）

```
设置 → 应用 → 特殊应用权限 → 使用情况访问权限 → 自镜 MirrorMe → 开启
```

或直接在 App 内按提示跳转。

### 2. 活动识别权限

App 首次启动时会弹出权限申请，点击**允许**即可（用于步数统计）。

### 3. 通知权限

允许通知，以便接收每日行为报告推送。

> 位置权限为可选项，仅用于计算位置多样性熵值（不存储具体坐标）。

---

## 项目结构

```
mirrorme/
├── app/src/main/kotlin/com/mirrorme/
│   ├── domain/          # 业务逻辑（模型、用例、接口）
│   │   ├── model/       # BehaviorEvent · DailyFeatures · PersonaProfile
│   │   ├── repository/  # BehaviorRepository 接口
│   │   └── usecase/     # 特征采集 + 人格推理
│   ├── data/            # 数据实现层
│   │   ├── local/       # Room 数据库 + 传感器采集
│   │   └── repository/  # Repository 实现
│   ├── service/         # 后台采集前台服务（每 15 分钟轮询）
│   ├── util/            # WorkManager 每日分析任务
│   ├── di/              # Hilt 依赖注入模块
│   └── ui/              # Jetpack Compose 界面
│       ├── home/        # 首页（画像卡 + 洞察 + 建议）
│       ├── persona/     # 人格详情页
│       ├── report/      # 行为数据报告页
│       └── theme/       # 深色主题（色彩/字体）
├── build_apk.sh         # 一键构建脚本
└── README.md
```

---

## 常见问题

**Q: Gradle Sync 失败，提示网络错误？**

需要访问 Google 的 Maven 仓库，请确保网络可以访问 `dl.google.com`。如在国内，可配置代理：

```bash
export JAVA_OPTS="-Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=7890"
./gradlew assembleDebug
```

**Q: 安装提示"解析包时出现问题"？**

手机 Android 版本低于 8.0（API 26），本 App 最低支持 Android 8.0。

**Q: App 安装后没有数据？**

确认已开启「使用情况访问权限」（见首次使用授权第 1 步），数据采集在后台运行，约 15 分钟后开始有数据，次日可看到完整日报。

**Q: 如何卸载？**

```bash
adb uninstall com.mirrorme.app
```

或手机上长按图标 → 卸载。

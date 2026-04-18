#!/bin/bash
# MirrorMe APK 一键构建脚本
# 用法: bash build_apk.sh
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
ANDROID_SDK_DIR="$HOME/android-sdk"
CMDLINE_TOOLS_ZIP="commandlinetools-linux-11076708_latest.zip"
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/$CMDLINE_TOOLS_ZIP"

echo "=============================="
echo " MirrorMe APK 构建脚本"
echo "=============================="

# ── Step 1: JDK ──
echo ""
echo "[1/5] 检查 JDK..."
if ! command -v java &>/dev/null; then
    echo "  安装 OpenJDK 17..."
    sudo apt-get install -y openjdk-17-jdk-headless
fi
java -version
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))

# ── Step 2: Android SDK 命令行工具 ──
echo ""
echo "[2/5] 检查 Android SDK..."
if [ ! -d "$ANDROID_SDK_DIR/cmdline-tools/latest/bin" ]; then
    echo "  下载 Android 命令行工具..."
    mkdir -p "$ANDROID_SDK_DIR/cmdline-tools"
    cd "$ANDROID_SDK_DIR/cmdline-tools"
    wget -q --show-progress "$CMDLINE_TOOLS_URL" -O "$CMDLINE_TOOLS_ZIP"
    unzip -q "$CMDLINE_TOOLS_ZIP"
    mv cmdline-tools latest
    rm "$CMDLINE_TOOLS_ZIP"
    cd "$PROJECT_DIR"
fi

export ANDROID_HOME="$ANDROID_SDK_DIR"
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"

# ── Step 3: SDK 组件 ──
echo ""
echo "[3/5] 安装 Android SDK 组件（首次需要约 10 分钟）..."
yes | sdkmanager --licenses > /dev/null 2>&1 || true
sdkmanager --install \
    "platform-tools" \
    "platforms;android-35" \
    "build-tools;35.0.0" \
    2>&1 | grep -E "Downloading|Installing|done"

# ── Step 4: local.properties ──
echo ""
echo "[4/5] 配置 local.properties..."
echo "sdk.dir=$ANDROID_SDK_DIR" > "$PROJECT_DIR/local.properties"

# ── Step 5: 构建 APK ──
echo ""
echo "[5/5] 构建 Debug APK..."
cd "$PROJECT_DIR"

# 下载 gradle wrapper jar（如不存在）
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "  下载 Gradle wrapper..."
    wget -q "https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar" \
        -O gradle/wrapper/gradle-wrapper.jar 2>/dev/null || \
    gradle wrapper --gradle-version 8.9 2>/dev/null || \
    (pip3 install gdown -q 2>/dev/null; echo "  请手动放置 gradle-wrapper.jar")
fi

chmod +x gradlew
./gradlew assembleDebug --no-daemon -q 2>&1

APK_PATH="$PROJECT_DIR/app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    echo ""
    echo "=============================="
    echo " 构建成功！"
    echo " APK 路径："
    echo " $APK_PATH"
    echo " 大小: $(du -sh "$APK_PATH" | cut -f1)"
    echo "=============================="
    echo ""
    echo "安装到已连接的手机："
    echo "  adb install $APK_PATH"
else
    echo "构建失败，请检查上方日志"
    exit 1
fi

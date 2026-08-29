# ToggleLocation - 车机定位一键开关

## 手动上传到 GitHub 的步骤

### 1. 在 GitHub 上新建仓库
- 打开 https://github.com/new
- 仓库名填 `toggle-location`（或任意）
- 选 **Public** 或 **Private** 都行
- **不要**勾选 "Add a README file"（我们自己上传）
- 点 Create repository

### 2. 按下面的文件清单，在网页上逐个上传
进仓库 → Add file → Upload files → 把对应文件拖进去，注意**保持目录结构**。

### 3. 触发构建
上传完后，进仓库 **Actions** 标签 → 选 "Build APK" → 右侧 Run workflow → main 分支 → Run
跑完（约 3-6 分钟）在下方 Artifacts 里下载 `app-debug.apk`

### 4. 安装到车机
甲壳虫 / adb：
```
adb push app-debug.apk /sdcard/downloads/
adb shell su -c "pm install /sdcard/downloads/app-debug.apk"
```
或直接用甲壳虫的 "安装 APK" 功能选这个文件。

### 5. 使用
- 车机 UI34 桌面会出现"定位开关"图标（因为 Manifest 声明了 LAUNCHER，和普通 App 一样自动出图标）
- 点一下 → 按钮文字在"已开/已关"间切换，同时系统定位模式 3↔0 翻转
- 首次运行 Magisk 会弹超级用户授权 → 允许（包名 com.example.toggleloc）

## 文件清单
```
ToggleLocation/
├── .github/workflows/build.yml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── README.md
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/example/toggleloc/MainActivity.kt
        └── res/
            ├── layout/activity_main.xml
            └── values/strings.xml
```

> 说明：本项目**不依赖 Termux**，直接用 `su -c settings put secure location_mode` 切换。
> 车机 Android 10，需要 Magisk root（已通过 com.android.shell 授权）。

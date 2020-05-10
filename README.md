# 自定义 View 库 —— VcView

[![](https://jitpack.io/v/zhengweichao/view.svg)](https://jitpack.io/#zhengweichao/view)

自定义View依赖库,收藏大量常用自定义View,持续更新中……

# 集成

**step 1.  项目的 build.gradle 中添加 maven 设置**

```
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

**step 2. 模块的 build.gradle 中添加依赖**

```
dependencies {
    // ...
    implementation 'com.github.zhengweichao:view:latest.release'
}
```

# 感谢

目前包括原创自定义View如下:

- ScienceBoardView : 科技感仪表盘
- DrawableTextView ：可自由控制 drawable 大小的 TextView

除原创的自定义View之外,其它均参考或者来自其他大佬的博文或Github，在此向各位大佬表示敬意与感谢。
参考内容如下:

- https://github.com/woxingxiao/DashboardView
- https://github.com/zhlucky/SaleProgressView
- https://github.com/chrisbanes/PhotoView
- https://www.jianshu.com/p/752f71743007
- https://github.com/dalong982242260/AndroidDashboardView
- https://github.com/baoyachi/StepView
- https://blog.csdn.net/xiao_nian/article/details/83141422
- https://github.com/HeZaiJin/SlantedTextView

本库只收藏轻量级的小型自定义View，以我自己的原创，或者网上的闲散资源整理得到的内容 为主。
除此之外，还有很多优秀的较为复杂的自定义View库，无法一一收录。在这里一并推荐给大家：

- https://github.com/scwang90/SmartRefreshLayout

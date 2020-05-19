# 楚楚街通用下载模块

为解决使用系统DownloadManager的短板（不支持https、各机型上兼容、不支持多线程下载等），故基于RxJava打造的下载工具, 支持多线程下载和断点续传,使用Kotlin编写

## 核心技术

1. Okhttp
2. Retrofit
3. RxJava&RxAndroid
4. Kotlin


## 优势
1. 智能判断服务器是否支持断点续传并适配相应下载方式；
2. 智能判断同一地址对应的文件在服务端是否有改变并重新下载；
3. 支持多线程下载，可设置下载线程数；
4. 支持下载状态、下载进度监听；
5. 支持在Service中下载文件，内置DownloadService


## 使用方式

1. 添加Gradle依赖

```gradle
dependencies{
    compile 'zlc.season:rxdownload3:x.y.z'
}
```

2. 配置权限

```xml

<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

```

> **注意: Android 6.0 以上还必须申请运行时权限, 如果遇到不能下载, 请先检查权限**

## 使用

1. 创建下载任务，并且接受下载的状态

```kt
val disposable = RxDownload.create(mission)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { status ->
                    setProgress(status)
                    setActionText(status)
                }
```

> 注意：下载状态的接收也是在这里进行，接收到的status会根据不同的下载状态自动更新。 重复调用不会导致任务多次创建，因此可以在任何想要接收状态的地方调用该方法来接收下载的状态。

2. 开始下载

```kt
RxDownload.start(mission).subscribe()
```

3. 停止下载

```kt
RxDownload.stop(mission).subscribe()
```

**提示: 创建任务是一个异步操作, 因此如果需要创建成功后立即开始下载有以下方式：**

+ 在create()完成的回调中进行，如下

```kt
val disposable = RxDownload.create(mission)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { status ->
                    //开始下载
                    RxDownload.start(mission).subscribe()
                    setProgress(status)
                    setActionText(status)
                }
```

+ 或者启用autoStart配置，当autoStart处于开启状态时，create任务完成以后就会自动start开始下载

```kt
DownloadConfig.Builder.create(context)
                  .enableAutoStart(true)
                  ...
                  
                  
DownloadConfig.init(builder)
```

## 配置

在APP启动时添加您的配置：

```
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val builder = DownloadConfig.Builder.create(this)
                .enableDb(true)
                .enableNotification(true)
				...

        DownloadConfig.init(builder)
    }
}
```
拥有丰富的配置选项满足您的需求:

```
DownloadConfig.Builder.create(this)
                .enableAutoStart(true)              //自动开始下载
                .setDefaultPath("custom download path")     //设置默认的下载地址
                .enableDb(true)                             //启用数据库
                .setDbActor(CustomSqliteActor(this))        //自定义数据库
                .enableService(true)                        //启用Service
                .enableNotification(true)                   //启用Notification
                .setNotificationFactory(NotificationFactoryImpl()) 	    //自定义通知
                .setOkHttpClientFacotry(OkHttpClientFactoryImpl()) 	    //自定义OKHTTP
                .addExtension(ApkInstallExtension::class.java)          //添加扩展
```

## 扩展

```kt
class CustomExtension:Extension {
    override fun init(mission: RealMission) {
        //Init
    }

    override fun action(): Maybe<Any> {
        //Your action
    }
}
```

## 混淆

```
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
```


### 参考资料

[RxDownload](https://github.com/ssseasonnn/RxDownload)

[OkRetrofit](https://github.com/Tailyou/OkRetrofit)
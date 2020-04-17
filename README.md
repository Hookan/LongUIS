# LongUIS

这是一个基于SpigotAPI的插件。

它主要是面向开发者，提供了一些接口用于与客户端的LongUI mod交互。

采用WPFPL开源，也就是说，你可以对源码做任何的事情。

本插件在开发时参考了[BukkitAPI服务器插件开发教程 | Bukkit/Spigot等适用](https://www.mcbbs.net/thread-808820-1-1.html)，以及[1.13+ 中 Forge 与 Bukkit 的通信](https://www.mcbbs.net/thread-873219-1-1.html)。

由于本人并不是很熟悉插件开发，所以这个插件写的很差，如果你有改进意见，欢迎发 issue 或者 Pull Request 。

请注意： LongUIS 的版本号与 LongUI 并不一一对应。

下载地址：[https://ci.qwq.cafe/job/LongUIS/ws/build/libs/](https://ci.qwq.cafe/job/LongUIS/ws/build/libs/)

## 如何在插件开发中使用 LongUIS 

请在你的 `build.gradle` 中添加

```groovy
repositories {
    ...
    maven { url 'https://ci.qwq.cafe/maven/' }
}

dependencies {
    ...
    implementation 'cafe.qwq:LongUIS:<LongUIS的版本>'
}
```


如果你使用 maven ，请参考 gradle 配置方法自行配置。

如果你既不用 gradle 也不用 maven ，那请你从上方的 LongUIS 下载地址下载最新的 LongUIS 及其源码，然后导入你的 IDE 。


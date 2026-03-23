- [English](README-en_us.md)
## 简介
这是一个简单的客户端模组，使玩家能够亲亲实体（冒爱心并播放音效）。

触发方式为潜行右击实体，按下绑定按键也可以触发（默认 F7）。
![kiss the player](https://cdn.modrinth.com/data/cached_images/ddbde5d1bdc6d5772e3338dcabd0d0286a290ce0.png)
## 服务端支持
当服务器安装了模组或插件时，安装模组的玩家间能互相看见效果。
## 命令
```/kissmod-rightclick```  切换是否启用潜行右击触发；

```/kissmod-rightclick true```  启用；

```/kissmod-rightclick false```  禁用。

```/kissmod-proxlib```  切换是否启用邻近通信；

```/kissmod-proxlib true```  启用；

```/kissmod-proxlib false```  禁用。
## 配置
添加[模组菜单](https://modrinth.com/mod/modmenu)以及[Cloth Config API](https://modrinth.com/mod/cloth-config)时可通过模组菜单打开配置页面修改：
### 通用设置
- 是否潜行右键触发；
- 是否给别人看亲亲；
- 是否看别人的亲亲；
- 长按触发之间的冷却；
- 是否详细日志；
- 是否邻近通信 **（开启后无需服务端安装就能让附近玩家看到你的亲亲）；**  
> **警告：可能被反作弊认为异常而误判，有被踢出或封禁的风险，作者不对此负责。**
- 列表是否白名单；
- 邻近通信服务器地址列表。
### 音效设置
- 是否播放音效；
- 音效音量；
- 音效音调。
### 粒子设置
- 爱心粒子数量；
- 粒子生成中心 XYZ 偏移；
- 粒子随机生成最大 XYZ 偏移。
## 更多支持的版本（基于0.5版本KissMod）
这是一个移植版 https://github.com/Xiaoyu-2009/kiss-mod-transplant

非官方版 https://modrinth.com/mod/kiss-mod-unofficial
## 插件的开源仓库链接
https://github.com/chicken-awa/kiss-mod-Plugin
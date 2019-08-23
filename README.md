# NevolutionXposed
通过xposed实现nevolution，希望可以解决持续性通知无法修改、微信通知替换有延迟和某些系统下服务不够稳定（被杀）的问题

鸣谢[女娲石](https://github.com/Nevolution/sdk)

## 功能模块

### 微信通知优化 WeChatDecorator
- [x] 将通知优化为会话形式
- [x] 增加通知直接回复功能
- [x] 清除通知直接标记会话已读
- [x] 会话嵌入图片，并可以放大
- [x] 修正MIUI开发版下，会话形式通知未展开为空白的问题 [MIUI]
- [ ] 撤回通知优化
- [ ] 微信会话中的表情和带预览图的链接嵌入为图片

鸣谢[女娲石 微信通知优化](https://github.com/Nevolution/decorator-wechat)

### 小米推送通知优化 MIUIDecorator [MIUI]
- [x] 替换小米推送通知默认的彩色APP为安卓标准的单色小图标（smallIcon）
- [x] 支持小米推送规定的内嵌小图标
- [x] 也支持通过app图标自动生成单色小图标
- [x] 通过app图标自动选择颜色对通知进行自动染色
- [ ] 生成单色小图标时，使用apk原始图标而非miui的主题图标

### 媒体通知优化 MediaDecorator
- [x] 将某些系统劣化的媒体通知改回Android O样式
- [x] （相对MusicNotification）支持未展开视图完整显示
- [ ] 展开收起动画
- [ ] 展开收起箭头

鸣谢[MusicNotification](https://github.com/Qiwu2542284182/MusicNotification)

### 待加入
- [ ] 大字体通知优化 BigTextDecorator
- [ ] 通知堆栈优化 StackDecorator
- [ ] 下载通知优化 [MIUI]

### 配置界面
- [x] 打开关闭以上模块，需要重启
- [ ] 打开关闭以上模块，无需重启
- [ ] 实现原版女娲石中的动态选择通知匹配功能模块

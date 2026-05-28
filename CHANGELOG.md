# Changelog

## v1.2.0

### English

#### New Features

1. Added smithing table and anvil mode switching to the manual crafting workspace.
2. Added a pattern multiplier for processing pattern encoding, with a client config option to disable it.
3. Added a wireless terminal settings screen for toggling selected client-side features directly in-game.
4. Added an option to prioritize bookmarked ingredients during recipe pulling, configurable via client config.

#### Improvements

1. Improved the English localization to better fit the UI layout.
2. Added Traditional Chinese, Japanese, and Russian localization files (machine translated).
3. Added pinyin search compatibility for the pattern management provider search box.

#### Fixes

1. Fixed latency when placing a network tool into the toolkit.
2. Fixed latency when opening JEI multi-candidate and tag views from the terminal with this mod installed.
3. Fixed the inability to use middle-click quantity setting directly in the processing pattern encoding area.
4. Fixed shift-quick-move inserting items into toolkit and cosmetic armor slots even when those UIs were not open.
5. Fixed missing low-power warning popups when the terminal ran out of charge.
6. Fixed terminal hotkeys still triggering while the two pattern management text fields were focused.
7. Fixed clearing the pattern encoding area not restoring the blank pattern item.
8. Fixed JEI/EMI recipe pull previews incorrectly treating already stored network items as blue highlighted existing pattern items.
9. Fixed EMI crafting recipe pulls not following the actual visible input slot order of the current UI.
10. Fixed JEI and EMI shift-pull of one full set closing the terminal immediately and failing the transfer.

### 中文

#### 新功能

1. “手动合成区”新增切换锻造台和铁砧模式；
2. 新增样板倍增器，可对样板编码区处理样板进行倍增，可在客户端配置文件中关闭；
3. 新增“无线终端设置”界面，可直接开关部分客户端设置功能；
4. 增加拉取配方时优先使用书签栏物品的功能，可在配置文件中开关；

#### 优化

1. 优化英语语言文件，使其更贴合界面布局；
2. 新增繁体中文、日语和俄语文件（均为机翻）；
3. 样板管理区供应器搜索框兼容拼音搜索；

#### 修复

1. 修复将网络工具放入工具包时延迟卡顿问题；
2. 修复安装该模组时从终端打开 JEI 多候选/标签界面时延迟卡顿问题；
3. 修复样板编码区处理样板无法直接使用鼠标中键设置物品数量的 bug；
4. 修复 `Shift` 快速移动时，即使未打开对应界面，终端仍把物品塞进工具包槽和装饰盔甲槽的 bug；
5. 修复终端在电量不足时不自动弹出电源不足提示的 bug；
6. 修复样板管理两个输入框聚焦时终端快捷键仍生效的 bug；
7. 修复样板编码区点击清除后未还原成空白样板的 bug；
8. 修复 JEI/EMI 拉取配方样板时将网络已存在物品作为已有样板蓝色高亮的问题；
9. 修复 EMI 拉取合成配方物品时未按照当前界面里实际显示出的输入槽顺序来取的 bug；
10. 修复 JEI 和 EMI 按住 `Shift` 拉取一组物品时直接关闭终端界面且拉取失败的 bug；

## v1.1.1

### English

1. Fixed a bug where the resonating pattern cache still showed the sort button after closing the UI when `Inventory Tweaks - ReFoxed` was installed.
2. Fixed a bug where the resonating/overload encoder button was not hidden and textures were missing when `AE2 Lightning Tech` and `AE2 Crystal Science` were not installed.
3. Improved some stutter and latency issues a bit (hopefully helpful).
4. Toolkit now supports cross-terminal usage and data persistence for the same player.
5. Fixed a bug where shift-clicking curios would still send them directly into Curios even when the terminal Curios UI was not open. When the terminal Curios UI is open, shift-clicking a curio now prioritizes the player inventory first, and falls back to the terminal if full.
6. Fixed a bug where the toolkit hotkey could not open the toolkit when the terminal was equipped in Curios.
7. Fixed a bug where the pattern management provider display mode, show/hide slots toggle, and search mode toggle were reset after reopening the terminal.
8. Fixed a bug where the pattern upload enable button style did not match its text/state.
9. Added a client config option to choose whether failed pattern uploads return to the pattern editor slot or the pattern cache slot. Default is the pattern cache slot.
10. Moved the pattern management shift quick move option to client config.

### 中文

1. 修复安装 `Inventory Tweaks - ReFoxed` 模组时谐振样板缓存区在关闭界面后仍出现整理图标的 bug；
2. 修复未安装 `AE2 Lightning Tech`、`AE2 Crystal Science` 模组时过载谐振编码器按钮不隐藏，材质贴图缺失的 bug；
3. 优化了些许卡顿与延迟（可能有用？）；
4. 工具包支持同玩家跨终端使用与数据保存；
5. 修复未打开终端饰品栏时 `shift` 点击仍会直接放入饰品栏中的 bug，打开终端饰品栏界面 `shift` 点击饰品时优先放入玩家物品栏，已满则放入终端；
6. 修复当终端放入饰品栏时无法使用快捷键打开工具包的 bug；
7. 修复样板管理区的切换供应器显示模式、显示/隐藏槽位、切换搜索模式按钮重新打开终端后重置状态的 bug；
8. 修复启用上传样板功能按钮样式与文本不匹配的 bug；
9. 增加客户端可配置上传样板失败后回退到样板编辑槽或样板缓存区，默认回退到样板缓存区；
10. 将样板管理 `shift` 快速移动配置更改为客户端配置。

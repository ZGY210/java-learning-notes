# java-learning-notes

一个 Java 后端方向学生（2026 级本科在读）的 **12 个月系统学习仓库**。

目标明确：**2027 年 3 月 1 日起投递实习，2027 年 8 月前拿到暑期实习 offer。**
本仓库记录从 Java SE 到 Spring Boot 微服务的全部练习代码、手写实现与每日验收结果。

> 这个仓库不只存代码，也存**可验证的进度**。每一行打勾的内容，都能在本仓库里找到对应的 commit 和可以跑出结果的代码。

---

## 目录

- [这个仓库在做什么](#这个仓库在做什么)
- [技术栈](#技术栈)
- [目录结构](#目录结构)
- [进度看板](#进度看板)
- [如何运行](#如何运行)
- [每日纪律](#每日纪律)

---

## 这个仓库在做什么

三个阶段的代码都放在这里，按 Maven 模块的目录习惯组织，后续可以直接升级为 Spring Boot 项目。

| 阶段 | 时间 | 内容 |
| --- | --- | --- |
| **P1 · Java SE** | 2026-10-14 → 2026-12-20 | 集合、异常与 IO、多线程与 JUC、反射与动态代理、JVM 入门、网络编程 |
| **P2 · 数据库与 Web** | 2026-12-21 → 2027-02 | MySQL（索引、事务、锁）、JavaWeb、Spring Boot 3 |
| **P3 · 项目实战** | 2027-02 → 2027-08 | 项目一：校园二手交易平台（单体）<br>项目二：AI 知识库问答平台（微服务 + AI 集成） |

手写实现（不依赖 JDK 现成类）是这个仓库的核心练习方式，例如 `MyArrayList`、`MyHashMap`、JSON 序列化工具、动态代理耗时切面等。

---

## 技术栈

**已使用：**

| | | |
| --- | --- | --- |
| Java 17 | Maven 3.9 | Git |

**规划中：**

| | | |
| --- | --- | --- |
| MySQL | Redis | Spring Boot 3 |
| MyBatis-Plus | Docker | RocketMQ |

> 本机 JDK 为 21，但 `maven.compiler.release` 锁定 **17**。原因：课程按 17 讲，企业主流（含 Spring Boot 3.2）也锁 17。用高版本 JDK 编译低版本目标，可以提前发现自己误用了高版本独有 API。

---

## 目录结构

```
java-learning-notes/                       ← 本仓库根目录
├── README.md                  # 你正在看的这一页
├── .gitignore
│
├── notes/                     # 📓 笔记：每个知识点用自己的话写一遍
│   ├── README.md                  # 笔记骨架（三问内核）+ 命名约定
│   ├── 待补-知识漏洞.md            # ⭐ 闭卷答不出来的点，考前只看这一页
│   ├── collections/               # 集合框架
│   └── java-basics/               # 面向对象 / String / 异常 / IO / 泛型 / 反射
│
├── journal/                   # 📅 日志：每天 3 行 + 每周复盘
│   ├── README.md                  # 为什么这个目录对求职最重要
│   └── 2026-10-13-D1.md           # 每日一篇
│
├── leetcode/                  # 🧩 题解：命名 0001-两数之和.md
│   ├── README.md                  # 60 题进度表 + 三条铁律
│   └── _模板.md                   # 五段式题解模板
│
├── pom.xml                    # Maven 坐标与编译配置（release 17）
├── mvn-local.cmd              # 复用 IDEA 自带 Maven，无需单独安装
├── verify.cmd                 # 一键编译 + 跑全部自测（每天 commit 前跑）
├── push-to-github.ps1         # 首次推送 + 失败分类排查（必须存为 UTF-8 带 BOM，见文末）
└── src/main/java/
    ├── api/                   # 常用 API 练习
    │   ├── RegexUtil.java         # 正则：校验 / 提取 / 分组 / 脱敏
    │   ├── MoneyUtil.java         # BigDecimal 金额计算与分摊
    │   └── DateUtil.java          # java.time、时区换算、线程安全对比实验
    └── practice/
        └── mycollection/      # 手写集合框架
            ├── MyArrayList.java        # 手写 ArrayList（1.5 倍扩容 + fail-fast）
            └── MyArrayListTest.java    # 30 项自测，全绿才算过关
```

> **「代码 + 笔记 + 日志 + 题解」四合一**是刻意的：面试官点开这个仓库，能看到的不只是代码，而是一条**持续 12 个月、每天都有痕迹的证据链**。对双非学生来说，这条链比学历更能说明问题。
>
> 后续会新增 `src/main/java/` 下的 `io/`、`thread/`、`reflect/` 等包，以及 P2 起的 Spring Boot 项目。

---

## 进度看板

> ⚠️ **状态口径**：`⬜ 未学` → `🔄 学习中` → `✅ 已掌握`。
> **「仓库里有代码」≠「已掌握」。** `api/` 下那三个类目前是**参考答案**（先于视频写好），你必须关掉它们从头重写一遍才算 ✅。

### P1-a · 常用 API + 常见算法收尾（2026-10-14 → 10-26）

| 视频集 | 内容 | 状态 | 验收物 |
| --- | --- | :---: | --- |
| 161–162 | BigInteger / BigDecimal | 🔄 | `api/BigIntegerDemo.java`（100! 与 long 溢出对比） |
| 163–168 | 正则表达式 | ⬜ | **重写** `api/RegexUtil.java` |
| 169–173 | 日期时间（JDK7 / JDK8） | ⬜ | **重写** `api/DateUtil.java` + `SimpleDateFormat` 线程不安全复现 |
| 174 | 包装类 | ⬜ | `api/BoxingDemo.java` 六组实验 |
| 176–183 | 常见算法（查找 / 排序 / Arrays / lambda） | ⬜ | `practice/algo/` 四个手写文件 |
| — | 12 道查漏诊断题（10/24 闭卷） | ⬜ | 得分 ≥ 9 |
| — | 集合预热件（**抢跑**） | 🎁 | `MyArrayList`（含 `modCount`，30 项自测全绿）**待读懂** |

### P1-b · 集合框架（2026-10-26 → 11-30）

| 视频集 | 内容 | 状态 | 验收物 |
| --- | --- | :---: | --- |
| 184–198 | 集合进阶·单列（Collection / List / 泛型 / Set / 红黑树） | ⬜ | 集合面试 15 问能口述 |
| 199–223 | 集合进阶·双列（Map / **HashMap 源码** / TreeMap） | ⬜ | 手写 `MyHashMap`（扰动函数 + 0.75 负载因子 + 2 倍扩容） |

### P1-c / P1-d · 后续模块

| 模块 | 状态 | 验收物 |
| --- | :---: | --- |
| 不可变集合 + Stream + 方法引用 | ⬜ | 用 Stream 重写自测卷第 8 题 |
| 异常 + File + IO 流 | ⬜ | 四种拷贝方式效率对比实测表 |
| 多线程与 JUC | ⬜ | 自定义线程池 + `CompletableFuture` 并行聚合 |
| 网络编程 | ⬜ | 口述「输入 URL 到页面显示」全链路 |
| 反射与动态代理 | ⬜ | 手写 JDK 动态代理耗时切面 |
| JVM 入门 | ⬜ | `jstack` 死锁分析记录 |

### 交付节奏

| 节点 | 日期 | 状态 |
| --- | --- | :---: |
| 常用 API + 算法收尾 | 2026-10-26 | 🔄 进行中 |
| **集合框架开始** | **2026-10-26** | ⬜ |
| Java SE 收口 | 2026-12-20 | ⬜ |
| 项目一上线 | 2027-02-14 | ⬜ |
| **开始投递** | **2027-03-01** | ⬜ 硬约束 |
| 项目二 v2 完成 | 2027-05-23 | ⬜ |
| 八股 + 算法冲刺 | 2027-05-24 → 07-31 | ⬜ |

---

## 如何运行

### 方式一：一键验收（推荐，不需要网络）

双击或命令行运行：

```
verify.cmd
```

它会编译全部源码、跑 `MyArrayListTest`，再依次运行各个 demo。最后看到 `ALL GREEN` 才算过。

### 方式二：Maven（需要网络，首次会下载依赖）

本项目不要求单独安装 Maven，`mvn-local.cmd` 会自动复用 IDEA 自带的 Maven：

```
mvn-local.cmd -v                 # 查看版本
mvn-local.cmd -o compile         # 离线编译
mvn-local.cmd test               # 跑单元测试（接入 JUnit 后）
```

> 离线参数 `-o` 在网络不稳时很有用，它强制 Maven 只用本地仓库。

### 方式三：IDEA

用 IDEA 打开 `pom.xml` 所在目录（`File → Open`），等待索引完成后右键任意类 Run。

---

## 每日纪律

三条规则，写在仓库里就是为了让中断有成本：

1. **可以中断，但绝不能连续断 2 天。**
2. **每周至少 5 个 commit。** 少于 5 个，说明这一周是「假学习」。
3. **每天结束时 `verify.cmd` 必须是绿的。** 跑不出结果的代码不算完成。

---

## 时间投入记录

| 日期 | 投入 | 内容 |
| --- | --- | --- |
| 2026-10-14 | 2.5 h | 正则 / BigDecimal / 日期时间三个工具类 + MyArrayList 补 modCount，30 项自测全绿 |

---

## 踩过的坑（写下来，防止再犯）

### 1. `.ps1` 文件没有 BOM，脚本被「打印」而不是被「执行」

**症状**：运行 `push-to-github.ps1`，终端里刷出一堆乱码，比如 `閽ュ彛 €?java-learning-notes`，而且能看见 `Write-Host` 本身被当成普通文本输出，脚本根本没跑完。

**原因**：Windows PowerShell **5.1** 读取不带 BOM 的 `.ps1` 时，会按系统 ANSI 编码（中文系统 = GBK）解码。文件其实是 UTF-8，于是中文变乱码；更致命的是乱码字节里混进了 `"` `'` 之类的字符，**把引号配对搞坏，整个脚本解析失败**，PowerShell 就把剩下的内容原样打出来。

**解法**：把 `push-to-github.ps1` 存为 **UTF-8 带 BOM**。BOM 是文件开头的三个字节 `EF BB BF`，PowerShell 5.1 和 7 都能靠它正确识别编码。

**怎么检查**：在 PowerShell 里执行

```powershell
$b = [System.IO.File]::ReadAllBytes(".\push-to-github.ps1")
if ($b[0] -eq 0xEF -and $b[1] -eq 0xBB -and $b[2] -eq 0xBF) { "有 BOM，正常" } else { "缺 BOM，要重存" }
```

**注意**：用编辑器或脚本改动这个文件后，BOM 可能会被去掉，改完要重新检查一次。`.cmd` 文件不受影响（`verify.cmd`、`mvn-local.cmd` 全部是纯 ASCII，没有中文）。

---

*本仓库持续更新至 2027 年 8 月。*

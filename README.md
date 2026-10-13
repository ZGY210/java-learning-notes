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
p1-javase/
├── pom.xml                  # Maven 坐标与编译配置（release 17）
├── mvn-local.cmd            # 复用 IDEA 自带 Maven，无需单独安装
├── verify.cmd               # 一键编译 + 跑全部自测（每天 commit 前跑）
└── src/main/java/
    ├── api/                 # 常用 API 练习
    │   ├── RegexUtil.java       # 正则：校验 / 提取 / 分组 / 脱敏
    │   ├── MoneyUtil.java       # BigDecimal 金额计算与分摊
    │   └── DateUtil.java        # java.time、时区换算、线程安全对比实验
    └── practice/
        └── mycollection/    # 手写集合框架
            ├── MyArrayList.java      # 手写 ArrayList（1.5 倍扩容 + fail-fast）
            └── MyArrayListTest.java  # 30 项自测，全绿才算过关
```

后续会新增 `collections/`、`io/`、`thread/`、`reflect/`、`leetcode/` 等包。

---

## 进度看板

### P1 · Java SE

| 模块 | 状态 | 验收物 | 完成日期 |
| --- | :---: | --- | --- |
| 常用 API：正则表达式 | ✅ | `api/RegexUtil.java` | 2026-10-14 |
| 常用 API：BigDecimal | ✅ | `api/MoneyUtil.java` | 2026-10-14 |
| 常用 API：日期时间 | ✅ | `api/DateUtil.java`（含并发实验） | 2026-10-14 |
| 手写 ArrayList | ✅ | `MyArrayList` + 30 项自测全绿 | 2026-10-14 |
| 常用 API：包装类与综合练习 | ⬜ | — | — |
| 集合进阶：单列集合与数据结构 | ⬜ | — | — |
| 集合进阶：双列集合与 HashMap 源码 | ⬜ | `MyHashMap` | — |
| Stream 流与方法引用 | ⬜ | — | — |
| 异常与 IO 流 | ⬜ | 词频统计工具 | — |
| 多线程与 JUC | ⬜ | 自定义线程池 + `CompletableFuture` 并行聚合 | — |
| 反射与动态代理 | ⬜ | 手写 JSON 工具 + 耗时切面 | — |
| JVM 与网络 | ⬜ | `jstack` 死锁分析记录 | — |

### 交付节奏

| 节点 | 日期 | 状态 |
| --- | --- | :---: |
| Java SE 收口 | 2026-12-20 | 🔄 进行中 |
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

*本仓库持续更新至 2027 年 8 月。*

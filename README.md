<div align="center">

# 🖳 虚拟机

### QEMU 级完整虚拟机 · ARM/x86 镜像运行 · VNC 显示 · 快照恢复 · 独立 Shizuku 授权

[![Plugin CI](https://github.com/abyss-tool/abyss-plugin-virtual-machine/actions/workflows/plugin-ci.yml/badge.svg?branch=main)](https://github.com/abyss-tool/abyss-plugin-virtual-machine/actions/workflows/plugin-ci.yml)
![Version](https://img.shields.io/badge/version-0.1.0-7EC8FF)
![Category](https://img.shields.io/badge/分类-沙箱与多开-4FD8EB)
![Status](https://img.shields.io/badge/状态-框架预览-1A222D)

</div>

---

> 深渊工具（[abyss-tool/Abyss-Tools](https://github.com/abyss-tool/Abyss-Tools)）的**引擎级大插件**（v0.8.0 插件矩阵新成员）—— 在手机上运行完整操作系统（Alpine / Debian / Windows / DOS）。本仓库为**插件框架期骨架**：manifest（含独立 Shizuku 授权声明）/ 补丁通道 / CI 门禁已就绪，虚拟化引擎按路线图实现。

## 为什么是插件？

整机虚拟机是**自带完整引擎**的能力中最重的一档：QEMU 用户态仿真（TCG，免 KVM）+ 系统镜像管理 + 显示管线 —— 这是 v0.8.0 大内置化后「只有引擎级能力才插件化」分界线的三个代表之一（另两个：应用构建器 / 病毒拦截）。

## 插件信息

| 字段 | 值 |
| --- | --- |
| 插件 ID | `abyss-plugin-virtual-machine` |
| 分类 | 沙箱与多开（sandbox） |
| 版本 | v0.1.0（框架预览） |
| 最低宿主 | App v0.8.0 / HOST_API v1 |
| 仓库 | [abyss-tool/abyss-plugin-virtual-machine](https://github.com/abyss-tool/abyss-plugin-virtual-machine) |
| 补丁通道 | [`patches/index.json`](patches/index.json) |

## 权限需求

- `INTERNET`（镜像下载与 VNC 本地回环）
- `FOREGROUND_SERVICE`（虚拟机前台保活）
- `WAKE_LOCK`（长时间运行的唤醒锁）

## Shizuku 独立授权（重要）

QEMU 用户态引擎在无 Root 设备上需更大内存映射与进程优先级，经
Shizuku（ADB shell 上下文）拉起以绕过普通应用进程的 RLIMIT_MEMLOCK
与 oom_adj 限制。本插件为独立 APK，按 Shizuku per-app 授权机制需
单独授权一次。

## 路线图

- [ ] v0.2 QEMU 用户态引擎拉起（TCG，免 KVM）与镜像管理
- [ ] v0.3 VNC 显示管线 + 输入路由（触屏 → PS/2 / USB Tablet）
- [ ] v0.4 镜像市场：Alpine / Debian / DOS / Windows XP 模板
- [ ] v0.5 快照 / 挂起恢复与性能档位

详见 [ROADMAP.md](ROADMAP.md)。

---

## ⚠️ 归档公告（2026-11-22 · ADR-100 D-4 / HUB-014）

本仓库已**归档（只读）**：QEMU 整机能力收编为 **abyss-plugin-linux** 的
`QEMU_VM` 执行域（完整系统 · 自带 Linux 内核 · TCG arm64 · qcow2 镜像 ·
VNC 回环 · 快照）。

- **能力去向**：`QEMU_VM 执行域 → [abyss-plugin-linux](https://github.com/abyss-tool/abyss-plugin-linux)`
- **规格承接**：原 N-VM-001~005 由 `11-Linux插件解耦/LIN-002`（QEMU 完整
  系统执行域）与 `LIN-003`（镜像资产管理）承接；原 DC-012 由
  `11-Linux插件解耦/DC-102`（abyss-asset-v1）承接
- **授权影响**：原「虚拟机单独 Shizuku 授权」取消 —— 收敛为 Linux 插件
  **唯一一次授权**（PRoot/QEMU 引擎共用 uid 2000 上下文）
- **补丁通道**：`patches/index.json` 保持现状不再滚动；旧版本用户可迁移
  至 Linux 插件获取等价能力（含镜像市场模板与快照档位）
- **纪律**：归档能力不得复活为插件（SRS-004 FR-005）；本仓库按
  HUB-014 历史存档保留（不再接受变更与补丁）

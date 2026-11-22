package com.abyss.plugin.virtualmachine

import com.abyss.plugin.virtualmachine.capability.VmCapability

/**
 * HOST_API v1 宿主入口（M1 脚手架：能力探测切片 —— 全量 QEMU 引擎为 M2
 * N-VM-001，本轮明确不做）。
 */
class VirtualMachinePlugin {

    val id: String = "abyss-plugin-virtual-machine"

    val minHostApi: Int = PluginContract.HOST_API_VERSION

    fun describeCapabilities(): List<String> = listOf(
        "QEMU 运行前提能力探测（ABI / KVM / memlock / 存储头寸，纯 JVM 判定）",
        "ARM on ARM 架构匹配建议（TCG 慢路径规避）",
        "QEMU 用户态引擎、镜像管理、VNC 显示随 M2（N-VM-001）落地",
    )
}

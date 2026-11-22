package com.abyss.plugin.virtualmachine.capability

/**
 * QEMU 运行前提能力探测（M1 脚手架 —— N-VM-001 切片，M2 前不做全量）。
 *
 * 探测要素（纯 JVM 判定，数据经字符串快照注入 —— Android 侧读取后传入，
 * 指令/读取编排纯 JVM 可单测）：
 *  - **ABI**：ARM on ARM 优先（TCG 纯软件仿真，x86 客户机在 ARM 设备上是
 *    慢路径 —— 规格风险清单第一项）；
 *  - **KVM**：/dev/kvm 存在性（用户态 TCG 路线不依赖，存在即注明可用）；
 *  - **memlock 上限**：RLIMIT_MEMLOCK 不足是 16GB 内存映射的直接障碍，
 *    规格缓解 = Shizuku 上下文拉起（每进程上限放宽）；
 *  - **存储余量**：镜像数 GB（分卷 + 断点续传随 v0.2 管线）。
 *
 * fail-closed：探测缺失/解析失败 → NOT_RUN（不猜测、不宣称可用）。
 */
object VmCapability {

    enum class AbiTier { ARM64_HOST, ARM32_HOST, X86_64_HOST, X86_HOST, UNKNOWN }

    enum class ProbeState { NOT_RUN, FEASIBLE, CONSTRAINED, INFEASIBLE }

    data class Snapshot(
        val abiList: List<String>,
        val kvmDevicePresent: Boolean,
        val memlockLimitBytes: Long?,
        val availableStorageBytes: Long?,
    )

    /** ABI 档位判定（abiList 由 Build.SUPPORTED_ABIS 注入）。 */
    fun abiTier(abiList: List<String>): AbiTier = when {
        abiList.any { it == "arm64-v8a" } -> AbiTier.ARM64_HOST
        abiList.any { it == "armeabi-v7a" } -> AbiTier.ARM32_HOST
        abiList.any { it == "x86_64" } -> AbiTier.X86_64_HOST
        abiList.any { it == "x86" } -> AbiTier.X86_HOST
        else -> AbiTier.UNKNOWN
    }

    /** 架构匹配建议（ARM on ARM 优先 —— TCG 慢路径规避）。 */
    fun recommendedGuest(host: AbiTier): String = when (host) {
        AbiTier.ARM64_HOST, AbiTier.ARM32_HOST -> "ARM 客户机（Alpine ARM）—— 同架构 TCG 路径最优；x86 客户机为慢路径，仅供实验"
        AbiTier.X86_64_HOST, AbiTier.X86_HOST -> "x86 客户机（DOS / 轻量 x86 系统）"
        AbiTier.UNKNOWN -> "未探测 —— 不做建议"
    }

    /**
     * 综合探测结论（fail-closed：ABI 未知或存储未知 → NOT_RUN）。
     * CONSTRAINED = 可运行但需 Shizuku 放宽 memlock / 受 TCG 慢路径限制。
     */
    fun probe(snapshot: Snapshot): ProbeState = when {
        abiTier(snapshot.abiList) == AbiTier.UNKNOWN -> ProbeState.NOT_RUN
        snapshot.availableStorageBytes == null -> ProbeState.NOT_RUN
        snapshot.availableStorageBytes < MIN_IMAGE_HEADROOM_BYTES -> ProbeState.INFEASIBLE
        snapshot.memlockLimitBytes != null && snapshot.memlockLimitBytes < MIN_MEMLOCK_BYTES ->
            ProbeState.CONSTRAINED
        else -> ProbeState.FEASIBLE
    }

    /** 人话结论（首页展示，M1 不承诺 QEMU 引擎 —— 引擎随 M2 N-VM-001）。 */
    fun humanMessage(state: ProbeState): String = when (state) {
        ProbeState.NOT_RUN -> "尚未完成能力探测（或设备信息不足，不做猜测）"
        ProbeState.FEASIBLE -> "设备满足 QEMU 用户态运行前提（TCG；KVM 存在则注明）。引擎随 M2 落地，当前不提供镜像运行"
        ProbeState.CONSTRAINED -> "可运行但受限：memlock 上限不足（16GB 映射需经 Shizuku 上下文放宽）或 TCG 慢路径 —— 引擎随 M2 落地"
        ProbeState.INFEASIBLE -> "存储余量不足以容纳最小系统镜像（需 ≥ ${MIN_IMAGE_HEADROOM_BYTES / (1024 * 1024)} MB）"
    }

    /** 最小镜像头寸（Alpine ARM ~200MB，含快照余量按 512MB 门禁）。 */
    const val MIN_IMAGE_HEADROOM_BYTES: Long = 512L * 1024 * 1024

    /** memlock 最低期望（128MB 以下视为需要 Shizuku 放宽）。 */
    const val MIN_MEMLOCK_BYTES: Long = 128L * 1024 * 1024
}

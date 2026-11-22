package com.abyss.plugin.virtualmachine.capability

import org.junit.Assert.assertEquals
import org.junit.Test

/** N-VM-001 脚手架能力探测（纯 JVM fail-closed 覆盖）。 */
class VmCapabilityTest {

    private val feasible = VmCapability.Snapshot(
        abiList = listOf("arm64-v8a", "armeabi-v7a"),
        kvmDevicePresent = false,
        memlockLimitBytes = 256L * 1024 * 1024,
        availableStorageBytes = 4L * 1024 * 1024 * 1024,
    )

    @Test
    fun `arm64 host with sufficient resources is FEASIBLE`() {
        assertEquals(VmCapability.ProbeState.FEASIBLE, VmCapability.probe(feasible))
    }

    @Test
    fun `low memlock is CONSTRAINED not INFEASIBLE`() {
        assertEquals(
            VmCapability.ProbeState.CONSTRAINED,
            VmCapability.probe(feasible.copy(memlockLimitBytes = 64L * 1024 * 1024)),
        )
    }

    @Test
    fun `storage below headroom is INFEASIBLE`() {
        assertEquals(
            VmCapability.ProbeState.INFEASIBLE,
            VmCapability.probe(feasible.copy(availableStorageBytes = 100L * 1024 * 1024)),
        )
    }

    @Test
    fun `unknown abi or storage fail-closed to NOT_RUN`() {
        assertEquals(
            VmCapability.ProbeState.NOT_RUN,
            VmCapability.probe(feasible.copy(abiList = emptyList())),
        )
        assertEquals(
            VmCapability.ProbeState.NOT_RUN,
            VmCapability.probe(feasible.copy(availableStorageBytes = null)),
        )
    }

    @Test
    fun `arm host recommends arm guest`() {
        assertEquals(
            VmCapability.AbiTier.ARM64_HOST,
            VmCapability.abiTier(listOf("arm64-v8a")),
        )
        assert(VmCapability.recommendedGuest(VmCapability.AbiTier.ARM64_HOST).contains("ARM"))
        assert(VmCapability.recommendedGuest(VmCapability.AbiTier.UNKNOWN).contains("不做建议"))
    }
}

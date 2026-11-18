package dev.oreui.adapter262;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fabric26InputBridgeTest {
    @Test
    void stopDisablesForwarding() {
        Fabric26InputBridge bridge = new Fabric26InputBridge(event -> {
        });
        assertTrue(bridge.isActive());
        bridge.stop();
        assertFalse(bridge.isActive());
    }
}
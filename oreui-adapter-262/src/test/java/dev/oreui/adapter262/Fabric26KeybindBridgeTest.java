package dev.oreui.adapter262;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Fabric26KeybindBridgeTest {
    @Test
    void reportsConflicts() {
        Fabric26KeybindBridge bridge = new Fabric26KeybindBridge();
        assertTrue(bridge.conflicts(65, 65));
        assertFalse(bridge.conflicts(65, 66));
    }
}
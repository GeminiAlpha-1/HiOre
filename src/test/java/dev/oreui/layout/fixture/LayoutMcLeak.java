package dev.oreui.layout.fixture;

import net.minecraft.client.Minecraft;

/**
 * Negative fixture: layout must not reference Minecraft.
 */
public final class LayoutMcLeak {
    private final Minecraft minecraft;

    private LayoutMcLeak(Minecraft minecraft) {
        this.minecraft = minecraft;
    }
}
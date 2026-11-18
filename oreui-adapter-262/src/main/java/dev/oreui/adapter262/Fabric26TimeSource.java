package dev.oreui.adapter262;

import dev.oreui.components.platform.ITimeSource;
import net.minecraft.client.Minecraft;

public final class Fabric26TimeSource implements ITimeSource {
    @Override
    public float deltaSeconds() {
        return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks() / 20f;
    }
}
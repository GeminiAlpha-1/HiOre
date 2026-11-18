package net.minecraft.client;

/**
 * Test-only stand-in so ArchUnit can evaluate the "layout must not touch
 * Minecraft" rule without loading the real game.
 */
public final class Minecraft {
    private Minecraft() {
    }
}
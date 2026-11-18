package dev.oreui.adapter262;

import dev.oreui.components.platform.ISoundPlayer;
import dev.oreui.theme.SoundRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public final class Fabric26SoundBridge implements ISoundPlayer {
    @Override
    public void play(SoundRole role, float volume, float pitch) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(event(role), volume, pitch));
    }

    private static SoundEvent event(SoundRole role) {
        return switch (role) {
            case CLICK, SCROLL -> SoundEvents.STONE_BUTTON_CLICK_ON;
            case TOGGLE_ON, TOGGLE_OFF -> SoundEvents.LEVER_CLICK;
            case TYPING, POPUP_OPEN, POPUP_CLOSE -> SoundEvents.BOOK_PAGE_TURN;
            case MODAL_OPEN -> SoundEvents.NOTE_BLOCK_BELL.value();
            case TOAST -> SoundEvents.NOTE_BLOCK_CHIME.value();
            case ERROR -> SoundEvents.NOTE_BLOCK_BASS.value();
        };
    }
}
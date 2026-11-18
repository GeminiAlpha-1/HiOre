package dev.oreui.components.platform;

import dev.oreui.theme.SoundRole;

public interface ISoundPlayer {
    void play(SoundRole role, float volume, float pitch);
}
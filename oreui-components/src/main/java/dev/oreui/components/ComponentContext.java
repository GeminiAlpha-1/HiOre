package dev.oreui.components;

import dev.oreui.components.platform.IClipboard;
import dev.oreui.components.platform.IGameObjectRenderer;
import dev.oreui.components.platform.IKeybindBridge;
import dev.oreui.components.platform.ISoundPlayer;
import dev.oreui.components.platform.ITextMeasurer;
import dev.oreui.components.platform.ITextSource;
import dev.oreui.components.platform.ITimeSource;
import dev.oreui.theme.OreTheme;

public final class ComponentContext {
    private final OreTheme theme;
    private final ITextSource textSource;
    private final ITextMeasurer textMeasurer;
    private final IClipboard clipboard;
    private final ISoundPlayer sound;
    private final IKeybindBridge keybind;
    private final ITimeSource time;
    private final IGameObjectRenderer gameObjectRenderer;

    public ComponentContext(OreTheme theme, ITextSource textSource, ITextMeasurer textMeasurer,
                            IClipboard clipboard, ISoundPlayer sound,
                            IKeybindBridge keybind, ITimeSource time,
                            IGameObjectRenderer gameObjectRenderer) {
        if (theme == null || textSource == null || textMeasurer == null
                || clipboard == null || sound == null || keybind == null || time == null) {
            throw new NullPointerException("component context dependency");
        }
        this.theme = theme;
        this.textSource = textSource;
        this.textMeasurer = textMeasurer;
        this.clipboard = clipboard;
        this.sound = sound;
        this.keybind = keybind;
        this.time = time;
        this.gameObjectRenderer = gameObjectRenderer;
    }

    public OreTheme theme() {
        return theme;
    }

    public ITextSource textSource() {
        return textSource;
    }

    public ITextMeasurer textMeasurer() {
        return textMeasurer;
    }

    public IClipboard clipboard() {
        return clipboard;
    }

    public ISoundPlayer sound() {
        return sound;
    }

    public IKeybindBridge keybind() {
        return keybind;
    }

    public ITimeSource time() {
        return time;
    }

    public IGameObjectRenderer gameObjectRenderer() {
        return gameObjectRenderer;
    }
}
package dev.oreui.components;

import dev.oreui.components.platform.IClipboard;
import dev.oreui.components.platform.IGameObjectRenderer;
import dev.oreui.components.platform.IKeybindBridge;
import dev.oreui.components.platform.ISoundPlayer;
import dev.oreui.components.platform.ITextMeasurer;
import dev.oreui.components.platform.ITimeSource;

import dev.oreui.components.platform.TextMetrics;
import dev.oreui.theme.OreTokens;

import java.util.ArrayList;
import java.util.List;

final class TestFixtures {
    private TestFixtures() {
    }

    static ComponentContext context() {
        return context(null);
    }

    static ComponentContext context(IGameObjectRenderer gameObjectRenderer) {
        ITextMeasurer measurer = (text, role, maxWidth) -> {
            String value = text.key();
            int width = value.length() * 6;
            int lineHeight = OreTokens.DARK.type(role).lineHeight();
            if (maxWidth > 0f && width > maxWidth) {
                int chars = Math.max(1, (int) (maxWidth / 6f));
                List<String> lines = new ArrayList<>();
                for (int i = 0; i < value.length(); i += chars) {
                    lines.add(value.substring(i, Math.min(value.length(), i + chars)));
                }
                return new TextMetrics(chars * 6, lineHeight * lines.size(), lines);
            }
            return new TextMetrics(width, lineHeight, List.of(value));
        };
        IClipboard clipboard = new IClipboard() {
            private String value = "";

            @Override
            public String get() {
                return value;
            }

            @Override
            public void set(String text) {
                value = text;
            }
        };
        ISoundPlayer sound = (role, volume, pitch) -> {
        };
        IKeybindBridge keybind = new IKeybindBridge() {
            @Override
            public String displayName(int keyCode) {
                return Integer.toString(keyCode);
            }

            @Override
            public boolean conflicts(int firstKeyCode, int secondKeyCode) {
                return firstKeyCode == secondKeyCode;
            }
        };
        ITimeSource time = () -> 0.016f;
        return new ComponentContext(OreTokens.DARK, text -> text.key(), measurer,
                clipboard, sound, keybind, time, gameObjectRenderer);
    }

    static void fillSurface(ComponentContext context, OreSurface surface, int width, int height) {
        surface.style().width(width).height(height).padding(new dev.oreui.core.geom.Insets(0f, 0f, 0f, 0f)).gap(0f);
    }

    static LiteralText text(String value) {
        return new LiteralText(value);
    }
}
package dev.oreui.components.platform;

import dev.oreui.theme.TypeRole;

public interface ITextMeasurer {
    TextMetrics measure(IText text, TypeRole role, float maxWidth);
}
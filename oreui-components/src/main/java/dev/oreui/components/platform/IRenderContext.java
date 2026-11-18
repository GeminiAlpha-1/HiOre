package dev.oreui.components.platform;

import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

public interface IRenderContext {
    void begin();

    void end();

    void submitPixelRect(Rect2f rect, int argb);

    void submitSdfRect(Rect2f rect, int argb, float radius, float border);

    void submitText(IText text, float x, float y, TypeRole role, ColorRole color);

    void pushClip(Rect2f rect);

    void popClip();

    void pushTransform(float translateX, float translateY, float scale);

    void popTransform();

    void pushOpacity(float alpha);

    void popOpacity();
}
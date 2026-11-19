package dev.oreui.components;

import dev.oreui.components.platform.IRenderContext;
import dev.oreui.components.platform.IText;
import dev.oreui.core.geom.Rect2f;
import dev.oreui.theme.ColorRole;
import dev.oreui.theme.TypeRole;

final class TestRenderContext implements IRenderContext {
    @Override
    public void begin() {
    }

    @Override
    public void end() {
    }

    @Override
    public void submitPixelRect(Rect2f rect, int argb) {
    }

    @Override
    public void submitSdfRect(Rect2f rect, int argb, float radius, float border) {
    }

    @Override
    public void submitText(IText text, float x, float y, TypeRole role, ColorRole color) {
    }

    @Override
    public void pushClip(Rect2f rect) {
    }

    @Override
    public void popClip() {
    }

    @Override
    public void pushTransform(float translateX, float translateY, float scale) {
    }

    @Override
    public void popTransform() {
    }

    @Override
    public void pushOpacity(float alpha) {
    }

    @Override
    public void popOpacity() {
    }
}
package dev.oreui.layout;

public final class Spacer extends LayoutNode {
    public Spacer() {
    }

    public Spacer(float grow) {
        style().flexGrow(grow);
    }

    public static Spacer grow(float factor) {
        return new Spacer(factor);
    }
}
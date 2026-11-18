package dev.oreui.components.platform;

public interface IScreenHost {
    void attach();

    void detach();

    boolean isAttached();
}
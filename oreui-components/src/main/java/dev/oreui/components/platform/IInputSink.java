package dev.oreui.components.platform;

@FunctionalInterface
public interface IInputSink {
    void accept(IInputEvent event);
}
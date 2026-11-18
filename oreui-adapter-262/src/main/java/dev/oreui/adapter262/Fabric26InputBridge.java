package dev.oreui.adapter262;

import dev.oreui.components.platform.IInputEvent;
import dev.oreui.components.platform.IInputSink;
import dev.oreui.components.platform.InputEvent;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screens.Screen;

public final class Fabric26InputBridge {
    private final IInputSink sink;
    private boolean active = true;

    Fabric26InputBridge(IInputSink sink) {
        if (sink == null) {
            throw new NullPointerException("sink");
        }
        this.sink = sink;
    }

    public Fabric26InputBridge(Screen screen, IInputSink sink) {
        this(sink);
        if (screen == null) {
            throw new NullPointerException("screen");
        }
        ScreenMouseEvents.afterMouseClick(screen).register((s, event, handled) -> {
            if (!active) {
                return false;
            }
            sink.accept(new InputEvent(IInputEvent.Kind.POINTER_DOWN,
                    (float) event.x(), (float) event.y(), event.button(), event.modifiers(), '\0'));
            return false;
        });
        ScreenMouseEvents.afterMouseRelease(screen).register((s, event, handled) -> {
            if (!active) {
                return false;
            }
            sink.accept(new InputEvent(IInputEvent.Kind.POINTER_UP,
                    (float) event.x(), (float) event.y(), event.button(), event.modifiers(), '\0'));
            return false;
        });
        ScreenMouseEvents.afterMouseDrag(screen).register((s, event, deltaX, deltaY, handled) -> {
            if (!active) {
                return false;
            }
            sink.accept(new InputEvent(IInputEvent.Kind.POINTER_MOVE,
                    (float) event.x(), (float) event.y(), event.button(), event.modifiers(), '\0'));
            return false;
        });
        ScreenMouseEvents.afterMouseScroll(screen).register((s, mouseX, mouseY, scrollX, scrollY, handled) -> {
            if (!active) {
                return false;
            }
            sink.accept(new InputEvent(IInputEvent.Kind.WHEEL,
                    (float) mouseX, (float) mouseY, (int) Math.signum(scrollY), 0, '\0'));
            return false;
        });
        ScreenKeyboardEvents.afterKeyPress(screen).register((s, event) -> {
            if (active) {
                sink.accept(new InputEvent(IInputEvent.Kind.KEY_DOWN, 0f, 0f,
                        event.key(), event.modifiers(), '\0'));
            }
        });
        ScreenKeyboardEvents.afterKeyRelease(screen).register((s, event) -> {
            if (active) {
                sink.accept(new InputEvent(IInputEvent.Kind.KEY_UP, 0f, 0f,
                        event.key(), event.modifiers(), '\0'));
            }
        });
        ScreenKeyboardEvents.afterCharType(screen).register((s, event) -> {
            if (active) {
                sink.accept(new InputEvent(IInputEvent.Kind.CHAR_TYPED, 0f, 0f,
                        event.codepoint(), 0, (char) event.codepoint()));
            }
        });
    }

    public void stop() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }
}
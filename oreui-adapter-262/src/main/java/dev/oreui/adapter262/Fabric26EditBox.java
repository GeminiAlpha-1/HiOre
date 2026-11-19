package dev.oreui.adapter262;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.network.chat.Component;

final class Fabric26EditBox extends EditBox {
    private String preedit = "";

    Fabric26EditBox(int x, int y, int width, int height, Component message) {
        super(Minecraft.getInstance().font, x, y, width, height, message);
    }

    @Override
    public boolean preeditUpdated(PreeditEvent event) {
        preedit = event == null ? "" : event.fullText();
        return super.preeditUpdated(event);
    }

    String preedit() {
        return preedit;
    }
}
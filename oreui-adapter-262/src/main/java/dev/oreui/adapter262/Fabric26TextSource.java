package dev.oreui.adapter262;

import dev.oreui.components.LiteralText;
import dev.oreui.components.platform.IText;
import dev.oreui.components.platform.ITextSource;
import net.minecraft.network.chat.Component;

public final class Fabric26TextSource implements ITextSource {
    @Override
    public String resolve(IText text) {
        if (text == null) {
            return "";
        }
        if (text instanceof LiteralText) {
            return text.key();
        }
        return Component.translatable(text.key(), text.args().toArray()).getString();
    }
}
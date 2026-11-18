package dev.oreui.adapter262;

import dev.oreui.components.platform.SimpleText;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Fabric26TextSourceTest {
    @Test
    void resolvesMissingTranslationsToKey() {
        Fabric26TextSource source = new Fabric26TextSource();
        assertEquals("oreui.missing.test", source.resolve(new SimpleText("oreui.missing.test", List.of())));
    }
}
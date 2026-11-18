package dev.oreui.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import dev.oreui.adapter262.fixture.AdapterInternalLeak;
import dev.oreui.components.fixture.ComponentsInternalLeak;
import dev.oreui.components.internal.InternalRenderBridge;
import dev.oreui.consumertest.ConsumerLeak;
import dev.oreui.consumertest.ConsumerSmoke;
import dev.oreui.core.fixture.CoreAwtLeak;
import dev.oreui.layout.LayoutModule;
import dev.oreui.layout.fixture.LayoutMcLeak;
import dev.oreui.theme.fixture.ThemeLayoutLeak;
import net.minecraft.client.Minecraft;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Proves that each module-boundary rule actually rejects a violation. The
 * fixtures live under src/test and are excluded from the automatic scan.
 */
class ArchRuleNegativeTest {
    private static final ClassFileImporter IMPORTER = new ClassFileImporter();

    @Test
    void coreRejectsAwtDependency() {
        JavaClasses classes = IMPORTER.importClasses(CoreAwtLeak.class);
        assertThrows(AssertionError.class, () -> ModuleBoundaryTest.coreIsPure.check(classes));
    }

    @Test
    void layoutRejectsMinecraftDependency() {
        JavaClasses classes = IMPORTER.importClasses(LayoutMcLeak.class, Minecraft.class);
        assertThrows(AssertionError.class, () -> ModuleBoundaryTest.layoutIsGameFree.check(classes));
    }

    @Test
    void themeRejectsLayoutDependency() {
        JavaClasses classes = IMPORTER.importClasses(ThemeLayoutLeak.class, LayoutModule.class);
        assertThrows(AssertionError.class, () -> ModuleBoundaryTest.themeIsGameFree.check(classes));
    }

    @Test
    void componentsRejectsInternalAccess() {
        JavaClasses classes = IMPORTER.importClasses(ComponentsInternalLeak.class, InternalRenderBridge.class);
        assertThrows(AssertionError.class,
                () -> ModuleBoundaryTest.internalApiIsPackagePrivate.check(classes));
    }

    @Test
    void consumerRejectsNonApiDependency() {
        JavaClasses classes = IMPORTER.importClasses(ConsumerLeak.class, LayoutModule.class);
        assertThrows(AssertionError.class,
                () -> ModuleBoundaryTest.consumerUsesOnlyPublicApi.check(classes));
    }

    @Test
    void consumerSmokeUsesOnlyApi() {
        JavaClasses classes = IMPORTER.importClasses(ConsumerSmoke.class);
        assertDoesNotThrow(() -> ModuleBoundaryTest.consumerUsesOnlyPublicApi.check(classes));
    }

    @Test
    void adapterRejectsInternalAccess() {
        JavaClasses classes = IMPORTER.importClasses(AdapterInternalLeak.class, InternalRenderBridge.class);
        assertThrows(AssertionError.class,
                () -> ModuleBoundaryTest.adapterMustNotUseInternal.check(classes));
    }
}
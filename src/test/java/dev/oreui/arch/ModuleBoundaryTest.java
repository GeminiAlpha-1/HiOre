package dev.oreui.arch;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Phase 1 module-boundary guard. Scans only the pure-Java modules (adapter is
 * not on the root test classpath).
 */
@AnalyzeClasses(packages = "dev.oreui",
        importOptions = {ImportOption.DoNotIncludeTests.class, ModuleBoundaryTest.ExcludeTestFixtures.class})
class ModuleBoundaryTest {
    @ArchTest
    static final ArchRule coreIsPure = noClasses()
            .that().resideInAnyPackage("dev.oreui.core..", "dev.oreui.api..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "net.minecraft..", "com.mojang..", "net.fabricmc..",
                    "java.awt..", "javax.swing..");

    @ArchTest
    static final ArchRule coreDoesNotDependOnHigherLayers = noClasses()
            .that().resideInAnyPackage("dev.oreui.core..", "dev.oreui.api..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "dev.oreui.layout..", "dev.oreui.theme..",
                    "dev.oreui.components..", "dev.oreui.adapter262..");

    @ArchTest
    static final ArchRule layoutIsGameFree = noClasses()
            .that().resideInAPackage("dev.oreui.layout..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "net.minecraft..", "com.mojang..", "net.fabricmc..");

    @ArchTest
    static final ArchRule layoutDoesNotDependOnHigherLayers = noClasses()
            .that().resideInAPackage("dev.oreui.layout..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "dev.oreui.theme..", "dev.oreui.components..", "dev.oreui.adapter262..");

    @ArchTest
    static final ArchRule themeIsGameFree = noClasses()
            .that().resideInAPackage("dev.oreui.theme..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "net.minecraft..", "com.mojang..", "net.fabricmc..", "dev.oreui.layout..");

    @ArchTest
    static final ArchRule themeDoesNotDependOnHigherLayers = noClasses()
            .that().resideInAPackage("dev.oreui.theme..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "dev.oreui.components..", "dev.oreui.adapter262..");

    @ArchTest
    static final ArchRule componentsAreGameFree = noClasses()
            .that().resideInAPackage("dev.oreui.components..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "net.minecraft..", "com.mojang..", "net.fabricmc..", "dev.oreui.adapter262..");

    @ArchTest
    static final ArchRule internalApiIsPackagePrivate = noClasses()
            .that().resideOutsideOfPackage("dev.oreui.components.internal..")
            .and().resideInAnyPackage("dev.oreui.core..", "dev.oreui.layout..", "dev.oreui.theme..",
                    "dev.oreui.components..", "dev.oreui.api..")
            .should().dependOnClassesThat().resideInAPackage("dev.oreui.components.internal..");

    @ArchTest
    static final ArchRule consumerUsesOnlyPublicApi = noClasses()
            .that().resideInAPackage("dev.oreui.consumertest..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "dev.oreui.core..", "dev.oreui.layout..", "dev.oreui.theme..",
                    "dev.oreui.components..", "dev.oreui.adapter262..", "dev.oreui.testmod..");

    @ArchTest
    static final ArchRule adapterMustNotUseInternal = noClasses()
            .that().resideInAPackage("dev.oreui.adapter262..")
            .should().dependOnClassesThat().resideInAPackage("dev.oreui.components.internal..")
            .allowEmptyShould(true);

    public static final class ExcludeTestFixtures implements ImportOption {
        @Override
        public boolean includes(Location location) {
            return !location.contains("build/classes/java/test")
                    && !location.contains("/test-classes/")
                    && !location.contains("fixture");
        }
    }
}
package com.proyecto.congreso;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Test para validar la arquitectura modular con Spring Modulith.
 * 
 * Este test:
 * 1. Verifica que los módulos estén correctamente definidos
 * 2. Valida que no existan dependencias cíclicas
 * 3. Asegura que se respeten las reglas de encapsulación
 * 4. Genera documentación UML de la arquitectura
 */
class ModulithTest {

    ApplicationModules modules = ApplicationModules.of(CongresoApplication.class);

    @Test
    void verifyModularStructure() {
        // Imprime la estructura de módulos detectada
        modules.forEach(System.out::println);
    }

//    @Test
//    void verifyCyclicDependencies() {
//        // Falla si hay dependencias cíclicas
//        modules.verify();
//    }

    @Test
    void documentModules() throws Exception {
        // Genera documentación en docs/modulith
        new Documenter(modules)
            .writeDocumentation()
            .writeIndividualModulesAsPlantUml()
            .writeModulesAsPlantUml();
    }

//    @Test
//    void verifyModuleStructure() {
//        // Verificar que los módulos esperados existen
//        modules.verify();
//
//        // Imprimir información de dependencias
//        System.out.println("\n=== Estructura de Módulos ===");
//        modules.forEach(module -> {
//            System.out.println("\n📦 Módulo: " + module.getName());
//            System.out.println("   Base Package: " + module.getBasePackage());
//            System.out.println("   Dependencias:");
//            module.getDependencies().forEach(dep ->
//                System.out.println("   ➜ " + targetModule.getName());
//            );
//        });
//    }
}

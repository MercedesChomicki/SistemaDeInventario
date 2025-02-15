package com.inventario.Inventario.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Configura un manejador de recursos estáticos para servir archivos subidos al servidor.
     * <p>
     * Este método le indica a Spring Boot que los archivos ubicados en la carpeta "uploads/"
     * en el sistema de archivos deben ser accesibles desde la URL pública "/uploads/**".
     * De esta manera, los clientes pueden acceder a los archivos subidos sin necesidad
     * de una lógica adicional en el backend.
     * </p>
     *
     * <h3>Ejemplo de uso:</h3>
     * <ul>
     *     <li>Si un archivo "imagen123.jpg" se guarda en "uploads/" en el servidor,
     *         podrá accederse desde <code>http://localhost:8080/uploads/imagen123.jpg</code>.</li>
     *     <li>Esto es útil para mostrar imágenes subidas en una aplicación web.</li>
     * </ul>
     *
     * <h3>Notas:</h3>
     * <ul>
     *     <li>Asegúrate de que la carpeta "uploads/" existe y tiene permisos de lectura.</li>
     *     <li>El prefijo <code>"file:"</code> indica que la ubicación es un directorio físico en el servidor.</li>
     *     <li>Este método es ejecutado automáticamente por Spring Boot al iniciar la aplicación.</li>
     * </ul>
     *
     * @param registry Registro de manejadores de recursos de Spring.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}

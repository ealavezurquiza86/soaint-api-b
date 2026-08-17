package com.soaint.ealavez.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuración de metadatos OpenAPI / Swagger UI (springdoc).
 * <p>
 * El servidor documentado toma el puerto de {@code server.port} (p. ej. perfil
 * {@code dev} → {@code application-dev.yml}).
 * </p>
 */
@Configuration
public class OpenApiConfig {

	/**
	 * @param serverPort puerto HTTP activo de la aplicación
	 * @return definición OpenAPI alineada al código y a la configuración runtime
	 */
	@Bean
	OpenAPI apiBOpenAPI(@Value("${server.port}") int serverPort) {
		return new OpenAPI()
				.info(new Info()
						.title("API de Transacciones - Almacenamiento")
						.version("1.2.0")
						.description(
								"API de persistencia, consulta paginada y cancelación de transacciones (H2). "
										+ "POST: comprobante plano (estatus APROBADA). "
										+ "GET: listado paginado sin secreto. "
										+ "PATCH /cancelar: transición APROBADA → CANCELADA (acción body: cancelar). "
										+ "Estatus en respuestas: valores del enum APROBADA | CANCELADA."))
				.servers(List.of(
						new Server()
								.url("http://localhost:" + serverPort)
								.description("Servidor local (server.port de la configuración activa)")));
	}
}

# Prueba Técnica - Desarrollador Java Jr

Proyecto de ejemplo para la prueba técnica solicitada. Implementación en Spring Boot con Maven.

## Requisitos
- Java 11+
- Maven 3.6+

## Cómo ejecutar
1. Clonar el repositorio
2. Desde la raíz del proyecto:

```bash
mvn clean package
java -jar target/prueba-tecnica-java-jr-1.0.0.jar
```

El servicio correrá por defecto en http://localhost:8082

## Endpoints
- `GET /posts` — Obtiene posts mergeando información de posts, comentarios y usuario autor (desde JSONPlaceholder).
- `DELETE /posts/{id}` — Realiza una llamada DELETE al servicio externo (operación simulada).

## Consideraciones técnicas
- Framework: Spring Boot
- Cliente HTTP: RestTemplate (configurado con timeouts)
- Cache: Caffeine via `@Cacheable("posts")` para optimizar llamadas repetidas
- OpenAPI/Swagger: disponible en `/swagger-ui.html` o `/swagger-ui/index.html`
- Tests: JUnit + Mockito para PostService
- Manejo de errores centralizado con `GlobalExceptionHandler`


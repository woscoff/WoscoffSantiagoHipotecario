package com.example.pruebajava.controller;

import com.example.pruebajava.dto.MergedPost;
import com.example.pruebajava.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/posts")
@Validated
@Tag(name = "Posts", description = "API para gestión de posts con integración de servicios externos")
public class PostsController {
    private static final Logger logger = LoggerFactory.getLogger(PostsController.class);

    private final PostService postService;

    public PostsController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    @Operation(
        summary = "Obtener todos los posts con información completa",
        description = "Obtiene todos los posts del servicio externo JSONPlaceholder, " +
                    "mergeando información de posts, comentarios y usuarios autores. " +
                    "Utiliza cache para optimizar rendimiento en llamadas repetidas."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Posts obtenidos exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MergedPost.class)
            )
        ),
        @ApiResponse(
            responseCode = "502", 
            description = "Error en servicio externo",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.example.pruebajava.exception.GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "504", 
            description = "Timeout del servicio externo",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.example.pruebajava.exception.GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.example.pruebajava.exception.GlobalExceptionHandler.ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<List<MergedPost>> getPosts() {
        logger.info("GET /posts requested");
        List<MergedPost> merged = postService.getAllMergedPosts();
        logger.info("Successfully returned {} merged posts", merged.size());
        return ResponseEntity.ok(merged);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar un post",
        description = "Elimina un post específico del servicio externo JSONPlaceholder. " +
                    "Nota: El servicio externo no persiste cambios realmente, solo simula la operación."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Post eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "ID de post inválido (debe ser mayor a 0)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.example.pruebajava.exception.GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Post no encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.example.pruebajava.exception.GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "502", 
            description = "Error en servicio externo",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.example.pruebajava.exception.GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "504", 
            description = "Timeout del servicio externo",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.example.pruebajava.exception.GlobalExceptionHandler.ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.example.pruebajava.exception.GlobalExceptionHandler.ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "ID del post a eliminar", required = true, example = "1")
            @PathVariable @Min(value = 1, message = "Post ID must be greater than 0") Integer id) {
        
        logger.info("DELETE /posts/{} requested", id);
        postService.deletePost(id);
        logger.info("Successfully deleted post with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}

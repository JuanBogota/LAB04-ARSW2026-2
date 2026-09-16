package co.edu.eci.blueprints.p1.controllers;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import co.edu.eci.blueprints.p1.model.ApiResponse;
import co.edu.eci.blueprints.p1.model.Blueprint;
import co.edu.eci.blueprints.p1.model.Point;
import co.edu.eci.blueprints.p1.persistence.BlueprintNotFoundException;
import co.edu.eci.blueprints.p1.persistence.BlueprintPersistenceException;
import co.edu.eci.blueprints.p1.services.BlueprintsServices;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/blueprints")
public class BlueprintsAPIController {

    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) { this.services = services; }

    @Operation(summary = "Obtener todos los blueprints")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    @GetMapping
    public ResponseEntity<ApiResponse<Set<Blueprint>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(services.getAllBlueprints()));
    }

    @Operation(summary = "Obtener blueprints de un autor")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    @GetMapping("/{author}")
    public ResponseEntity<ApiResponse<Set<Blueprint>>> byAuthor(@PathVariable String author)
            throws BlueprintNotFoundException {
        return ResponseEntity.ok(ApiResponse.ok(services.getBlueprintsByAuthor(author)));
    }

    @Operation(summary = "Obtener un blueprint específico")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    @GetMapping("/{author}/{bpname}")
    public ResponseEntity<ApiResponse<Blueprint>> byAuthorAndName(
            @PathVariable String author, @PathVariable String bpname)
            throws BlueprintNotFoundException {
        return ResponseEntity.ok(ApiResponse.ok(services.getBlueprint(author, bpname)));
    }

    @Operation(summary = "Crear un nuevo blueprint")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')")
    @PostMapping
    public ResponseEntity<ApiResponse<Blueprint>> add(@Valid @RequestBody NewBlueprintRequest req)
            throws BlueprintPersistenceException {
        Blueprint bp = new Blueprint(req.author(), req.name(), req.points());
        services.addNewBlueprint(bp);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(bp));
    }

    @Operation(summary = "Agregar un punto a un blueprint")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')")
    @PutMapping("/{author}/{bpname}/points")
    public ResponseEntity<ApiResponse<Blueprint>> addPoint(
            @PathVariable String author, @PathVariable String bpname, @RequestBody Point p)
            throws BlueprintNotFoundException {
        services.addPoint(author, bpname, p.x(), p.y());
        Blueprint updated = services.getBlueprint(author, bpname);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.accepted(updated));
    }

    public record NewBlueprintRequest(
            @NotBlank String author,
            @NotBlank String name,
            @Valid java.util.List<Point> points
    ) { }
}
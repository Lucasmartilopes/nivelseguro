package br.com.fiap.nivelseguro.controller;

import br.com.fiap.nivelseguro.dto.BairroRequest;
import br.com.fiap.nivelseguro.dto.BairroResponse;
import br.com.fiap.nivelseguro.dto.SensorResponse;
import br.com.fiap.nivelseguro.service.BairroService;
import br.com.fiap.nivelseguro.service.SensorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bairros")
public class BairroController {

    private final BairroService bairroService;
    private final SensorService sensorService;

    public BairroController(BairroService bairroService, SensorService sensorService) {
        this.bairroService = bairroService;
        this.sensorService = sensorService;
    }

    @GetMapping
    public ResponseEntity<List<BairroResponse>> listar() {
        return ResponseEntity.ok(bairroService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BairroResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(bairroService.buscarPorId(id));
    }

    // Relacionamento 1:N -> sensores instalados no bairro
    @GetMapping("/{id}/sensores")
    public ResponseEntity<List<SensorResponse>> listarSensores(@PathVariable Long id) {
        return ResponseEntity.ok(sensorService.listarPorBairro(id));
    }

    @PostMapping
    public ResponseEntity<BairroResponse> criar(@Valid @RequestBody BairroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bairroService.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BairroResponse> atualizar(@PathVariable Long id, @Valid @RequestBody BairroRequest request) {
        return ResponseEntity.ok(bairroService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bairroService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

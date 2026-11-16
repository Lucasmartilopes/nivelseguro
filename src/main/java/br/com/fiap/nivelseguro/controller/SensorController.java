package br.com.fiap.nivelseguro.controller;

import br.com.fiap.nivelseguro.dto.LeituraResponse;
import br.com.fiap.nivelseguro.dto.SensorRequest;
import br.com.fiap.nivelseguro.dto.SensorResponse;
import br.com.fiap.nivelseguro.service.LeituraService;
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
@RequestMapping("/api/sensores")
public class SensorController {

    private final SensorService sensorService;
    private final LeituraService leituraService;

    public SensorController(SensorService sensorService, LeituraService leituraService) {
        this.sensorService = sensorService;
        this.leituraService = leituraService;
    }

    @GetMapping
    public ResponseEntity<List<SensorResponse>> listar() {
        return ResponseEntity.ok(sensorService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sensorService.buscarPorId(id));
    }

    // Relacionamento 1:N -> leituras registradas pelo sensor
    @GetMapping("/{id}/leituras")
    public ResponseEntity<List<LeituraResponse>> listarLeituras(@PathVariable Long id) {
        return ResponseEntity.ok(leituraService.listarPorSensor(id));
    }

    @PostMapping
    public ResponseEntity<SensorResponse> criar(@Valid @RequestBody SensorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sensorService.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorResponse> atualizar(@PathVariable Long id, @Valid @RequestBody SensorRequest request) {
        return ResponseEntity.ok(sensorService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        sensorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

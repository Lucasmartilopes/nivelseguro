package br.com.fiap.nivelseguro.controller;

import br.com.fiap.nivelseguro.dto.LeituraRequest;
import br.com.fiap.nivelseguro.dto.LeituraResponse;
import br.com.fiap.nivelseguro.service.LeituraService;
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
@RequestMapping("/api/leituras")
public class LeituraController {

    private final LeituraService leituraService;

    public LeituraController(LeituraService leituraService) {
        this.leituraService = leituraService;
    }

    @GetMapping
    public ResponseEntity<List<LeituraResponse>> listar() {
        return ResponseEntity.ok(leituraService.listar());
    }

    // Leituras em ALERTA ou EMERGENCIA (o que o app mostra ao cidadao)
    @GetMapping("/alertas")
    public ResponseEntity<List<LeituraResponse>> listarAlertas() {
        return ResponseEntity.ok(leituraService.listarAlertas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeituraResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(leituraService.buscarPorId(id));
    }

    // Endpoint chamado pelo sensor IoT (Arduino/ESP32) a cada medicao
    @PostMapping
    public ResponseEntity<LeituraResponse> criar(@Valid @RequestBody LeituraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leituraService.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeituraResponse> atualizar(@PathVariable Long id, @Valid @RequestBody LeituraRequest request) {
        return ResponseEntity.ok(leituraService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        leituraService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

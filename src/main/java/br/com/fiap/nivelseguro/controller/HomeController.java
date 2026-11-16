package br.com.fiap.nivelseguro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Pagina inicial da API: confirma no navegador (http://IP-DA-VM:8080/)
 * que a aplicacao esta no ar e lista os recursos disponiveis.
 */
@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> inicio() {
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("aplicacao", "NivelSeguro API");
        resposta.put("descricao", "Monitoramento do nivel de agua e alertas de enchente por bairro");
        resposta.put("status", "online");
        resposta.put("recursos", Map.of(
                "bairros", "/api/bairros",
                "sensores", "/api/sensores",
                "leituras", "/api/leituras",
                "alertas", "/api/leituras/alertas",
                "health", "/actuator/health"
        ));
        return resposta;
    }
}

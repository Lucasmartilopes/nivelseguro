package br.com.fiap.nivelseguro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * NivelSeguro - monitoramento do nivel de agua de rios e corregos
 * com classificacao automatica de risco de enchente por bairro.
 */
@SpringBootApplication
public class NivelSeguroApplication {

    public static void main(String[] args) {
        SpringApplication.run(NivelSeguroApplication.class, args);
    }
}

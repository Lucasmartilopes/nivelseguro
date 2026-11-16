package br.com.fiap.nivelseguro.repository;

import br.com.fiap.nivelseguro.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    List<Sensor> findAllByOrderByCodigoAsc();

    List<Sensor> findByBairroIdOrderByCodigoAsc(Long bairroId);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);
}

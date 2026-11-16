package br.com.fiap.nivelseguro.repository;

import br.com.fiap.nivelseguro.model.Leitura;
import br.com.fiap.nivelseguro.model.NivelRisco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface LeituraRepository extends JpaRepository<Leitura, Long> {

    List<Leitura> findAllByOrderByDataHoraDesc();

    List<Leitura> findBySensorIdOrderByDataHoraDesc(Long sensorId);

    List<Leitura> findByNivelRiscoInOrderByDataHoraDesc(Collection<NivelRisco> niveis);
}

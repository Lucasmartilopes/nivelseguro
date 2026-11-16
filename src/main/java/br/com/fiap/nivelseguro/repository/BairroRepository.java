package br.com.fiap.nivelseguro.repository;

import br.com.fiap.nivelseguro.model.Bairro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BairroRepository extends JpaRepository<Bairro, Long> {

    List<Bairro> findAllByOrderByNomeAsc();
}

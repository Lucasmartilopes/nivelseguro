package br.com.fiap.nivelseguro.service;

import br.com.fiap.nivelseguro.dto.BairroRequest;
import br.com.fiap.nivelseguro.dto.BairroResponse;
import br.com.fiap.nivelseguro.exception.RecursoNaoEncontradoException;
import br.com.fiap.nivelseguro.model.Bairro;
import br.com.fiap.nivelseguro.repository.BairroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BairroService {

    private final BairroRepository bairroRepository;

    public BairroService(BairroRepository bairroRepository) {
        this.bairroRepository = bairroRepository;
    }

    @Transactional(readOnly = true)
    public List<BairroResponse> listar() {
        return bairroRepository.findAllByOrderByNomeAsc()
                .stream()
                .map(BairroResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public BairroResponse buscarPorId(Long id) {
        return BairroResponse.de(buscarEntidade(id));
    }

    @Transactional
    public BairroResponse criar(BairroRequest request) {
        Bairro bairro = new Bairro();
        copiarDados(request, bairro);
        return BairroResponse.de(bairroRepository.save(bairro));
    }

    @Transactional
    public BairroResponse atualizar(Long id, BairroRequest request) {
        Bairro bairro = buscarEntidade(id);
        copiarDados(request, bairro);
        return BairroResponse.de(bairroRepository.save(bairro));
    }

    @Transactional
    public void excluir(Long id) {
        Bairro bairro = buscarEntidade(id);
        bairroRepository.delete(bairro);
    }

    /** Usado tambem pelo SensorService para validar o bairro informado. */
    @Transactional(readOnly = true)
    public Bairro buscarEntidade(Long id) {
        return bairroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bairro nao encontrado com id: " + id));
    }

    private void copiarDados(BairroRequest request, Bairro bairro) {
        bairro.setNome(request.nome().trim());
        bairro.setCidade(request.cidade().trim());
        bairro.setUf(request.uf().trim().toUpperCase());
        bairro.setRegiao(request.regiao() == null ? null : request.regiao().trim());
    }
}

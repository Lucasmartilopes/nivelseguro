package br.com.fiap.nivelseguro.service;

import br.com.fiap.nivelseguro.dto.SensorRequest;
import br.com.fiap.nivelseguro.dto.SensorResponse;
import br.com.fiap.nivelseguro.exception.ConflitoException;
import br.com.fiap.nivelseguro.exception.RecursoNaoEncontradoException;
import br.com.fiap.nivelseguro.exception.RegraNegocioException;
import br.com.fiap.nivelseguro.model.Sensor;
import br.com.fiap.nivelseguro.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;
    private final BairroService bairroService;

    public SensorService(SensorRepository sensorRepository, BairroService bairroService) {
        this.sensorRepository = sensorRepository;
        this.bairroService = bairroService;
    }

    @Transactional(readOnly = true)
    public List<SensorResponse> listar() {
        return sensorRepository.findAllByOrderByCodigoAsc()
                .stream()
                .map(SensorResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SensorResponse> listarPorBairro(Long bairroId) {
        bairroService.buscarEntidade(bairroId); // 404 se o bairro nao existir
        return sensorRepository.findByBairroIdOrderByCodigoAsc(bairroId)
                .stream()
                .map(SensorResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public SensorResponse buscarPorId(Long id) {
        return SensorResponse.de(buscarEntidade(id));
    }

    @Transactional
    public SensorResponse criar(SensorRequest request) {
        String codigo = request.codigo().trim().toUpperCase();
        if (sensorRepository.existsByCodigo(codigo)) {
            throw new ConflitoException("Ja existe um sensor com o codigo: " + codigo);
        }
        Sensor sensor = new Sensor();
        copiarDados(request, codigo, sensor);
        return SensorResponse.de(sensorRepository.save(sensor));
    }

    @Transactional
    public SensorResponse atualizar(Long id, SensorRequest request) {
        Sensor sensor = buscarEntidade(id);
        String codigo = request.codigo().trim().toUpperCase();
        if (sensorRepository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ConflitoException("Ja existe outro sensor com o codigo: " + codigo);
        }
        copiarDados(request, codigo, sensor);
        return SensorResponse.de(sensorRepository.save(sensor));
    }

    @Transactional
    public void excluir(Long id) {
        Sensor sensor = buscarEntidade(id);
        sensorRepository.delete(sensor);
    }

    /** Usado tambem pelo LeituraService para validar o sensor informado. */
    @Transactional(readOnly = true)
    public Sensor buscarEntidade(Long id) {
        return sensorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sensor nao encontrado com id: " + id));
    }

    private void copiarDados(SensorRequest request, String codigo, Sensor sensor) {
        // Regra de negocio: a cota de emergencia precisa ser maior que a de alerta
        if (request.cotaEmergenciaCm().compareTo(request.cotaAlertaCm()) <= 0) {
            throw new RegraNegocioException("A cota de emergencia deve ser maior que a cota de alerta");
        }
        sensor.setCodigo(codigo);
        sensor.setLocalizacao(request.localizacao().trim());
        sensor.setCotaAlertaCm(request.cotaAlertaCm());
        sensor.setCotaEmergenciaCm(request.cotaEmergenciaCm());
        sensor.setAtivo(request.ativo() == null ? Boolean.TRUE : request.ativo());
        sensor.setBairro(bairroService.buscarEntidade(request.bairroId()));
    }
}

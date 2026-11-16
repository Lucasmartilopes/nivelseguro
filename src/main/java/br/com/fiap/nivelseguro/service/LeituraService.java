package br.com.fiap.nivelseguro.service;

import br.com.fiap.nivelseguro.dto.LeituraRequest;
import br.com.fiap.nivelseguro.dto.LeituraResponse;
import br.com.fiap.nivelseguro.exception.RecursoNaoEncontradoException;
import br.com.fiap.nivelseguro.exception.RegraNegocioException;
import br.com.fiap.nivelseguro.model.Leitura;
import br.com.fiap.nivelseguro.model.NivelRisco;
import br.com.fiap.nivelseguro.model.Sensor;
import br.com.fiap.nivelseguro.repository.LeituraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;

@Service
public class LeituraService {

    private static final Logger log = LoggerFactory.getLogger(LeituraService.class);

    private final LeituraRepository leituraRepository;
    private final SensorService sensorService;

    public LeituraService(LeituraRepository leituraRepository, SensorService sensorService) {
        this.leituraRepository = leituraRepository;
        this.sensorService = sensorService;
    }

    @Transactional(readOnly = true)
    public List<LeituraResponse> listar() {
        return leituraRepository.findAllByOrderByDataHoraDesc()
                .stream()
                .map(LeituraResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeituraResponse> listarPorSensor(Long sensorId) {
        sensorService.buscarEntidade(sensorId); // 404 se o sensor nao existir
        return leituraRepository.findBySensorIdOrderByDataHoraDesc(sensorId)
                .stream()
                .map(LeituraResponse::de)
                .toList();
    }

    /** Leituras que geraram alerta para a populacao (ALERTA ou EMERGENCIA). */
    @Transactional(readOnly = true)
    public List<LeituraResponse> listarAlertas() {
        return leituraRepository.findByNivelRiscoInOrderByDataHoraDesc(
                        EnumSet.of(NivelRisco.ALERTA, NivelRisco.EMERGENCIA))
                .stream()
                .map(LeituraResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public LeituraResponse buscarPorId(Long id) {
        return LeituraResponse.de(buscarEntidade(id));
    }

    @Transactional
    public LeituraResponse criar(LeituraRequest request) {
        Leitura leitura = new Leitura();
        copiarDados(request, leitura);
        Leitura salva = leituraRepository.save(leitura);

        if (salva.getNivelRisco() == NivelRisco.ALERTA || salva.getNivelRisco() == NivelRisco.EMERGENCIA) {
            log.warn("ALERTA DE ENCHENTE: sensor {} ({}) registrou {} cm -> {}",
                    salva.getSensor().getCodigo(),
                    salva.getSensor().getBairro().getNome(),
                    salva.getNivelAguaCm(),
                    salva.getNivelRisco());
        }
        return LeituraResponse.de(salva);
    }

    @Transactional
    public LeituraResponse atualizar(Long id, LeituraRequest request) {
        Leitura leitura = buscarEntidade(id);
        copiarDados(request, leitura);
        return LeituraResponse.de(leituraRepository.save(leitura));
    }

    @Transactional
    public void excluir(Long id) {
        Leitura leitura = buscarEntidade(id);
        leituraRepository.delete(leitura);
    }

    private Leitura buscarEntidade(Long id) {
        return leituraRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitura nao encontrada com id: " + id));
    }

    private void copiarDados(LeituraRequest request, Leitura leitura) {
        Sensor sensor = sensorService.buscarEntidade(request.sensorId());
        // Regra de negocio: sensor inativo (em manutencao) nao registra novas leituras
        if (leitura.getId() == null && !Boolean.TRUE.equals(sensor.getAtivo())) {
            throw new RegraNegocioException("O sensor " + sensor.getCodigo() + " esta inativo e nao pode registrar leituras");
        }
        leitura.setSensor(sensor);
        leitura.setNivelAguaCm(request.nivelAguaCm());
        leitura.setChuvaMm(request.chuvaMm());
        if (request.dataHora() != null) {
            leitura.setDataHora(request.dataHora());
        }
        // Regra de negocio principal: classificar o risco de enchente
        leitura.setNivelRisco(NivelRisco.calcular(
                request.nivelAguaCm(),
                sensor.getCotaAlertaCm(),
                sensor.getCotaEmergenciaCm()));
    }
}

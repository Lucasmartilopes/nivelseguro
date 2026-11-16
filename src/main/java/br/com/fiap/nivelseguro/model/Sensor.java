package br.com.fiap.nivelseguro.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Sensor de nivel de agua (ex.: Arduino + sensor ultrassonico HC-SR04)
 * instalado em um rio/corrego de um bairro.
 * N sensores pertencem a 1 bairro; 1 sensor possui N leituras.
 */
@Entity
@Table(name = "sensores")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unicidade garantida pela constraint uk_sensores_codigo (database/init.sql)
    // e verificada no SensorService (retorna 409 se repetido)
    @Column(name = "codigo", nullable = false, length = 30)
    private String codigo;

    @Column(name = "localizacao", nullable = false, length = 150)
    private String localizacao;

    @Column(name = "cota_alerta_cm", nullable = false, precision = 7, scale = 2)
    private BigDecimal cotaAlertaCm;

    @Column(name = "cota_emergencia_cm", nullable = false, precision = 7, scale = 2)
    private BigDecimal cotaEmergenciaCm;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = Boolean.TRUE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bairro_id", nullable = false)
    private Bairro bairro;

    // Ao excluir um sensor, suas leituras tambem sao excluidas
    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Leitura> leituras = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public BigDecimal getCotaAlertaCm() {
        return cotaAlertaCm;
    }

    public void setCotaAlertaCm(BigDecimal cotaAlertaCm) {
        this.cotaAlertaCm = cotaAlertaCm;
    }

    public BigDecimal getCotaEmergenciaCm() {
        return cotaEmergenciaCm;
    }

    public void setCotaEmergenciaCm(BigDecimal cotaEmergenciaCm) {
        this.cotaEmergenciaCm = cotaEmergenciaCm;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public List<Leitura> getLeituras() {
        return leituras;
    }

    public void setLeituras(List<Leitura> leituras) {
        this.leituras = leituras;
    }
}

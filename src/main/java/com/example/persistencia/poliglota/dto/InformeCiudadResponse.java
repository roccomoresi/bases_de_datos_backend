package com.example.persistencia.poliglota.dto;

public class InformeCiudadResponse {

    private String ciudad;
    private String pais;
    private Double temperaturaMax;
    private Double temperaturaMin;
    private Double temperaturaPromedio;
    private Double humedadMax;
    private Double humedadMin;
    private Double humedadPromedio;
    private Long cantidadMediciones;

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public Double getTemperaturaMax() {
        return temperaturaMax;
    }

    public void setTemperaturaMax(Double temperaturaMax) {
        this.temperaturaMax = temperaturaMax;
    }

    public Double getTemperaturaMin() {
        return temperaturaMin;
    }

    public void setTemperaturaMin(Double temperaturaMin) {
        this.temperaturaMin = temperaturaMin;
    }

    public Double getTemperaturaPromedio() {
        return temperaturaPromedio;
    }

    public void setTemperaturaPromedio(Double temperaturaPromedio) {
        this.temperaturaPromedio = temperaturaPromedio;
    }

    public Double getHumedadMax() {
        return humedadMax;
    }

    public void setHumedadMax(Double humedadMax) {
        this.humedadMax = humedadMax;
    }

    public Double getHumedadMin() {
        return humedadMin;
    }

    public void setHumedadMin(Double humedadMin) {
        this.humedadMin = humedadMin;
    }

    public Double getHumedadPromedio() {
        return humedadPromedio;
    }

    public void setHumedadPromedio(Double humedadPromedio) {
        this.humedadPromedio = humedadPromedio;
    }

    public Long getCantidadMediciones() {
        return cantidadMediciones;
    }

    public void setCantidadMediciones(Long cantidadMediciones) {
        this.cantidadMediciones = cantidadMediciones;
    }
}



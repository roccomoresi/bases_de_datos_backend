package com.example.persistencia.poliglota.dto;

public class InformeCiudadRequest {

    private String ciudad;
    private String pais;
    private String desde; // ISO-8601 (e.g., 2024-01-01T00:00:00Z)
    private String hasta; // ISO-8601

    public InformeCiudadRequest() {}

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

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public String getHasta() {
        return hasta;
    }

    public void setHasta(String hasta) {
        this.hasta = hasta;
    }
}



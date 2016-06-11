/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.restful.Data;

/**
 *
 * @author Jörg
 */
public class Beteiligung {
    private String key;
    private int anzahlFragenBeantwortet;
    private int anzahlFragen;
    private Integer schuelerId;
    private Integer betriebId;
    private String lehrerId;
    
    
    public Beteiligung() {
    }

    public Beteiligung(String key, int anzahlFragenBeantwortet, int anzahlFragen) {
        this.key = key;
        this.anzahlFragenBeantwortet = anzahlFragenBeantwortet;
        this.anzahlFragen=anzahlFragen;
    }

    public Beteiligung(String key, int anzahlFragenBeantwortet, int anzahlFragen, Integer schuelerId, Integer betriebId, String lehrerId) {
        this.key = key;
        this.anzahlFragenBeantwortet = anzahlFragenBeantwortet;
        this.anzahlFragen = anzahlFragen;
        this.schuelerId = schuelerId;
        this.betriebId = betriebId;
        this.lehrerId = lehrerId;
    }
    
    

    public Integer getBetriebId() {
        return betriebId;
    }

    public String getLehrerId() {
        return lehrerId;
    }

    public Integer getSchuelerId() {
        return schuelerId;
    }

    public void setBetriebId(Integer betriebId) {
        this.betriebId = betriebId;
    }

    public void setLehrerId(String lehrerId) {
        this.lehrerId = lehrerId;
    }

    public void setSchuelerId(Integer schuelerId) {
        this.schuelerId = schuelerId;
    }

    
    
    
    public int getAnzahlFragen() {
        return anzahlFragen;
    }

    public void setAnzahlFragen(int anzahlFragen) {
        this.anzahlFragen = anzahlFragen;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    

    public int getAnzahlFragenBeantwortet() {
        return anzahlFragenBeantwortet;
    }

    public void setAnzahlFragenBeantwortet(int anzahlFragenBeantwortet) {
        this.anzahlFragenBeantwortet = anzahlFragenBeantwortet;
    }
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.restful.Data;

/**
 * Kurse Klasse Kurs Details
 * @author Jörg
 */
public class KlasseShort {
    private String ID_LEHRER;
    private String KNAME;
    private Integer id;
    private Integer idKategorie;

    public KlasseShort() {
    }

    /**
     * Kurse Klassen / Kurs Details erzeugen
     * @param id ID der Klasse
     * @param ID_LEHRER Kürzel des Klassenlehrers
     * @param KNAME Name der Klasse
     * @param ID_KATEGORIE Kategorie der Klasse
     */
    public KlasseShort(Integer id, String ID_LEHRER, String KNAME, Integer ID_KATEGORIE) {
        this.ID_LEHRER = ID_LEHRER;
        this.KNAME = KNAME;
        this.id = id;
        this.idKategorie = ID_KATEGORIE;
    }

    public Integer getIdKategorie() {
        return idKategorie;
    }

    public void setIdKategorie(Integer idKategorie) {
        this.idKategorie = idKategorie;
    }
    
    
    
    public String getID_LEHRER() {
        return ID_LEHRER;
    }

    public void setID_LEHRER(String ID_LEHRER) {
        this.ID_LEHRER = ID_LEHRER;
    }

    public String getKNAME() {
        return KNAME;
    }

    public void setKNAME(String KNAME) {
        this.KNAME = KNAME;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    
}

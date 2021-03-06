/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.restful.Data;

import de.tuttas.util.VerspaetungsUtil;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Anwesenheit eines Shcülers
 * @author Jörg
 */
public class AnwesenheitEintrag {
    private Timestamp DATUM;
    private String ID_LEHRER;
    private int ID_SCHUELER;
    private String VERMERK;
    private String BEMERKUNG;
    private int ID_KLASSE;
    private String KRANKMELDUNG;
    
    
    private boolean parseError = false;

    public void setKRANKMELDUNG(String KRANKMELDUNG) {
        this.KRANKMELDUNG = KRANKMELDUNG;
    }

    public String getKRANKMELDUNG() {
        return KRANKMELDUNG;
    }

    
    
    public void setID_KLASSE(int ID_KLASSE) {
        this.ID_KLASSE = ID_KLASSE;
    }

    public int getID_KLASSE() {
        return ID_KLASSE;
    }

        
    public void setBEMERKUNG(String BEMERKUNG) {
        this.BEMERKUNG = BEMERKUNG;
    }

    public String getBEMERKUNG() {
        return BEMERKUNG;
    }
        
    public void setParseError(boolean b) {
        this.parseError=b;
    }
    
    public boolean getParseError() {
        return  this.parseError;
    }


    public AnwesenheitEintrag() {
    }

    public AnwesenheitEintrag(Timestamp DATUM, String ID_LEHRER, int ID_SCHUELER, String VERMERK,String KRANKMELDUNG) {
        this.DATUM = DATUM;
        this.ID_LEHRER = ID_LEHRER;
        this.ID_SCHUELER = ID_SCHUELER;
        this.VERMERK = VERMERK;
        this.KRANKMELDUNG=KRANKMELDUNG;

    }

    public void setDATUM(Timestamp DATUM) {
        this.DATUM = DATUM;
    }

    public void setID_LEHRER(String ID_LEHRER) {
        this.ID_LEHRER = ID_LEHRER;
    }

    public void setID_SCHUELER(int ID_SCHUELER) {
        this.ID_SCHUELER = ID_SCHUELER;
    }

    public void setVERMERK(String VERMERK) {
        this.VERMERK = VERMERK;
    }

    public Timestamp getDATUM() {
        return DATUM;
    }

    public String getID_LEHRER() {
        return ID_LEHRER;
    }

    public int getID_SCHUELER() {
        return ID_SCHUELER;
    }

    public String getVERMERK() {
        return VERMERK;
    }

    @Override
    public String toString() {
        return "Anwesenheit ID_SCHUELER="+ID_SCHUELER+" ID_LEHRER="+ID_LEHRER+" VERMERK="+VERMERK+" Datum="+DATUM+" ID_KLASSE="+ID_KLASSE+"\n";
    }
    
    

    
    
}

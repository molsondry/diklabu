
package de.tuttas.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * Klasse bzw. Kurs Entity
 * @author Jörg
 */
@Entity
public class Klasse implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    /**
     * Primärschlüssel
     */
    private Integer ID;
    /**
     * ID des Lehrers
     */
    private String  ID_LEHRER;
    /**
     * Name der Klasse des Kurses
     */
    private String TITEL;

    /**
     * Setzen des Names der Klasse des Kurses
     * @param TITEL der Name
     */
    public void setTITEL(String TITEL) {
        this.TITEL = TITEL;
    }

    /**
     * Setzen des Primärschlüssels für den Lehrer der Klasse des Kurses
     * @param ID_LEHRER der Primärschlüssel
     */
    public void setID_LEHRER(String ID_LEHRER) {
        this.ID_LEHRER = ID_LEHRER;
    }

    /**
     * Abfrage des Titels der Klasse des Kurses
     * @return 
     */
    public String getTITEL() {
        return TITEL;
    }

    /**
     * Abfrage des Primärschlüssels des Lehrer des Kurses
     * @return der Primärschlüssel
     */
    public String getID_LEHRER() {
        return ID_LEHRER;
    }
    
    
    /**
     * Abfrage des Primrschlüssels des Kurses
     * @return der Primärschlüssel
     */
    public Integer getId() {
        return ID;
    }

    /**
     * Setzen des Primärschlüssels des Kurses
     * @param id der Primärschlüssel
     */
    public void setId(Integer id) {
        this.ID = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ID != null ? ID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Klasse)) {
            return false;
        }
        Klasse other = (Klasse) object;
        if ((this.ID == null && other.ID != null) || (this.ID != null && !this.ID.equals(other.ID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "de.tuttas.entities.Klasse[ id=" + ID + " ]";
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.util;

import de.tuttas.entities.Kategorie;
import de.tuttas.entities.Noten;

/**
 * Hilfs Methoden zur Notenverwaltung
 * @author Jörg
 */
public class NotenUtil {
    
    private static final String NOTENWERT[] = {"?","sehr gut","gut","befriedigend","ausreichend","mangelhaft","ungenügend","teilgenommen"};
    
    /**
     * Note als Text ausgeben
     * @param Wert der Wert der Note (z.B. 1)
     * @return  Der Name der Note (z.B. sehr gut)
     */
    public static String getNote(String Wert) {        
        try {
            return NOTENWERT[Integer.parseInt(Wert)];
        }
        catch (ArrayIndexOutOfBoundsException aob) {
            return Wert;
        }
        catch (java.lang.NumberFormatException ne) {
            return Wert;
        }
    }

    /**
     * Überprüft ober die Note in die Kategorie eingetragen werden kann
     * @param n die Note
     * @param ka die Kategorie
     * @return Ergebnis der Prüfung
     */
    public static boolean gradeAllowed4Course(Noten n, Kategorie ka) {
        if (ka==null || n==null) return false;
        Log.d("Teste Noteneintrag "+n.getID_LERNFELD()+" für Klasse "+ka.getKATEGORIE());
        if (n.getID_LERNFELD().equals("Kurs") && ka.getKATEGORIE().equals("IT-WPK")) return true;
        if (n.getID_LERNFELD().startsWith("OPT") && ka.getKATEGORIE().equals("IT-CCNA")) return true;
        if (!ka.getKATEGORIE().equals("IT-WPK") && 
            !ka.getKATEGORIE().equals("IT-CCNA") && 
            !n.getID_LERNFELD().equals("Kurs") &&
            !n.getID_LERNFELD().startsWith("OPT")) return true;
        return false;
    }
}

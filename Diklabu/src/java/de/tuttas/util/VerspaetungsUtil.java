/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.util;

import de.tuttas.restful.Data.AnwesenheitEintrag;
import de.tuttas.restful.Data.AnwesenheitObjekt;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jörg
 */
public class VerspaetungsUtil {

    public static AnwesenheitObjekt parse(AnwesenheitObjekt ao) {

        for (AnwesenheitEintrag ae : ao.getEintraege()) {
            String vermerk = ae.getVERMERK();
            vermerk = vermerk.replace((char) 160, ' ');
            vermerk = vermerk.trim();
            System.out.println("parse (" + vermerk + ")");
            if (vermerk.length() > 0) {
                if (vermerk.charAt(0) == 'a') {
                    if (vermerk.length() > 1 && vermerk.charAt(1) == 'g') {
                        ao.incVerspaetungen();
                        ao.addMinutenVerspaetung(filterMinuten(vermerk, "ag"));
                        if (istEntschuldigt(vermerk, "ag")) {
                            ao.addMinutenVerspaetungEntschuldigt(filterMinuten(vermerk, "ag"));
                        }
                    }
                } else if (vermerk.charAt(0) == 'f') {
                    ao.getFehltageUnentschuldigt().add(ae);
                    ao.incFehltage();
                } else if (vermerk.charAt(0) == 'e') {
                    ao.incFehltage();
                    ao.incFehltageEntschuldigt();
                    ao.getFehltageEntschuldigt().add(ae);
                } else if (vermerk.charAt(0) == 'v') {
                    ao.incVerspaetungen();
                    int min = filterMinuten(vermerk, "v");
                    boolean e = false;
                    ao.addMinutenVerspaetung(min);
                    if (istEntschuldigt(vermerk, "v")) {
                        ao.addMinutenVerspaetungEntschuldigt(filterMinuten(vermerk, "v"));
                        e = true;
                    }
                    // Test auf v40xG90
                    int i = 0;
                    if (e) {
                        i++;
                    }
                    try {
                        vermerk = vermerk.substring(1 + Integer.toString(min).length() + i);
                        System.out.println("Vermerk ist nun (" + vermerk + ")");
                        min = filterMinuten(vermerk, "g");
                        ao.addMinutenVerspaetung(min);
                        if (istEntschuldigt(vermerk, "g")) {
                            ao.addMinutenVerspaetungEntschuldigt(filterMinuten(vermerk, "g"));
                        }
                    } catch (StringIndexOutOfBoundsException ee) {

                    }
                } else {
                    ao.getParseErrors().add(ae);

                }
            }
        }
        return ao;
    }

    public static boolean isValid(AnwesenheitEintrag ae) {
        String vermerk = ae.getVERMERK();
        return isValid(vermerk);
    }

    public static boolean isValid(String vermerk) {

        vermerk = vermerk.replace((char) 160, ' ');
        vermerk = vermerk.trim();
        vermerk = vermerk.toLowerCase();
        System.out.println("Test parse Error (" + vermerk + ")");
        Pattern p = Pattern.compile("(^v\\d+(e(g\\d+(e|)|)|g\\d+(e|)|)|^a(g\\d+(e|)|)|^e|^f)");
        Matcher m = p.matcher(vermerk);
        while (m.find()) {
            System.out.println("is Valid ist ("+m.group()+")");
            return true;
        }
        return false;
    }

    /**
     * Ermittels die Fehlminuten
     *
     * @param v Der String z.B v90
     * @param firstChar Beginn der Zeichenkette "v" f. Verspätungen "ag" f.
     * anwesend gegangen
     * @return Anzahl der Fehlminuten
     */
    public static int filterMinuten(String v, String firstChar) {
        v = v.toLowerCase();
        Pattern p = Pattern.compile("^" + firstChar + "?\\d+");
        Matcher m = p.matcher(v);
        while (m.find()) {
            String f = m.group().substring(firstChar.length());
            return Integer.parseInt(f);
        }
        return 0;
    }

    /**
     * Überprüft ob die Fehlzeiten entschuldigt sind
     *
     * @param v Der String z.B. v90e
     * @param firstChar Beginn der Zeichenkette "v" f. Verspätungen "ag" f.
     * anwesend gegangen
     * @return true f. entschuldigt, f für unentschuldigt
     */
    public static boolean istEntschuldigt(String v, String firstChar) {
        v = v.toLowerCase();
        Pattern p = Pattern.compile("^" + firstChar + "?\\d+e");
        Matcher m = p.matcher(v);
        while (m.find()) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        //System.out.println("v60test=" + filterMinuten("v60test"));
        System.out.println("v60test2=" + filterMinuten("v60test2", "v"));
        System.out.println("d60test2=" + filterMinuten("d60test2", "v"));
        System.out.println("v60etest=" + filterMinuten("v60etest", "v"));
        System.out.println("av60etest=" + filterMinuten("av60etest", "v"));
        System.out.println("aversuch90=" + filterMinuten("aversuch90", "v"));
        System.out.println("v90g90" + filterMinuten("v90g90", "v"));
        System.out.println("--");
        System.out.println("v60test2=" + istEntschuldigt("v60test2", "v"));
        System.out.println("d60test2=" + istEntschuldigt("d60test2", "v"));
        System.out.println("v60etest=" + istEntschuldigt("v60etest", "v"));
        System.out.println("av60etest=" + istEntschuldigt("av60etest", "v"));
        System.out.println("aversuch90=" + istEntschuldigt("aversuch90", "v"));
    }
}
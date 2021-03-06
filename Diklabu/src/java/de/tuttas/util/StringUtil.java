/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.util;

/**
 * Methoden zur Zeichenketten bearbeitung
 * @author Jörg
 */
public class StringUtil {

    /**
     * Deutsche Umlaute entfernen
     * @param id Der Text mit deutschen Umlauten
     * @return Der Text ohne deutsche Umlaute
     */
    public static String removeGermanCharacters(String id) {
        String out = "";
        if (id == null) {
            return null;
        }
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            switch (c) {
                case 'ö':
                    out += "oe";
                    break;
                case 'ä':
                    out += "ae";
                    break;
                case 'ü':
                    out += "ue";
                    break;
                case 'ß':
                    out += "ss";
                    break;
                case 'Ä':
                    out += "AE";
                    break;
                case 'Ö':
                    out += "OE";
                    break;
                case 'Ü':
                    out += "UE";
                    break;
                default:
                    out += c;
                    break;
            }
        }
        return out;
    }

    /**
     * Ermittelt die Levenshtein Distanz zweier Zeichenketten
     * @param lhs erste zeichenkette
     * @param rhs zweite Zeichenkette
     * @return die Lehvenshteindistanz zwischen den beiden Zeichenketten
     */
    public static int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
        //Log.d("LDist ("+lhs+") ("+rhs+")");
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        // the array of distances                                                       
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0                                 
        for (int i = 0; i < len0; i++) {
            cost[i] = i;
        }

    // dynamically computing the array of distances                                  
        // transformation cost for each letter in s1                                    
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1                             
            newcost[0] = j;

            // transformation cost for each letter in s0                                
            for (int i = 1; i < len0; i++) {
                // matching current letters in both strings                             
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation                               
                int cost_replace = cost[i - 1] + match;
                int cost_insert = cost[i] + 1;
                int cost_delete = newcost[i - 1] + 1;

                // keep minimum cost                                                    
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays                                                 
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings        
        return cost[len0 - 1];
    }

    /**
     * HTML Escape Zeichen
     * @param string Der Text ohne HTML Escape Zeichen
     * @return der Text mit Escape Zeichen
     */
    public static String escapeHtml(String string) {
        String escapedTxt = "";
        char tmp = ' ';
        for (int i = 0; i < string.length(); i++) {
            tmp = string.charAt(i);
            switch (tmp) {
                case '<':
                    escapedTxt += "&lt;";
                    break;
                case '>':
                    escapedTxt += "&gt;";
                    break;
                case '&':
                    escapedTxt += "&amp;";
                    break;
                case '"':
                    escapedTxt += "&quot;";
                    break;
                case '\'':
                    escapedTxt += "&#x27;";
                    break;
                case '/':
                    escapedTxt += "&#x2F;";
                    break;
                default:
                    escapedTxt += tmp;
            }
        }
        return escapedTxt;
    }

    /**
     * <br> Tag hinzufügen
     * @param content der Text mit CR
     * @return der Text mit <br>
     */
    public static String addBR(String content) {
        String txt = "";
        char tmp = ' ';
        for (int i = 0; i < content.length(); i++) {
            tmp = content.charAt(i);
            switch (tmp) {
                case 13:
                    txt += "<br></br>";
                    break;
                default:
                    txt += tmp;
            }
        }
        return txt;
    }

}

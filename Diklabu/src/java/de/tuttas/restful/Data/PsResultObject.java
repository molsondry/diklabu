/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.restful.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * ErgebnisObjekt mit Warnungen und Meldungen
 * @author Jörg
 */
public class PsResultObject extends ResultObject{
    
    private int[] ids ;

    /**
     * Betroffene IDs
     * @return IDs
     */
    public int[] getIds() {
        return ids;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    
}

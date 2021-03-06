/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.restful;

import de.tuttas.entities.Schuljahr;
import de.tuttas.restful.Data.KlasseDetails;
import de.tuttas.util.Log;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Webservice zum Verwalten der Schuljahre
 * @author Jörg
 */
@Path("schuljahr")
@Stateless
public class SchuljahrManager {
     /**
     * Injection des EntityManagers
     */
    @PersistenceContext(unitName = "DiklabuPU")
    EntityManager em;
    
    /**
     * Abfrage des aktuellen Schuljahrs
     * @return aktuelles Schuljahr
     */
    @GET
    public Schuljahr getSchuljahr() {
        Query query = em.createNamedQuery("getLatestSchuljahr").setMaxResults(1);        
        List<Schuljahr> jahr = query.getResultList();
        return jahr.get(0);
    }
    
    /**
     * Abfrage aller Schuljahr
     * @return Liste der Schuljahre
     */
    @GET
    @Path("all")
    public List<Schuljahr> getSchuljahre() {
        Query query = em.createNamedQuery("getSchuljahre");        
        List<Schuljahr> jahre = query.getResultList();
        Log.d("Schuljahr All Result List="+jahre);
        return jahre;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.restful;

import de.tuttas.entities.Buchungsfreigabe;
import de.tuttas.entities.Klasse;
import de.tuttas.entities.Konfig;
import de.tuttas.entities.Kurswunsch;
import de.tuttas.entities.KurswunschId;
import de.tuttas.entities.Schueler;
import de.tuttas.entities.Schueler_Klasse;
import de.tuttas.restful.Data.PsResultObject;
import de.tuttas.restful.Data.ResultObject;
import de.tuttas.util.Log;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author Jörg
 */
@Path("coursevoting")
@Stateless
public class CourseVotingManager {

    /**
     * Injection des EntityManagers
     */
    @PersistenceContext(unitName = "DiklabuPU")
    EntityManager em;
    
    @POST
    @Path("admin/{idklasse}")
    @Produces({"application/json; charset=iso-8859-1"})
    public ResultObject addCourse(@PathParam("idklasse") int kid) {
        Log.d("Kurs zur Kurwahl hinzufügen:" + kid);
        ResultObject ro = new ResultObject();
        Klasse k = em.find(Klasse.class, kid);
        if (k != null) {
            Buchungsfreigabe bf = em.find(Buchungsfreigabe.class, kid);
            if (bf == null) {
                bf = new Buchungsfreigabe(kid);
                em.persist(bf);
                ro.setSuccess(true);
                ro.setMsg("Kurs " + k.getKNAME() + " zur Kurswahl hinzugefügt!");
            } else {
                ro.setSuccess(false);
                ro.setMsg("Kurs " + k.getKNAME() + " bereits in Kurswahl enthalten!");
            }
        } else {
            ro.setSuccess(false);
            ro.setMsg("Kann keinen Kurs mit der ID " + kid + " finden!");
        }
        return ro;
    }
    
    @DELETE
    @Path("admin/{idklasse}")
    @Produces({"application/json; charset=iso-8859-1"})
    public PsResultObject deleteCourse(@PathParam("idklasse") int kid
    ) {
        Log.d("Kurs " + kid + " Aus Kurswahl entfernen");
        PsResultObject ro = new PsResultObject();
        Klasse k = em.find(Klasse.class, kid);
        if (k != null) {
            Buchungsfreigabe bf = em.find(Buchungsfreigabe.class, kid);
            if (bf != null) {
                Query query = em.createNamedQuery("findWunschByKlassenId");
                query.setParameter("paramId", kid);
                List<Kurswunsch> wunsche = query.getResultList();
                if (wunsche.size() == 0) {
                    em.remove(bf);
                    ro.setSuccess(true);
                    ro.setMsg("Kurs " + k.getKNAME() + " aus Kurswahl entfernt!");
                } else {
                    ro.setSuccess(false);
                    ro.setMsg("Kurs " + k.getKNAME() + " kann nicht aus Kurswahl entfernt werden, da er bereits gewählt wurde!");
                    int[] ids = new int[wunsche.size()];
                    for (int i = 0; i < wunsche.size(); i++) {
                        ids[i] = wunsche.get(i).getID_SCHUELER();
                    }
                    ro.setIds(ids);
                }
            } else {
                ro.setSuccess(false);
                ro.setMsg("Kann Kurs " + k.getKNAME() + " nicht in der Kurswahl finden!");
            }
        } else {
            ro.setSuccess(false);
            ro.setMsg("Kann Kurs mit id " + kid + " nicht finden!");
        }
        return ro;
    }
    
    @POST
    @Path("admin/voting/{state}")
    @Produces({"application/json; charset=iso-8859-1"})
    public ResultObject enableDisableVoting(@PathParam("state") int v) {
        Log.d("Kurswahl freischalten/sperren = " + v);
        ResultObject ro = new ResultObject();
        Konfig k = em.find(Konfig.class, "kursbuchung");
        k.setSTATUS(v);
        em.merge(k);
        if (v == 1) {
            ro.setMsg("Kurswahl freigeschaltet");
        } else {
            ro.setMsg("Kurswahl gesperrt");
        }
        ro.setSuccess(true);
        return ro;
    }
    
    @DELETE
    @Path("admin/schueler/{idschueler}") 
    @Produces({"application/json; charset=iso-8859-1"})
    public ResultObject deleteCourseVoting(@PathParam("idschueler") int sid) {
        Log.d("Schüler " + sid + " Kurswunsch zurück setzten");
        ResultObject ro = new ResultObject();
        Query query = em.createNamedQuery("findWunschBySchuelerId");
        query.setParameter("paramId", sid);
        List<Kurswunsch> wunsche = query.getResultList();
        for (Kurswunsch kw : wunsche) {
            em.remove(kw);
        }
        ro.setMsg("Kurswüsche entfernt");
        ro.setSuccess(true);
        return ro;
    }
    
     @DELETE
    @Path("admin/")
    @Produces({"application/json; charset=iso-8859-1"})
    public ResultObject deleteCourseVoting() {
        Log.d("Alle Kurswünsch zurück setzten");
        ResultObject ro = new ResultObject();
        Query q3 = em.createNativeQuery("DELETE FROM Kurswunsch");
        q3.executeUpdate();
        
        ro.setMsg("Alle Kurswüsche entfernt");
        ro.setSuccess(true);
        return ro;
    }
    
    @GET
    @Path("{klassenid}/{prio}")
    @Produces({"application/json; charset=iso-8859-1"})
    public List<Schueler> getVotings(@PathParam("klassenid") int kid,@PathParam("prio") String prio) {
        
        Query query = em.createNamedQuery("findWunschByKlasseAndPrio");
        query.setParameter("paramId", kid);
        query.setParameter("paramPrio", prio);
        List<Schueler> schueler = query.getResultList();
        return schueler;
    }
    @GET
    @Produces({"application/json; charset=iso-8859-1"})
    public List<Klasse> listVotings() {        
        Query query = em.createNamedQuery("findBuchungsfreigabe");
        List<Klasse> klassen = query.getResultList();
        return klassen;
    }
    
    @POST
    @Path("admin/schueler/")
    @Produces({"application/json; charset=iso-8859-1"})
    public ResultObject setVoting(Kurswunsch kw) {
        Log.d("setzte Kurswunsch = " + kw);
        ResultObject ro = new ResultObject();
        Schueler s = em.find(Schueler.class, kw.getID_SCHUELER());
        if (s!=null) {
            Klasse k = em.find(Klasse.class, kw.getID_KURS());
            Log.d("Klasse ist:"+k);
            if (k!=null) {
                Kurswunsch sk = em.find(Kurswunsch.class, new KurswunschId(kw.getID_SCHUELER(),kw.getID_KURS()));
                if (sk==null) {
                    em.persist(kw);
                    em.flush();
                    ro.setSuccess(true);
                    ro.setMsg("Schüler "+s.getVNAME()+" "+s.getNNAME()+" hat Kurs '"+k.getTITEL()+"' mit Priorität "+kw.getPRIORITAET()+" gewählt");
                }
                else {
                    ro.setSuccess(false);
                    ro.setMsg("Schüler "+s.getVNAME()+" "+s.getNNAME()+" hatte bereits Kurs '"+k.getTITEL()+"' gewählt");                    
                }
            }
            else {
                ro.setMsg("Kann Klasse mit id "+kw.getID_KURS()+" nicht finden!");
                ro.setSuccess(false);                                
            }
        }
        else {
            ro.setMsg("Kann Schüler mit id "+kw.getID_SCHUELER()+" nicht finden!");
            ro.setSuccess(false);
        }
                
        return ro;
    }
}

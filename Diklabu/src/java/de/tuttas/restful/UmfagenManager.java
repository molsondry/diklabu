/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.restful;

import de.tuttas.entities.Antworten;
import de.tuttas.entities.Antwortskalen;
import de.tuttas.entities.Anwesenheit;
import de.tuttas.entities.Fragen;
import de.tuttas.entities.Schueler;
import de.tuttas.entities.Umfrage;
import de.tuttas.entities.Verlauf;
import de.tuttas.restful.Data.ActiveUmfrage;
import de.tuttas.restful.Data.AntwortSkalaObjekt;
import de.tuttas.restful.Data.AntwortUmfrage;
import de.tuttas.restful.Data.AnwesenheitEintrag;
import de.tuttas.restful.Data.Beteiligung;
import de.tuttas.restful.Data.FragenObjekt;
import de.tuttas.restful.Data.KlasseDetails;
import de.tuttas.restful.Data.ResultObject;
import de.tuttas.restful.Data.UmfrageObjekt;
import de.tuttas.restful.Data.UmfrageResult;
import de.tuttas.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * GET: /umfrage/fragen/{UMFRAGE ID} .. zeigt alle Fragen und
 * Antwortsmöglichkeiten an ! POST: /umfrage/antwort/ mit AntortObjekt ..
 * (setzten einer Antwort) GET: /umfrage/antworten/{ID_KLASSE} .. alle Antworten
 * einer Klasse GET: /umfrage/antworten/regex/{regex} .. alle Antworten der
 * Klassennamen die dem Regex entsprechen
 *
 * @author Jörg
 */
@Path("umfrage")
@Stateless
public class UmfagenManager {

    /**
     * Injection des EntityManagers
     */
    @PersistenceContext(unitName = "DiklabuPU")
    EntityManager em;

    @GET
    @Produces({"application/json; charset=iso-8859-1"})
    public List<ActiveUmfrage> getUmfragen() {
        System.out.println("Umfragen abfragen");
        Query query = em.createNamedQuery("findAllUmfragen");
        List<ActiveUmfrage> umfrage = query.getResultList();
        Log.d("Result List:" + umfrage);
        return umfrage;
    }

    @GET
    @Path("beteiligung/{uid}/{kname}")
    public List<Beteiligung> getBeteiligung(@PathParam("uid") int uid, @PathParam("kname") String kname) {
        System.out.println("Get Beteiligung f. Umfrage " + uid + " und Klasse =" + kname);
        em.getEntityManagerFactory().getCache().evictAll();
        Umfrage u = em.find(Umfrage.class, uid);
        if (u == null) {
            return null;
        }
        List<Beteiligung> beteiligungen = new ArrayList<>();
        Query q = em.createQuery("SELECT a.ID_SCHUELER,COUNT(a.ID_SCHUELER) from Antworten a inner join Schueler s on a.ID_SCHUELER=s.ID inner join Schueler_Klasse sk on s.ID=sk.ID_SCHUELER inner join Klasse k on sk.ID_KLASSE=k.ID WHERE a.ID_UMFRAGE=" + uid + " AND k.KNAME like '" + kname + "' Group by a.ID_SCHUELER");
        List<Object[]> r = q.getResultList();
        for (int i = 0; i < r.size(); i++) {
            Object[] ro = r.get(i);
            System.out.println("Der Schueler mit der ID " + ro[0] + " hat " + ro[1] + " Fragen beantwortet!");
            Integer idSchueler = (Integer) ro[0];
            Long fragen = (Long) ro[1];
            Beteiligung b = new Beteiligung(idSchueler.intValue(), fragen.intValue(), u.getFragen().size());
            beteiligungen.add(b);
        }
        return beteiligungen;
    }

    @GET
    @Path("auswertung/{uid}/{klassenname}")
    @Produces({"application/json; charset=iso-8859-1"})
    public List<UmfrageResult> getAntwortenKlasse(@PathParam("uid") int uid, @PathParam("klassenname") String kname) {
        System.out.println("Get Antworten f. Umfrage " + uid + " und Klasse =" + kname);
        em.getEntityManagerFactory().getCache().evictAll();
        List<UmfrageResult> resultList = new ArrayList<UmfrageResult>();
        Umfrage u = em.find(Umfrage.class, uid);
        if (u == null) {
            return null;
        }
        for (Fragen f : u.getFragen()) {
            UmfrageResult ur = new UmfrageResult(f.getTITEL(), f.getID_FRAGE());
            Map<Integer, AntwortSkalaObjekt> antworten = new HashMap();
            for (Antwortskalen skalen : f.getAntwortskalen()) {
                antworten.put(skalen.getID(), new AntwortSkalaObjekt(skalen.getNAME(), skalen.getID(), 0));
            }
            //Query q = em.createQuery("select aska.NAME,count(a.antwortskala) from Antworten a inner join a.antwortskala aska inner join Schueler s on a.ID_SCHUELER=s.ID inner join Schueler_Klasse sk on s.id=sk.ID_SCHUELER inner join Klasse k on k.ID=sk.IK_KLASSE Group by a.fragenAntworten");
            System.out.println("Frage=" + f);
            Query q = em.createQuery("SELECT a.antwortskala,COUNT(a.antwortskala) from Antworten a inner join a.antwortskala aska inner join Schueler s on a.ID_SCHUELER=s.ID inner join Schueler_Klasse sk on s.ID=sk.ID_SCHUELER inner join Klasse k on sk.ID_KLASSE=k.ID WHERE a.ID_UMFRAGE=" + uid + " AND k.KNAME like '" + kname + "' and a.fragenAntworten.ID_FRAGE=" + f.getID_FRAGE() + " Group by a.antwortskala");
            List<Object[]> r = q.getResultList();
            for (int i = 0; i < r.size(); i++) {
                Object[] ro = r.get(i);
                Antwortskalen a = (Antwortskalen) ro[0];
                System.out.println("Die Antwort " + ro[0] + " Wurde " + ro[1] + " mal gewählt!");
                antworten.put(a.getID(), new AntwortSkalaObjekt(a.getNAME(), a.getID().intValue(), (long) ro[1]));
            }

            Iterator it = antworten.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                it.remove(); // avoids a ConcurrentModificationException
                ur.getSkalen().add((AntwortSkalaObjekt) pair.getValue());
            }

            resultList.add(ur);
        }
        System.out.println("resultList=" + resultList);
        return resultList;
    }

    @GET
    @Path("admin/frage/{fid}")
    @Produces({"application/json; charset=iso-8859-1"})
    public FragenObjekt getFrage(@PathParam("fid") int fid) {
        System.out.println("get Fragen:" + fid);
        Fragen f = em.find(Fragen.class, fid);
        if (f != null) {
            
            FragenObjekt fro = new FragenObjekt(f.getTITEL());
            fro.setId(f.getID_FRAGE());
            for (Antwortskalen skalen : f.getAntwortskalen()) {
                fro.getIDantworten().add(skalen.getID());
                fro.getStringAntworten().add(skalen.getNAME());
            }                        
            return fro;
        }
        return null;
    }

    @POST
    @Path("admin/frage")
    @Produces({"application/json; charset=iso-8859-1"})
    public FragenObjekt newFrage(FragenObjekt fo) {
        System.out.println("newFragenobjekt:" + fo);
        Fragen f = new Fragen(fo.getFrage());
        em.persist(f);
        em.flush();
        FragenObjekt fro = new FragenObjekt(f.getTITEL());
        fro.setId(f.getID_FRAGE());
        return fro;
    }

    @PUT
    @Path("admin/frage/{fid}")
    @Produces({"application/json; charset=iso-8859-1"})
    public FragenObjekt setFrage(FragenObjekt fo) {
        System.out.println("setFragenobjekt:" + fo);
        Fragen f = em.find(Fragen.class, fo.getId());
        if (f != null) {
            f.setTITEL(fo.getFrage());
            em.merge(f);
            em.flush();
            FragenObjekt fro = new FragenObjekt(f.getTITEL());
            fro.setId(f.getID_FRAGE());
            return fro;
        }
        return null;
    }

    @DELETE
    @Path("admin/frage/{fid}")
    @Produces({"application/json; charset=iso-8859-1"})
    public FragenObjekt setFrage(@PathParam("fid") int fid) {
        System.out.println("Detele Fragenobjekt id=:" + fid);
        Fragen f = em.find(Fragen.class, fid);
        if (f != null) {
            em.remove(f);
            em.flush();
            FragenObjekt fro = new FragenObjekt(f.getTITEL());
            fro.setId(f.getID_FRAGE());
            return fro;
        }
        return null;
    }
    
     @GET
    @Path("admin/antwort/{aid}")
    @Produces({"application/json; charset=iso-8859-1"})
    public AntwortSkalaObjekt getAntwort(@PathParam("aid") int aid) {
        System.out.println("get Antwort:" + aid);
        Antwortskalen a = em.find(Antwortskalen.class, aid);
        if (a != null) {
            AntwortSkalaObjekt aso = new AntwortSkalaObjekt();
            aso.setId(a.getID());
            aso.setName(a.getNAME());
            return aso;
        }
        return null;
    }
    @GET
    @Path("admin/antwort/")
    @Produces({"application/json; charset=iso-8859-1"})
    public List<AntwortSkalaObjekt> listAntwort() {
        
        System.out.println("list Antworten:");
        Query query = em.createNamedQuery("getAntwortenSkalen");       
        List<Antwortskalen> as = query.getResultList();
        List<AntwortSkalaObjekt> asol = new ArrayList<>();
        for (int i=0;i<as.size();i++) {
            Antwortskalen a = as.get(i);
            AntwortSkalaObjekt aso = new AntwortSkalaObjekt();
            aso.setId(a.getID());
            aso.setName(a.getNAME());            
            asol.add(aso);            
        } 
        return asol;
        
    }
    @GET
    @Path("admin/frage/")
    @Produces({"application/json; charset=iso-8859-1"})
    public List<FragenObjekt> listFrage() {
        
        System.out.println("list Fragen:");
        Query query = em.createNamedQuery("getFragen");       
        List<Fragen> fr = query.getResultList();
        List<FragenObjekt> frol = new ArrayList<>();
        for (int i=0;i<fr.size();i++) {
            Fragen f = fr.get(i);
            FragenObjekt fo= new FragenObjekt();
            fo.setId(f.getID_FRAGE());
            fo.setFrage(f.getTITEL());
            for (Antwortskalen as : f.getAntwortskalen()) {
                fo.getIDantworten().add(as.getID());
                fo.getStringAntworten().add(as.getNAME());
            }
            frol.add(fo);            
        } 
        return frol;
        
    }

    @POST
    @Path("admin/antwort")
    @Produces({"application/json; charset=iso-8859-1"})
    public AntwortSkalaObjekt newAntwort(AntwortSkalaObjekt ao) {
        System.out.println("newAntwortobjekt:" + ao);
        Antwortskalen a = new Antwortskalen();
        a.setNAME(ao.getName());        
        em.persist(a);
        em.flush();
        ao.setId(a.getID());
        return ao;
    }

    @PUT
    @Path("admin/antwort")
    @Produces({"application/json; charset=iso-8859-1"})
    public AntwortSkalaObjekt setAntwort(AntwortSkalaObjekt ao) {
        System.out.println("setAntwortobjekt:" + ao);
        Antwortskalen a = em.find(Antwortskalen.class, ao.getId());
        if (a != null) {
            a.setNAME(ao.getName());            
            em.merge(a);
            em.flush();
            return ao;
        }
        return null;
    }

    @DELETE
    @Path("admin/antwort/{aid}")
    @Produces({"application/json; charset=iso-8859-1"})
    public AntwortSkalaObjekt setAntwort(@PathParam("aid") int aid) {
        System.out.println("Detele Antwortobjekt id=:" + aid);
        Antwortskalen a = em.find(Antwortskalen.class, aid);
        if (a != null) {
            em.remove(a);
            em.flush();
            AntwortSkalaObjekt aso = new AntwortSkalaObjekt();
            aso.setId(a.getID());
            aso.setName(a.getNAME());            
            return aso;
        }
        return null;
    }
    
    @POST
    @Path("admin/add/{fid}/{aid}")
    @Produces({"application/json; charset=iso-8859-1"})
    public ResultObject addAntwort(@PathParam("fid") int fid,@PathParam("aid") int aid) {
        System.out.println("addAntwort: ID=" + aid +" to Frage ID="+fid);
        ResultObject ro = new ResultObject();
        Fragen f = em.find(Fragen.class, fid);
        if (f!=null) {
             Antwortskalen as = em.find(Antwortskalen.class, aid);
             if (as!=null) {
                 if (f.getAntwortskalen().contains(as)) {
                    ro.setSuccess(false);
                    ro.setMsg("Die Frage ("+f.getTITEL()+") enthält bereits die Antwort ("+as.getNAME()+") !");                     
                 }
                 else {
                    f.getAntwortskalen().add(as);
                    em.merge(f);
                    ro.setSuccess(true);
                    ro.setMsg("Habe der Frage ("+f.getTITEL()+") die Antwort ("+as.getNAME()+") hinzugefügt!");
                 }
             }
             else {
                 ro.setMsg("Kann Antwort mit ID="+aid+" nicht finden!");
                 ro.setSuccess(false);
             }
        }
        else {
            ro.setSuccess(false);
            ro.setMsg("Kann Frage mit ID="+fid+" nicht finden!");
        }
        return ro;
    }
    
    @POST
    @Path("admin/remove/{fid}/{aid}")
    @Produces({"application/json; charset=iso-8859-1"})
    public ResultObject removeAntwort(@PathParam("fid") int fid,@PathParam("aid") int aid) {
        System.out.println("removeAntwort: ID=" + aid +" from Frage ID="+fid);
        ResultObject ro = new ResultObject();
        Fragen f = em.find(Fragen.class, fid);
        if (f!=null) {
             Antwortskalen as = em.find(Antwortskalen.class, aid);
             if (as!=null) {
                 if (f.getAntwortskalen().contains(as)) {
                    f.getAntwortskalen().remove(as);
                    em.merge(f);
                    ro.setSuccess(true);
                    ro.setMsg("Habe die Antwort ("+as.getNAME()+") aus der Frage ("+f.getTITEL()+") entfernt!");
                 }
                 else {
                     ro.setSuccess(false);
                     ro.setMsg("Die Antwort ("+as.getNAME()+") ist nicht in der Frage ("+f.getTITEL()+") enthalten!");
                 }
             }
             else {
                 ro.setMsg("Kann Antwort mit ID="+aid+" nicht finden!");
                 ro.setSuccess(false);
             }
            
        }
        else {
            ro.setSuccess(false);
            ro.setMsg("Kann Frage mit ID="+fid+" nicht finden!");
        }
        return ro;
    }

}

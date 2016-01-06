/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.servlets;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import de.tuttas.config.Config;
import de.tuttas.entities.Klasse;
import de.tuttas.entities.Schueler;
import de.tuttas.entities.Verlauf;
import de.tuttas.restful.Data.AnwesenheitEintrag;
import de.tuttas.restful.Data.AnwesenheitObjekt;
import de.tuttas.restful.auth.Authenticator;
import de.tuttas.util.DatumUtil;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jörg
 */
public class DokuServlet extends HttpServlet {

    /**
     * Injection des EntityManagers
     */
    @PersistenceContext(unitName = "DiklabuPU")
    EntityManager em;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String auth = request.getParameter("auth_token");
        String service = request.getParameter("service_key");
        System.out.println("auth_token=" + auth);
        if (request.getParameter("cmd") == null || request.getParameter("idklasse") == null || request.getParameter("from") == null) {
            System.out.println("Info zeigen");
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                /* TODO output your page here. You may use following sample code. */
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Doku Servlet Usage</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Doku Servlet @ " + request.getContextPath() + "</h1>");
                out.println("<a href='?cmd=Verlauf&idklasse=3608&from=2015-09-08&debug=" + Config.debug + "' target='_pdf'>Usage: ?cmd=verlauf&idklasse=3608&from=2015-09-08</a>");
                out.println("</body>");
                out.println("</html>");
            }
        } else {
            if (de.tuttas.config.Config.debug || service != null && auth != null && Authenticator.getInstance().isAuthTokenValid(service, auth)) {
                Klasse kl = em.find(Klasse.class, Integer.parseInt(request.getParameter("idklasse")));
                String cmd = request.getParameter("cmd");
                System.out.println("cmd=" + cmd + " Klasse=" + kl.getKNAME());
                response.setContentType("application/pdf");
                //Get the output stream for writing PDF object        
                OutputStream out = response.getOutputStream();
                try {
                    Document document = new Document();
                    /* Basic PDF Creation inside servlet */
                    PdfWriter writer = PdfWriter.getInstance(document, out);
                    StringBuilder htmlString = new StringBuilder();
                    /* Nun das Template laden */
                    /*
                     ServletContext cntxt = this.getServletContext();
                     String fName = "/temp-verlauf.html";
                     System.out.println("Lade Template: " + cntxt.getRealPath(fName));
                     InputStream ins = cntxt.getResourceAsStream(fName);
                     if (ins != null) {
                     InputStreamReader isr = new InputStreamReader(ins);
                     BufferedReader reader = new BufferedReader(isr);
                     String word = "";
                     while ((word = reader.readLine()) != null) {
                     System.out.println("word=" + word);
                     htmlString.append(word);
                     }
                     } else {
                     System.out.println("Kann Template nicht finden");
                     }
                     */
                    String kopf = "";
                    kopf += ("<table border='1' align='center' width='100%'>");
                    kopf += ("<tr>");
                    kopf += ("<td rowspan=\"3\" width='150px'></td>");
                    kopf += ("<td align='center'><h2>Multi Media Berufsbildende Schulen Hannover</h2></td>");
                    if (cmd.compareTo("Verlauf") == 0) {
                        kopf += ("<td colspan=\"2\" align='center'><b>Digitales Klassenbuch Unterrichtsverlauf</b></td>");
                    } else if (cmd.compareTo("Anwesenheit") == 0) {
                        kopf += ("<td colspan=\"2\" align='center'><b>Digitales Klassenbuch Anwesenheit</b></td>");

                    }
                    kopf += ("</tr>");
                    kopf += ("<tr>");
                    kopf += ("<td  align='center' rowspan=\"2\"><h3>Klasse/ Kurs: " + kl.getKNAME() + "</h3></td>");
                    kopf += ("<td  style=\"font-size: 11;\">Verantwortlicher: " + kl.getID_LEHRER() + "</td>");
                    kopf += ("<td  style=\"font-size: 11;\">geprüft</td>");
                    kopf += ("</tr>");
                    kopf += ("<tr>");
                    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                    Calendar c = df.getCalendar();
                    c.setTimeInMillis(System.currentTimeMillis());
                    String dat = c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR);
                    kopf += ("<td  style=\"font-size: 11;\">Ausdruck am: " + dat + "</td>");
                    kopf += ("<td  style=\"font-size: 11;\">Datum</td>");
                    kopf += ("</tr>");
                    kopf += ("</table>");
                    kopf += ("<p>&nbsp;</p>");
                    htmlString.append(kopf);

                    if (cmd.compareTo("Verlauf") == 0) {
                        htmlString = createVerlauf(kl, request, response, htmlString, document, writer, kopf);
                    } else if (cmd.compareTo("Anwesenheit") == 0) {
                        htmlString = createAnwesenheit(kl, request, response, htmlString, document, writer, kopf);

                    }

                    //document.add(new Paragraph("Tutorial to Generate PDF using Servlet"));
                    InputStream is = new ByteArrayInputStream(htmlString.toString().getBytes());
                    // Bild einfügen
                    String url = "http://www.mmbbs.de/fileadmin/template/mmbbs/gfx/mmbbs_logo_druck.gif";
                    Image image = Image.getInstance(url);
                    image.setAbsolutePosition(45f, 720f);
                    image.scalePercent(50f);
                    document.add(image);
                    XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
                    document.close();

                } catch (DocumentException exc) {
                    throw new IOException(exc.getMessage());
                } catch (ParseException ex) {
                    Logger.getLogger(DokuServlet.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    out.close();
                }

            } else {
                response.setContentType("text/html;charset=UTF-8");
                try (PrintWriter out = response.getWriter()) {
                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>Doku Servlet Usage</title>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<h1>You are not authorized</h1>");
                    out.println("</body>");
                    out.println("</html>");
                }
            }
        }
    }

    private StringBuilder createAnwesenheit(Klasse kl, HttpServletRequest request, HttpServletResponse response, StringBuilder htmlString, Document document, PdfWriter writer, String kopf) throws ParseException, IOException, DocumentException {
        /* Anwesenheit einfügen */
        TypedQuery<AnwesenheitEintrag> query = em.createNamedQuery("findAnwesenheitbyKlasse", AnwesenheitEintrag.class);
        query.setParameter("paramKName", kl.getKNAME());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedFrom = (Date) dateFormat.parse(request.getParameter("from"));
        query.setParameter("paramFromDate", new java.sql.Date(parsedFrom.getTime()));
        System.out.println("setze From auf " + new java.sql.Date(parsedFrom.getTime()));
        Date parsedTo;
        if (request.getParameter("to") == null) {
            query.setParameter("paramToDate", new java.sql.Date(System.currentTimeMillis()));
            parsedTo = new java.sql.Date(System.currentTimeMillis());
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            parsedTo = (Date) dateFormat.parse(request.getParameter("to"));
            query.setParameter("paramToDate", new java.sql.Date(parsedTo.getTime()));
        }
        System.out.println("setze To auf " + new java.sql.Date(parsedTo.getTime()));
        response.addHeader("Content-Disposition", "attachment; filename=Anwesenheit_" + kl.getKNAME() + "_" + new java.sql.Date(parsedFrom.getTime()).toString() + "-" + new java.sql.Date(parsedTo.getTime()).toString() + ".pdf");
        List<AnwesenheitObjekt> anwesenheit = getData(query);
        
        
        System.out.println("Result List:" + anwesenheit);
        GregorianCalendar c = (GregorianCalendar) GregorianCalendar.getInstance();
        c.setTime(parsedFrom);
        System.out.println("KW beginnt bei " + c.get(GregorianCalendar.WEEK_OF_YEAR));

        int spalte = 0;
        String tagZeile = "";
        document.open();
        Query q = em.createNamedQuery("findSchuelerEinerBenanntenKlasse");
        q.setParameter("paramNameKlasse", kl.getKNAME());
        List<Schueler> schueler = q.getResultList();
        Date current = new Date(parsedFrom.getTime());
        while (!parsedFrom.after(parsedTo)) {
            tagZeile += "<table  align='center' width='100%' style=\"border: 2px solid black; border-collapse: collapse;\">\n";
            tagZeile += ("<tr >\n");
            tagZeile += ("<td width='25%' style=\"font-size: 14;border: 1px solid black;\"><b>Name</b></td>\n");
            spalte = 0;
            // Zeile f.  Tage
            while (spalte < 7 && !current.after(parsedTo)) {
                c.setTime(current);
                if (c.get(GregorianCalendar.DAY_OF_WEEK) == 1 || c.get(GregorianCalendar.DAY_OF_WEEK) == 7) {
                    tagZeile += ("<td align=\"center\" style=\"padding:5px; background-color: #cccccc; font-size: 12;border: 1px solid black;\">" + DatumUtil.getWochentag(c.get(GregorianCalendar.DAY_OF_WEEK)) + "<br></br>" + c.get(GregorianCalendar.DATE) + "." + (c.get(GregorianCalendar.MONTH) + 1) + "." + c.get(GregorianCalendar.YEAR) + "</td>\n");
                } else {
                    tagZeile += ("<td align=\"center\" style=\"padding: 5px; font-size: 12;border: 1px solid black;\">" + DatumUtil.getWochentag(c.get(GregorianCalendar.DAY_OF_WEEK)) + "<br></br>" + c.get(GregorianCalendar.DATE) + "." + (c.get(GregorianCalendar.MONTH) + 1) + "." + c.get(GregorianCalendar.YEAR) + "</td>\n");
                }
                System.out.println("Spalte " + spalte + " Datum=" + current);
                current = new Date(current.getTime() + 24 * 60 * 60 * 1000);
                spalte++;
            }
            tagZeile += "</tr>\n";
            // Zeile pro Name
            for (Schueler s : schueler) {
                tagZeile += "<tr>\n";
                tagZeile += ("<td width='20%' style=\"padding: 5px;font-size: 12;border: 1px solid black;\"><b>" + s.getVNAME() + " " + s.getNNAME() + "</b></td>\n");
                // Zeile f.  Tage
                System.out.println("Zeile f. Schüler " + s.getNNAME());
                spalte = 0;
                current = new Date(parsedFrom.getTime());
                while (spalte < 7 && !current.after(parsedTo)) {
                    c.setTime(current);
                    if (c.get(GregorianCalendar.DAY_OF_WEEK) == 1 || c.get(GregorianCalendar.DAY_OF_WEEK) == 7) {
                        tagZeile += ("<td style=\"background-color:#cccccc;font-size: 11;border: 1px solid black;\">"+findVermerk(s.getId(),current,anwesenheit)+"</td>\n");
                    } else {
                        tagZeile += ("<td style=\"font-size: 11;border: 1px solid black;\">"+findVermerk(s.getId(),current,anwesenheit)+"</td>\n");
                    }
                    System.out.println("Zeile f. Schüler " + s.getNNAME() + " Datum " + current);
                    current = new Date(current.getTime() + 24 * 60 * 60 * 1000);
                    c.setTime(parsedFrom);
                    spalte++;
                }
                tagZeile += "</tr>\n";
            }
            parsedFrom = new Date(current.getTime());
            if (spalte == 7) {
                tagZeile += "</table>\n";
                htmlString.append(tagZeile);
                InputStream is = new ByteArrayInputStream(htmlString.toString().getBytes());
                // Bild einfügen
                String url = "http://www.mmbbs.de/fileadmin/template/mmbbs/gfx/mmbbs_logo_druck.gif";
                Image image = Image.getInstance(url);
                image.setAbsolutePosition(45f, 720f);
                image.scalePercent(50f);
                document.add(image);
                XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
                document.newPage();
                System.out.println("neue Seite html=" + htmlString);
                htmlString = new StringBuilder();
                tagZeile = "";
                htmlString.append(kopf);
            }
            System.out.println("parsedFrom ist nun " + parsedFrom);
        }
        if (spalte < 7) {

            tagZeile += "</table>\n";
            htmlString.append(tagZeile);
        }

        System.out.println("fertig");

        /*
         for (Schueler s : schueler) {
         htmlString.append("<tr>");
         htmlString.append("<td style=\"font-size: 11;border: 1px solid black;\">" + s.getVNAME() + " " + s.getNNAME() + "</td>");
         spalte = 0;
         while (spalte <= 7) {
         //htmlString.append("<td style=\"font-size: 11;border: 1px solid black;\">" + getAnwesenheit(s.getId(), anwesenheit,parsedFrom) + "</td>");
         htmlString.append("<td style=\"font-size: 11;border: 1px solid black;\">x</td>");
         parsedFrom = new Date(parsedFrom.getTime() + 1000 * 60 * 60 * 24);
         spalte++;
         }
         htmlString.append("</tr>");
         }

         }
         */
        System.out.println("html String =" + htmlString.toString());
        return htmlString;
    }

    private StringBuilder createVerlauf(Klasse kl, HttpServletRequest request, HttpServletResponse response, StringBuilder htmlString, Document document, PdfWriter writer, String kopf) throws ParseException, IOException, DocumentException {
        /* Verlauf einfügen */
        Query query = em.createNamedQuery("findVerlaufbyKlasse");
        query.setParameter("paramKName", kl.getKNAME());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedFrom = (Date) dateFormat.parse(request.getParameter("from"));
        query.setParameter("paramFromDate", new java.sql.Date(parsedFrom.getTime()));
        System.out.println("setze From auf " + new java.sql.Date(parsedFrom.getTime()));
        Date parsedTo;
        if (request.getParameter("to") == null) {
            query.setParameter("paramToDate", new java.sql.Date(System.currentTimeMillis()));
            parsedTo = new java.sql.Date(System.currentTimeMillis());
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            parsedTo = (Date) dateFormat.parse(request.getParameter("to"));
            query.setParameter("paramToDate", new java.sql.Date(parsedTo.getTime()));
        }
        response.addHeader("Content-Disposition", "attachment; filename=verlauf_" + kl.getKNAME() + "_" + new java.sql.Date(parsedFrom.getTime()).toString() + "-" + new java.sql.Date(parsedTo.getTime()).toString() + ".pdf");
        List<Verlauf> verlauf = query.getResultList();
        System.out.println("Result List:" + verlauf);
        htmlString.append("<table  align='center' width='100%' style=\"border: 2px solid black; border-collapse: collapse;\">");
        String tagZeile = "";
        tagZeile += ("<tr >");
        tagZeile += ("<td  width='3%' style=\"font-size: 11;border: 1px solid black;\"><b>LK</b></td>");
        tagZeile += ("<td  width='5%' style=\"font-size: 11;border: 1px solid black;\"><b>LF</b></td>");
        tagZeile += ("<td  width='7%' style=\"font-size: 11;border: 1px solid black;\"><b>Stunde</b></td>");
        tagZeile += ("<td  style=\"font-size: 11;border: 1px solid black;\"><b>Inhalt</b></td>");
        tagZeile += ("<td  style=\"font-size: 11;border: 1px solid black;\"><b>Bemerkungen</b></td>");
        tagZeile += ("<td  style=\"font-size: 11;border: 1px solid black;\"><b>Lernsituation</b></td>");
        tagZeile += ("</tr>");
        htmlString.append(tagZeile);
        String tag = " ";
        int kw = -1;
        document.open();
        boolean firstPage = true;
        for (Verlauf v : verlauf) {
            String str = v.getDATUM().toString();
            if (str.compareTo(tag) == 0) {
                htmlString.append("<tr>");
                htmlString.append("<td width='3%' style=\"font-size: 11;border: 1px solid black;\">" + v.getID_LEHRER() + "</td>");
                htmlString.append("<td width='5%' style=\"font-size: 11;border: 1px solid black;\">" + v.getID_LERNFELD() + "</td>");
                htmlString.append("<td width='7%' style=\"font-size: 11;border: 1px solid black;\">" + v.getSTUNDE() + "</td>");
                htmlString.append("<td style=\"font-size: 11;border: 1px solid black;\">" + v.getINHALT() + "</td>");
                htmlString.append("<td style=\"font-size: 11;border: 1px solid black;\">" + v.getBEMERKUNG() + "</td>");
                htmlString.append("<td style=\"font-size: 11;border: 1px solid black;\">" + v.getAUFGABE() + "</td>");
                htmlString.append("</tr>");

            } // ein neuer Tag
            else {
                if (kw == -1) {
                    kw = v.getKw();
                }
                // Jede Woche eine neue Seite!
                if (!firstPage && kw != v.getKw()) {
                    kw = v.getKw();
                    htmlString.append("</table>");
                    System.out.println("html String=" + htmlString.toString());
                    //document.add(new Paragraph("Tutorial to Generate PDF using Servlet"));
                    InputStream is = new ByteArrayInputStream(htmlString.toString().getBytes());
                    // Bild einfügen
                    String url = "http://www.mmbbs.de/fileadmin/template/mmbbs/gfx/mmbbs_logo_druck.gif";
                    Image image = Image.getInstance(url);
                    image.setAbsolutePosition(45f, 720f);
                    image.scalePercent(50f);
                    document.add(image);
                    XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
                    document.newPage();
                    htmlString = new StringBuilder();
                    htmlString.append(kopf);
                    htmlString.append("<table  align='center' width='100%' style=\"border: 2px solid black; border-collapse: collapse;\">");
                    htmlString.append(tagZeile);
                    System.out.println("weiter mit neuer Seite");
                }
                htmlString.append("<tr>");
                htmlString.append("<td colspan=\"6\" align=\"center\" style=\"background-color: #cccccc; padding:4px;border: 1px solid black;\">KW " + v.getKw() + " / " + v.getWochentag() + " " + str.substring(0, str.indexOf(" ")) + "</td>");
                htmlString.append("</tr>");
                htmlString.append("<tr>");
                htmlString.append("<td width='3%' style=\"font-size: 11;border: 1px solid black;\">" + v.getID_LEHRER() + "</td>");
                htmlString.append("<td width='5%' style=\"font-size: 11;border: 1px solid black;\">" + v.getID_LERNFELD() + "</td>");
                htmlString.append("<td width='7%' style=\"font-size: 11;border: 1px solid black;\">" + v.getSTUNDE() + "</td>");
                htmlString.append("<td style=\"font-size: 11;border: 1px solid black;\">" + v.getINHALT() + "</td>");
                htmlString.append("<td style=\"font-size: 11;border: 1px solid black;\">" + v.getBEMERKUNG() + "</td>");
                htmlString.append("<td style=\"font-size: 11;border: 1px solid black;\">" + v.getAUFGABE() + "</td>");
                htmlString.append("</tr>");
                firstPage = false;
                tag = str;
            }
        }
        htmlString.append("</table>");
        System.out.println("html String Rest=" + htmlString.toString());
        return htmlString;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private String findVermerk(Integer id, Date current, List<AnwesenheitObjekt> anwesenheit) {
        for (AnwesenheitObjekt ao : anwesenheit) {
            if (ao.getId_Schueler()==id) {
                System.out.println("Schuler mit id= "+id+" gefunden!");
                for (AnwesenheitEintrag a : ao.getEintraege()) {
                    if (a.getDATUM().compareTo(current)==0) {
                        return a.getVERMERK();
                    }
                }
                return "";
            }
        }
        return "";
    }

    private List<AnwesenheitObjekt> getData(TypedQuery query) {
        List<AnwesenheitEintrag> anwesenheit = query.getResultList();
        //System.out.println("Results:="+anwesenheit);
        List<AnwesenheitObjekt> anw = new ArrayList();
        int id = 0;
        AnwesenheitObjekt ao = new AnwesenheitObjekt();
        for (int i = 0; i < anwesenheit.size(); i++) {
            if (anwesenheit.get(i).getID_SCHUELER() != id) {
                id = anwesenheit.get(i).getID_SCHUELER();
                ao = new AnwesenheitObjekt(id);
                anw.add(ao);
            }
            ao.getEintraege().add(anwesenheit.get(i));
        }
        return anw;
    }
}

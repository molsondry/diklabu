/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.util;

import de.tuttas.config.Config;
import de.tuttas.restful.auth.Roles;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Jörg
 */
public class LDAPUtil {

    private static LDAPUtil instance;
    private String bindUser;
    private String bindPassword;
    private String host;
    private String userContext;
    private InitialDirContext context;
    private SearchControls ctrls;

    public static LDAPUtil getInstance() {
        if (instance == null) {
            instance = new LDAPUtil();
        }
        return instance;
    }

    /*
     ldapconfig.json
    
        {
        "host": "ldap://192.168.178.147:389",
        "binduser": "CN=Administrator,CN=Users,DC=tuttas,DC=de",
        "bindpassword":"geheim",
        "context": "OU=ou-lehrer,OU=mmbbs,DC=tuttas,DC=de"
        }

     */
    private LDAPUtil() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(Config.LDAP_CONF_PATH));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String conf = sb.toString();
                JSONParser parser = new JSONParser();
                JSONObject jo = (JSONObject) parser.parse(conf);
                host = (String) jo.get("host");
                bindUser = (String) jo.get("binduser");
                bindPassword = (String) jo.get("bindpassword");
                userContext = (String) jo.get("context");
                Log.d("Context is " + userContext);

            } catch (IOException ex) {
                Logger.getLogger(LDAPUtil.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(LDAPUtil.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(LDAPUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LDAPUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

               

    }

    public static void main(String[] args) {
        LDAPUtil lpd = LDAPUtil.getInstance();
        LDAPUser u;
        try {
            u = lpd.authenticateJndi("fiae15h.timm", "Xicuxi14");
            Log.d("Habe gefunden " + u);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public LDAPUser authenticateJndi(String username, String password) throws Exception {
// Anbindung ans LDAP
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.PROVIDER_URL, host);
        props.put(Context.SECURITY_PRINCIPAL, bindUser);//adminuser - User with special priviledge, dn user
        props.put(Context.SECURITY_CREDENTIALS, bindPassword);//dn user password
        try {
            context = new InitialDirContext(props);
            ctrls = new SearchControls();
            ctrls.setReturningAttributes(new String[]{"description","mail","sn","initials","givenName", "sn", "memberOf", "userPrincipalName","distinguishedName"});
            ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        } catch (NamingException ex) {
            Logger.getLogger(LDAPUtil.class.getName()).log(Level.SEVERE, null, ex);
        }       
        NamingEnumeration<javax.naming.directory.SearchResult> answers = context.search(userContext, "(cn=" + username + ")", ctrls);
        Log.d("answers=" + answers);
        Log.d("answers=" + answers.hasMore());
        if (!answers.hasMore()) {
            return null;
        }
        javax.naming.directory.SearchResult result = answers.nextElement();

        

        try {
            for (NamingEnumeration ae = result.getAttributes().getAll(); ae.hasMore();) {
                Attribute attr = (Attribute) ae.next();
                Log.d("attribute: " + attr.getID());

                /* print each value */
                for (NamingEnumeration e = attr.getAll(); e.hasMore(); System.out
                        .println("value: " + e.next()))
            ;
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
        
        String inititials="";
        if (result.getAttributes().get("initials")!=null) {
            inititials=result.getAttributes().get("initials").getAll().next().toString();
        }
        LDAPUser u = new LDAPUser(result.getAttributes().get("sn").getAll().next().toString(),
                result.getAttributes().get("givenName").getAll().next().toString(),
                result.getAttributes().get("mail").getAll().next().toString(),
                inititials);

        String dName=result.getAttributes().get("distinguishedName").getAll().next().toString();
        Log.d("dName="+dName);
        if (dName.contains("OU=Lehrer")) {
            System.out.println("Ich bin ein Lehrer");
            u.setRole(Roles.toString(Roles.LEHRER));
        }
        else {
            System.out.println("Ich bin ein Schüler");
            u.setRole(Roles.toString(Roles.SCHUELER));
        }
        
        String user = result.getNameInNamespace();

        try {
            
            props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            props.put(Context.PROVIDER_URL, host);
            props.put(Context.SECURITY_PRINCIPAL, user);
            props.put(Context.SECURITY_CREDENTIALS, password);

            context = new InitialDirContext(props);
        } catch (Exception e) {
            return null;
        }
        return u;
    }
}

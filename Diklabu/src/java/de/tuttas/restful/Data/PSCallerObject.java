/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.restful.Data;




/**
 *
 * @author Jörg
 */
public class PSCallerObject extends ResultObject{
    Auth auth;
    String script;
    String result;

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }
            

    public PSCallerObject() {
    }

    public PSCallerObject(String script, Auth auth) {
        this.script = script;
        this.auth = auth;
    }

    
    
    public void setResult(String result) {
        this.result = result;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getResult() {
        return result;
    }

    public String getScript() {
        return script;
    }

    @Override
    public String toString() {
        return "script="+script+" result="+result;
    }
    
    
    
}

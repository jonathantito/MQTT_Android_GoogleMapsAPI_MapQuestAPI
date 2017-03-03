package com.example.dell.testapigoogle;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SuscriptionRequest extends StringRequest {

    private Map<String, String> params;


    //public LoginRequest(String username, String password, Response.Listener<String> listener) {//Modif.1.old.A.ln
    public SuscriptionRequest(Response.Listener<String> listener, String LOGIN_REQUEST_URL) {//Modif.1.new.A.ln
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        //params.put("ruta", ruta);

    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

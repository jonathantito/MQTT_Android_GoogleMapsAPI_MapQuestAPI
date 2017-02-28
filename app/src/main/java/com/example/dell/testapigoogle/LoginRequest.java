package com.example.dell.testapigoogle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    //private static final String LOGIN_REQUEST_URL = "http://192.168.1.7/Login3.php";//Modif.1.old.A.ln
    private Map<String, String> params;


    //public LoginRequest(String username, String password, Response.Listener<String> listener) {//Modif.1.old.A.ln
    public LoginRequest(String username, String password, Response.Listener<String> listener, String LOGIN_REQUEST_URL) {//Modif.1.new.A.ln
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("nombreUsuario", username);
        params.put("passUsuario", password);


    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

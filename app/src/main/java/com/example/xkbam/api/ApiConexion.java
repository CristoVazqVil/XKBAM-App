package com.example.xkbam.api;

import android.util.Log;

import com.example.xkbam.utilidades.SesionSingleton;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiConexion {

    private static OkHttpClient cliente;
    private static final String TAG = "APIConexion";
    private static final String API_BASE_URL = "http://192.168.17.135:3000/api/";

    public static OkHttpClient obtenerCliente() {
        if (cliente == null) {
            cliente = new OkHttpClient();
        }
        return cliente;
    }

    public static OkHttpClient obtenerClienteSinAutenticacion() {
        if (cliente == null) {
            cliente = new OkHttpClient();
        }
        return cliente;
    }

    public static Response enviarRequest(String metodo, String endpoint, String jsonBody, boolean autenticacion, Callback callback) {
        OkHttpClient client = autenticacion ? obtenerCliente() : obtenerClienteSinAutenticacion();

        String url = API_BASE_URL + endpoint;

        RequestBody body = null;
        if (jsonBody != null) {
            body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
        }

        Request.Builder builder = new Request.Builder()
                .url(url);

        if (autenticacion) {
            builder.addHeader("Authorization", "Bearer " + SesionSingleton.getJWT());
        }

        if (metodo.equals("GET")) {
            builder.get();
        } else if (metodo.equals("POST")) {
            builder.post(body);
        } else if (metodo.equals("PUT")) {
            builder.put(body);
        } else if (metodo.equals("DELETE")) {
            builder.delete();
        }

        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                Headers headers = response.headers();
                String newToken = headers.get("Set-Authorization");
                if (newToken != null && !newToken.isEmpty()) {
                    SesionSingleton.setJWT(newToken);
                }
            } else if (response.code() == 401) {
                Log.d(TAG, "Error: Debe volver a iniciar sesión");
            }

            return response;
        } catch (IOException e) {
            Log.e(TAG, "Error al enviar la solicitud: " + e.getMessage());
            return null;
        }
    }


    public static void enviarRequestAsincrono(String metodo, String endpoint, String jsonBody, boolean autenticacion, Callback callback) {
        OkHttpClient client = autenticacion ? obtenerCliente() : obtenerClienteSinAutenticacion();

        String url = API_BASE_URL + endpoint;

        RequestBody body = null;
        if (jsonBody != null) {
            body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
        }

        Request.Builder builder = new Request.Builder()
                .url(url);

        if (autenticacion) {
            builder.addHeader("Authorization", "Bearer " + SesionSingleton.getJWT());
        }

        if (metodo.equals("GET")) {
            builder.get();
        } else if (metodo.equals("POST")) {
            builder.post(body);
        } else if (metodo.equals("PUT")) {
            builder.put(body);
        } else if (metodo.equals("DELETE")) {
            builder.delete();
        }

        Request request = builder.build();

        client.newCall(request).enqueue(callback);
    }

}

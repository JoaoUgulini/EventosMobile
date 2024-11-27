package com.example.gereciamentoeventos;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TelaReservaEvento extends AppCompatActivity {

    private Spinner spinnerLocais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_reserva_evento);

        spinnerLocais = findViewById(R.id.spinnerLocais);

        new PreencheComboTask().execute("http://200.132.172.204/Eventos/getLocais.php");
    }

    private class PreencheComboTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            ArrayList<String> nomesLocais = new ArrayList<>();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.d("PreencheComboTask", "Resposta da API: " + response.toString());

                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject local = jsonArray.getJSONObject(i);
                        String nome = local.getString("nome");
                        int capacidade = local.getInt("capacidade");

                        nomesLocais.add(nome + " (Capacidade: " + capacidade + ")");
                    }
                } else {
                    Log.e("PreencheComboTask", "Erro na resposta: Código " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e("PreencheComboTask", "Erro na requisição: " + e.getMessage(), e);
            }
            return nomesLocais;
        }

        @Override
        protected void onPostExecute(ArrayList<String> nomesLocais) {
            if (!nomesLocais.isEmpty()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        TelaReservaEvento.this,
                        android.R.layout.simple_spinner_item,
                        nomesLocais
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLocais.setAdapter(adapter);
            } else {
                Toast.makeText(TelaReservaEvento.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

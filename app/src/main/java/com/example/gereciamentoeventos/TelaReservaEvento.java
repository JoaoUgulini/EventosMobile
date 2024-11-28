package com.example.gereciamentoeventos;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TelaReservaEvento extends AppCompatActivity {

    Spinner spinnerLocais;
    Button btRetornoReserva, btReserva;
    EditText edtHora, edtData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_reserva_evento);

        btRetornoReserva = findViewById(R.id.btRetornoReserva);
        btReserva = findViewById(R.id.btReserva);
        edtHora = findViewById(R.id.edtHora);
        edtData = findViewById(R.id.edtData);
        spinnerLocais = findViewById(R.id.spinnerLocais);

        new PreencheComboTask().execute("http://200.132.172.204/Eventos/getLocais.php");

        btRetornoReserva.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), TelaPrincipal.class);
            startActivity(i);
        });

        addDateWatcher(edtData);
        addTimeWatcher(edtHora);
    }

    private void addDateWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private final String ddmmyyyy = "DDMMYYYY";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    String input = s.toString().replaceAll("[^\\d]", "");
                    StringBuilder formatted = new StringBuilder();

                    if (input.length() <= ddmmyyyy.length()) {
                        int index = 0;
                        for (char c : ddmmyyyy.toCharArray()) {
                            if (index < input.length()) {
                                formatted.append(input.charAt(index));
                                if ((formatted.length() == 2 || formatted.length() == 5) && index + 1 < input.length()) {
                                    formatted.append("/");
                                }
                                index++;
                            }
                        }
                        current = formatted.toString();
                        editText.setText(current);
                        editText.setSelection(current.length());
                    }
                }
            }
        });
    }

    private void addTimeWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    String input = s.toString().replaceAll("[^\\d]", "");
                    StringBuilder formatted = new StringBuilder();

                    if (input.length() <= 4) {
                        int index = 0;
                        for (char c : "HHMM".toCharArray()) {
                            if (index < input.length()) {
                                formatted.append(input.charAt(index));
                                if (formatted.length() == 2 && index + 1 < input.length()) {
                                    formatted.append(":");
                                }
                                index++;
                            }
                        }
                        current = formatted.toString();
                        editText.setText(current);
                        editText.setSelection(current.length());
                    }
                }
            }
        });
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

                    Log.d("PreencheComboTask", "Resposta da API: " + response);

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
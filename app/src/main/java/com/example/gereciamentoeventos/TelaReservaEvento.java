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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

public class TelaReservaEvento extends AppCompatActivity {

    Spinner spinnerLocais;
    Button btRetornoReserva, btReserva;
    EditText edtHora, edtData;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_reserva_evento);

        btRetornoReserva = findViewById(R.id.btRetornoReserva);
        btReserva = findViewById(R.id.btReserva);
        edtHora = findViewById(R.id.edtHora);
        edtData = findViewById(R.id.edtData);
        spinnerLocais = findViewById(R.id.spinnerLocais);
        userId = getIntent().getIntExtra("userId", -1);
        new PreencheComboTask().execute("http://200.132.172.204/Eventos/consulta_locais.php");
        btRetornoReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TelaPrincipal.class);
                startActivity(i);
            }
        });

        addDateWatcher(edtData);
        addTimeWatcher(edtHora);

        btReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EnviajsonEvento().execute();
            }
        });
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

    public String getFormattedDate(String dateInput) {
        String input = dateInput.replaceAll("[^\\d]", "");
        if (input.length() == 8) {
            String formattedDate = input.substring(4, 8) + "-" + input.substring(2, 4) + "-" + input.substring(0, 2);
            return formattedDate;
        }
        return "";
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

    public String getFormattedTime(String timeInput) {
        String input = timeInput.replaceAll("[^\\d]", "");
        if (input.length() == 4) {
            String formattedTime = input.substring(0, 2) + ":" + input.substring(2, 4) + ":00";
            return formattedTime;
        }
        return "";
    }


    class EnviajsonEvento extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... arg0) {
            try {
                String url = "http://200.132.172.204/eventos/cadastra_evento.php";
                JSONObject jsonValores = new JSONObject();
                jsonValores.put("id_usuario", userId);
                jsonValores.put("id_local", 1);
                jsonValores.put("data", getFormattedDate(edtData.getText().toString())  );
                jsonValores.put("hora", getFormattedTime(edtHora.getText().toString()) );
                conexaouniversal mandar = new conexaouniversal();
                return mandar.postJSONObject(url, jsonValores);
            } catch (Exception e) {
                e.printStackTrace();
                return "erro";
            }
        }

        @Override
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);
        }

        public String getPostDataString(JSONObject params) throws Exception {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while (itr.hasNext()) {
                String key = itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));
            }
            return result.toString();
        }
    }

    private class PreencheComboTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            ArrayList<String> nomesLocais = new ArrayList<>();
            try {
                manipulahttp httpHandler = new manipulahttp();
                String resposta = httpHandler.requisitaservico(urls[0]);
                if (resposta != null) {
                    JSONArray jsonArray = new JSONArray(resposta);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject local = jsonArray.getJSONObject(i);
                        String nome = local.getString("nome");
                        int capacidade = local.getInt("capacidade");
                        nomesLocais.add(nome + " (Capacidade: " + capacidade + ")");
                    }
                } else {
                }
            } catch (Exception e) {
            }
            return nomesLocais;
        }

        @Override
        protected void onPostExecute(ArrayList<String> nomesLocais) {
            if (!nomesLocais.isEmpty()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        TelaReservaEvento.this,
                        android.R.layout.simple_spinner_item, nomesLocais
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLocais.setAdapter(adapter);
            } else {
                Toast.makeText(TelaReservaEvento.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

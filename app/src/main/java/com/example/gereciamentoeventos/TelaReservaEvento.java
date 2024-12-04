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
    int userId, LocalId;

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
                // Busca o ID do local selecionado e só então envia os dados.
                new ConsultajsonIdLocal().execute();
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

    class ConsultajsonIdLocal extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                String url = "http://200.132.172.204/Eventos/consulta_IDlocal.php";
                JSONObject jsonValores = new JSONObject();
                String nomeLocalSelecionado = spinnerLocais.getSelectedItem().toString();
                nomeLocalSelecionado = nomeLocalSelecionado.split(" \\(")[0].trim();
                jsonValores.put("nome_local", nomeLocalSelecionado);

                conexaouniversal mandar = new conexaouniversal();
                String resposta = mandar.postJSONObject(url, jsonValores);

                if (resposta != null) {
                    JSONObject jsonResponse = new JSONObject(resposta);

                    if (jsonResponse.has("id")) {
                        return jsonResponse.getInt("id");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer id) {
            if (id != -1) {
                LocalId = id; // Definir LocalId
                new EnviajsonEvento().execute(); // Enviar evento após receber o ID
            } else {
                Toast.makeText(TelaReservaEvento.this, "ID do local não encontrado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class EnviajsonEvento extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... arg0) {
            try {
                String url = "http://200.132.172.204/eventos/cadastra_evento.php";
                JSONObject jsonValores = new JSONObject();
                jsonValores.put("id_usuario", userId);
                jsonValores.put("id_local", LocalId);  // Usa o ID correto
                jsonValores.put("data", getFormattedDate(edtData.getText().toString()));
                jsonValores.put("hora", getFormattedTime(edtHora.getText().toString()));

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
            Toast.makeText(TelaReservaEvento.this, "Reserva efetuada com sucesso!", Toast.LENGTH_SHORT).show();
        }
    }

    public String getFormattedDate(String dateInput) {
        String input = dateInput.replaceAll("[^\\d]", "");
        if (input.length() == 8) {
            return input.substring(4, 8) + "-" + input.substring(2, 4) + "-" + input.substring(0, 2);
        }
        return "";
    }

    public String getFormattedTime(String timeInput) {
        String input = timeInput.replaceAll("[^\\d]", "");
        if (input.length() == 4) {
            return input.substring(0, 2) + ":" + input.substring(2, 4) + ":00";
        }
        return "";
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


package com.example.gereciamentoeventos;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TelaReservaEvento extends AppCompatActivity {

    private Spinner spinnerLocais;
    private Button btRetornoReserva, btReserva;
    private EditText edtHora, edtData;
    private int userId; // ID do usuário recebido na intent
    private ArrayList<Integer> idLocais; // Lista para armazenar os IDs dos locais

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_reserva_evento);

        // Inicializa componentes
        btRetornoReserva = findViewById(R.id.btRetornoReserva);
        btReserva = findViewById(R.id.btReserva);
        edtHora = findViewById(R.id.edtHora);
        edtData = findViewById(R.id.edtData);
        spinnerLocais = findViewById(R.id.spinnerLocais);

        // Recebe o ID do usuário da intent
        userId = getIntent().getIntExtra("userId", -1);

        // Inicializa lista para IDs dos locais
        idLocais = new ArrayList<>();

        // Preenche o Spinner com os locais via API
        new PreencheComboTask().execute("http://192.168.3.221/Eventos/getLocais.php");

        // Configura botão para retornar à tela principal
        btRetornoReserva.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), TelaPrincipal.class);
            startActivity(intent);
        });

        // Adiciona máscaras de formatação para os campos de data e hora
        addDateWatcher(edtData);
        addTimeWatcher(edtHora);

        // Configura botão para registrar o evento
        btReserva.setOnClickListener(view -> {
            String data = edtData.getText().toString().trim();
            String hora = edtHora.getText().toString().trim();
            int selectedIndex = spinnerLocais.getSelectedItemPosition(); // Índice do local selecionado no Spinner

            if (!data.isEmpty() && !hora.isEmpty() && selectedIndex >= 0) {
                try {
                    // Valida a data no formato DD/MM/YYYY
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    sdf.parse(data);

                    // Pega o ID do local selecionado
                    int idLocalSelecionado = idLocais.get(selectedIndex);

                    // Envia os dados para o servidor
                    new EnviarEventoTask().execute(userId, idLocalSelecionado, data, hora);
                } catch (ParseException e) {
                    Toast.makeText(TelaReservaEvento.this, "Data inválida. Use o formato DD/MM/AAAA.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TelaReservaEvento.this, "Preencha todos os campos e selecione um local.", Toast.LENGTH_SHORT).show();
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

    // AsyncTask para preencher o Spinner com dados da API
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

                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject local = jsonArray.getJSONObject(i);
                        String nome = local.getString("nome");
                        int id = local.getInt("id");

                        // Armazena o nome e o ID do local
                        nomesLocais.add(nome);
                        idLocais.add(id); // Adiciona o ID do local à lista
                    }
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
                Toast.makeText(TelaReservaEvento.this, "Erro ao carregar dados.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // AsyncTask para enviar os dados do evento ao servidor
    private class EnviarEventoTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            int idUsuario = (int) params[0];
            int idLocal = (int) params[1];
            String data = (String) params[2];
            String hora = (String) params[3];

            try {
                URL url = new URL("http://192.168.3.221/Eventos/cadastra_evento.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("id_usuario", idUsuario);
                jsonBody.put("id_local", idLocal);
                jsonBody.put("data", data);
                jsonBody.put("hora", hora);

                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(jsonBody.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    return response.toString();
                }
            } catch (Exception e) {
                Log.e("EnviarEventoTask", "Erro ao enviar evento: " + e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(TelaReservaEvento.this, "Evento registrado com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TelaReservaEvento.this, "Erro ao registrar evento.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

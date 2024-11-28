package com.example.gereciamentoeventos;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TelaCadLocal extends AppCompatActivity {

    EditText edtNomeLocal, edtEndereco, edtCapacidade;
    Button btLocalCad, btRetornoLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cad_local);

        edtNomeLocal = findViewById(R.id.edtNomeLocal);
        edtEndereco = findViewById(R.id.edtEndereco);
        edtCapacidade = findViewById(R.id.edtCapacidade);
        btLocalCad = findViewById(R.id.btLocalCad);
        btRetornoLocal = findViewById(R.id.btRetornoLocal);

        btLocalCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeLocal = edtNomeLocal.getText().toString().trim();
                String endereco = edtEndereco.getText().toString().trim();
                String capacidadeStr = edtCapacidade.getText().toString().trim();

                if (nomeLocal.isEmpty() || endereco.isEmpty() || capacidadeStr.isEmpty()) {
                    Toast.makeText(TelaCadLocal.this, "Todos os campos são obrigatórios!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int capacidade = Integer.parseInt(capacidadeStr);
                        Local local = new Local(nomeLocal, endereco, capacidade);
                        new CadastroLocalTask().execute(local);
                    } catch (NumberFormatException e) {
                        Toast.makeText(TelaCadLocal.this, "Capacidade deve ser um número válido!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btRetornoLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaCadLocal.this, TelaPrincipal.class);
                startActivity(intent);
            }
        });
    }

    private class CadastroLocalTask extends AsyncTask<Local, Void, String> {
        @Override
        protected String doInBackground(Local... params) {
            Local local = params[0];
            try {
                URL url = new URL("http://200.132.172.204/Eventos/cadastra_local.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("nome", local.getNome_local());
                json.put("endereco", local.getEndereco());
                json.put("capacidade", local.getCapacidade());

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "Local cadastrado com sucesso!";
                } else {
                    return "Erro ao cadastrar local: Código " + responseCode;
                }
            } catch (Exception e) {
                return "Erro na conexão: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(TelaCadLocal.this, result, Toast.LENGTH_LONG).show();

            if (result.contains("sucesso")) {
                edtNomeLocal.setText("");
                edtEndereco.setText("");
                edtCapacidade.setText("");
            }
        }
    }
}

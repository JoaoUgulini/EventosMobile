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
                String nomeLocal = edtNomeLocal.getText().toString();
                String endereco = edtEndereco.getText().toString();
                String capacidadeStr = edtCapacidade.getText().toString();

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
                String url = "http://200.132.172.204/Eventos/cadastra_local.php";
                JSONObject jsonValores = new JSONObject();
                jsonValores.put("nome_local", local.getNome_local());
                jsonValores.put("endereco", local.getEndereco());
                jsonValores.put("capacidade", local.getCapacidade());

                return conexaouniversal.postJSONObject(url, jsonValores);

            } catch (Exception e) {
                e.printStackTrace();
                return "erro";
            }
        }

        @Override
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);
        }
    }
}

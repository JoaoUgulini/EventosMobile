package com.example.gereciamentoeventos;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class TelaPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        Button btReservarEvento = (Button) findViewById(R.id.btReservarEvento);
        Button btCadastrarLocal = (Button) findViewById(R.id.btCadastrarLocal);

        btReservarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TelaReservaEvento.class);
                startActivity(i);
            }
        });

        btCadastrarLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TelaCadLocal.class);
                startActivity(i);
            }
        });
    }
}
package com.brunobatista.trabalhoandroid_2_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PrincipalActivity extends AppCompatActivity {

    private Button btnUsuario, btnVeiculo, btnCliente, btnMotorista, btnCorrida, btnMapa, btnConfiguracao, btnSair;
    private TextView lblUsuario;

    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        firebaseAuth = FirebaseAuthSignleton.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        btnUsuario = findViewById(R.id.btnUsuario);
        btnVeiculo = findViewById(R.id.btnVeiculo);
        btnCliente = findViewById(R.id.btnCliente);
        btnMotorista = findViewById(R.id.btnMotorista);
        btnCorrida = findViewById(R.id.btnCorrida);
        btnMapa = findViewById(R.id.btnMapa);
        btnConfiguracao = findViewById(R.id.btnConfiguracao);
        btnSair = findViewById(R.id.btnSair);

        lblUsuario = findViewById(R.id.lblUsuario);

        lblUsuario.setText("Olá, " + user.getEmail());

        btnUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CadastroUsuario();
            }
        });
        btnVeiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CadastroVeiculo();
            }
        });
        btnCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PrincipalActivity.this, "Cliente em manutenção....", Toast.LENGTH_SHORT).show();
            }
        });
        btnMotorista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PrincipalActivity.this, "Motorista em manutenção....", Toast.LENGTH_SHORT).show();
            }
        });
        btnCorrida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PrincipalActivity.this, "Corrida em manutenção....", Toast.LENGTH_SHORT).show();
            }
        });
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PrincipalActivity.this, "Mapa em manutenção....", Toast.LENGTH_SHORT).show();
            }
        });
        btnConfiguracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PrincipalActivity.this, "Configuração em manutenção....", Toast.LENGTH_SHORT).show();
            }
        });
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sair();
            }
        });
    }

    private void Sair() {
        firebaseAuth.signOut();
        startActivity(new Intent(PrincipalActivity.this, LoginActivity.class));
        finish();
    }

    private void CadastroUsuario() {
        startActivity(new Intent(PrincipalActivity.this, ListaUsuarioActivity.class));
    }

    private void CadastroVeiculo() {
        startActivity(new Intent(PrincipalActivity.this, ListaVeiculoActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mniSair:
                Sair();
                return true;
            case R.id.mniUsuario:
                CadastroUsuario();
                return true;

            case R.id.mniVeiculo:
                CadastroVeiculo();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.principal, menu);

        return true;
    }
}
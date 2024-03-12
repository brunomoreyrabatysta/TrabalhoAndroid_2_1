package com.brunobatista.trabalhoandroid_2_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity {

    private Button btnLogin, btnCadastreSe, btnEsqueceuSenha;
    private EditText edtEmail, edtSenha;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuthSignleton.getInstance();

        btnLogin = findViewById(R.id.btnLogin);
        btnCadastreSe = findViewById(R.id.btnCadastreSe);
        btnEsqueceuSenha = findViewById(R.id.btnEsqueceuSenha);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        btnCadastreSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(LoginActivity.this,"Cadastre-se",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, CadastroUsuarioActivity.class);
                intent.putExtra("KEY_USUARIOID", "");
                intent.putExtra("KEY_CADASTRESE", "true");
                startActivity(intent);
            }
        });
        btnEsqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "Esqueceu a senha em manutenção....", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signIn() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if ((!email.isEmpty()) && (!senha.isEmpty())) {
            firebaseAuth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login ON", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginActivity.this, PrincipalActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Login OFF", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
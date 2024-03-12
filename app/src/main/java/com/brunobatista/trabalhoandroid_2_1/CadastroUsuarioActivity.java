package com.brunobatista.trabalhoandroid_2_1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.brunobatista.trabalhoandroid_2_1.model.Usuario;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtSenha, edtConfirmacaoSenha;
    private TextView lblTitulo;
    private Button btnSalvar, btnExcluir;
    private boolean novoRegistro = false;
    private boolean cadastreSe = false;
    private List<Usuario> usuarios;

    private final String C_TABLE_NAME = "usuario";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        firebaseAuth = FirebaseAuthSignleton.getInstance();

        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        edtConfirmacaoSenha = findViewById(R.id.edtConfirmacaoSenha);

        lblTitulo = findViewById(R.id.lblTitulo);

        btnSalvar = findViewById(R.id.btnSalvar);
        btnExcluir = findViewById(R.id.btnExcluir);

        Intent intent = getIntent();
        if (intent != null )  {
            if (intent.hasExtra("KEY_USUARIOID")) {
                usuarioId = intent.getStringExtra("KEY_USUARIOID").trim();
            }
            if (intent.hasExtra("KEY_CADASTRESE")) {
                cadastreSe = intent.getStringExtra("KEY_CADASTRESE").trim().toLowerCase().equals("true");
            }
        } else {
            usuarioId = "";
        }

        databaseReference = FirebaseDatabase.getInstance().getReference(C_TABLE_NAME);

        usuarios = new ArrayList<Usuario>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarios.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Usuario usuario = snapshot1.getValue(Usuario.class);
                    usuario.setUsuarioId(snapshot1.getKey());
                    usuarios.add(usuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        CadastroUsuarioActivity.this,
                        "Problema ao ler no banco de dados.",
                        Toast.LENGTH_LONG).show();
            }
        });

        System.out.println("TESTE 0 TSTE");
        novoRegistro = usuarioId.isEmpty();

        if (novoRegistro) {
            lblTitulo.setText("NOVO CADASTRO");

            edtNome.setText("");
            edtEmail.setText("");
            edtSenha.setText("");
            edtConfirmacaoSenha.setText("");

            btnExcluir.setVisibility(View.INVISIBLE);

            edtEmail.setEnabled(true);
            edtEmail.setBackgroundColor(Color.WHITE);
            edtEmail.setTextColor(Color.BLACK);
        }  else {
            lblTitulo.setText("ALTERAR CADASTRO");

            String nome = intent.getStringExtra("KEY_NOME");
            String email = intent.getStringExtra("KEY_EMAIL");

            edtNome.setText(nome);
            edtEmail.setText(email);

            edtEmail.setEnabled(false);
            edtEmail.setBackgroundColor(Color.GRAY);
            edtEmail.setTextColor(Color.WHITE);
        }

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Salvar();
            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Excluir();
            }
        });
    }

    private  void Excluir() {
        String email = edtEmail.getText().toString().trim();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (email.equals(user.getEmail())) {
            Toast.makeText( CadastroUsuarioActivity.this, "Não é possível excluir o usuário logado!", Toast.LENGTH_LONG).show();
        } else {
            try {
                databaseReference.child(usuarioId).removeValue();
            } catch (Exception ex) {
                Toast.makeText(CadastroUsuarioActivity.this, "Problema ao excluir o cadastro de usuário!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void Salvar() {
        if (Validar()) {
            String sNome = edtNome.getText().toString().trim();
            String sEmail = edtEmail.getText().toString().trim();
            String sSenha = edtSenha.getText().toString().trim();

            Usuario usuario = new Usuario();

            if (!novoRegistro)
                usuario.setUsuarioId(usuarioId);

            usuario.setNome(sNome);
            usuario.setEmail(sEmail);
            usuario.setSenha(sSenha);

            if (novoRegistro) {
                CadastrarUsuarioFirebase(usuario);
            } else {
                Alterar(usuario);
            }
        }
    }

    private void CadastrarUsuarioFirebase(Usuario usuario){
        String sEmail = usuario.getEmail();
        String sSenha = usuario.getSenha();

        firebaseAuth.createUserWithEmailAndPassword(sEmail, sSenha)
                .addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            //FirebaseUser user = firebaseAuth.getCurrentUser();
                            Inserir(usuario);
                            MensagemFinal("Cadastro inserido com sucesso!");
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CadastroUsuarioActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void Inserir(Usuario usuario) {
        try {
            usuarioId = databaseReference.push().getKey();
            databaseReference.child(usuarioId).setValue(usuario);
            MensagemFinal("Cadastro inserido com sucesso!");
        } catch (Exception ex)
        {
            Toast.makeText( CadastroUsuarioActivity.this, "Problema ao inserir o cadastro de usuário!", Toast.LENGTH_LONG).show();
        }
    }

    private void Alterar(Usuario usuario) {
        try {
            usuario.setUsuarioId(null);

            databaseReference = FirebaseDatabase.getInstance().getReference(C_TABLE_NAME).child(usuarioId);
            databaseReference.setValue(usuario);
            MensagemFinal("Cadastro alterado com sucesso!");
        } catch (Exception ex)
        {
            Toast.makeText( CadastroUsuarioActivity.this, "Problema ao alterar o cadastro de usuário!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean Validar() {
        String sNome = edtNome.getText().toString().trim();
        String sEmail = edtEmail.getText().toString().trim();
        String sSenha = edtSenha.getText().toString().trim();
        String sConfirmacaoSenha = edtConfirmacaoSenha.getText().toString().trim();

        if (sNome == null || sNome.length() == 0) {
            Toast.makeText(CadastroUsuarioActivity.this, "O nome deve ser preenchido!", Toast.LENGTH_LONG).show();
            return false;
        } else if (sNome.length() <  5) {
            Toast.makeText(CadastroUsuarioActivity.this, "O nome deve conter no mínimo 5 (cinco) caracteres!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (sEmail == null || sEmail.length() == 0) {
            Toast.makeText(CadastroUsuarioActivity.this, "O e-mail deve ser preenchido!", Toast.LENGTH_LONG).show();
            return false;
        } else if (sEmail.length() <  5) {
            Toast.makeText(CadastroUsuarioActivity.this, "O e-mail deve conter no mínimo 5 (cinco) caracteres!", Toast.LENGTH_LONG).show();
            return false;
        } else if (!validarEmail(sEmail)) {
            Toast.makeText(CadastroUsuarioActivity.this, "O e-mail está invalido!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (sSenha == null || sSenha.length() == 0) {
            Toast.makeText(CadastroUsuarioActivity.this, "A senha deve ser preenchida!", Toast.LENGTH_LONG).show();
            return false;
        } else if (sSenha.length() <  5) {
            Toast.makeText(CadastroUsuarioActivity.this, "A senha deve conter no mínimo 5 (cinco) caracteres!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (sConfirmacaoSenha == null || sConfirmacaoSenha.length() == 0) {
            Toast.makeText(CadastroUsuarioActivity.this, "A confirmação da senha deve ser preenchida!", Toast.LENGTH_LONG).show();
            return false;
        } else if (sConfirmacaoSenha.length() <  5) {
            Toast.makeText(CadastroUsuarioActivity.this, "A confirmação da senha deve conter no mínimo 5 (cinco) caracteres!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!sSenha.equals(sConfirmacaoSenha)) {
            Toast.makeText(CadastroUsuarioActivity.this, "A senha e a confirmação da senha deve ser idênticas!", Toast.LENGTH_LONG).show();
            return false;
        }

        boolean encontrou = false;
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equals(sEmail)) {
                if (novoRegistro) {
                    encontrou = true;
                    break;
                } else {
                    if (!usuario.getUsuarioId().equals(usuarioId)) {
                        encontrou = true;
                        break;
                    }
                }
            }
        }

        if (encontrou) {
            Toast.makeText(CadastroUsuarioActivity.this, "O e-mail já existe cadastado!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
    public static boolean validarEmail(String email){
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mniVoltar:
                Voltar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cadastrousuario, menu);

        return true;
    }

    private void Voltar() {
        Intent intent;
        if (!cadastreSe) {
            intent = new Intent(CadastroUsuarioActivity.this, ListaUsuarioActivity.class);
        } else {
            intent = new Intent(CadastroUsuarioActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void MensagemFinal(String mensagem) {
        Toast.makeText(CadastroUsuarioActivity.this, mensagem, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (!cadastreSe) {
                    intent = new Intent(CadastroUsuarioActivity.this, ListaUsuarioActivity.class);
                } else {
                    intent = new Intent(CadastroUsuarioActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
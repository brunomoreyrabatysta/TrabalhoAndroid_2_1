package com.brunobatista.trabalhoandroid_2_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.brunobatista.trabalhoandroid_2_1.model.Usuario;
import com.brunobatista.trabalhoandroid_2_1.model.Veiculo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaUsuarioActivity extends AppCompatActivity implements UsuarioAdapter.OnDeleteClickListener,UsuarioAdapter.OnUpdateClickListener {

    private DatabaseReference databaseReference;
    private final String C_TABLE_NAME = "usuario";

    private List<Usuario> usuarios;
    private UsuarioAdapter usuarioAdapter;

    private RecyclerView lstUsuarios;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuario);

        firebaseAuth = FirebaseAuthSignleton.getInstance();

        lstUsuarios = findViewById(R.id.lstUsuarios);

        usuarios = new ArrayList<Usuario>();
        usuarioAdapter = new UsuarioAdapter(this, usuarios, this, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lstUsuarios.setLayoutManager(layoutManager);
        lstUsuarios.setAdapter(usuarioAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference(C_TABLE_NAME);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarios.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Usuario usuario = snapshot1.getValue(Usuario.class);
                    usuario.setUsuarioId(snapshot1.getKey());
                    usuarios.add(usuario);
                }
                usuarioAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        ListaUsuarioActivity.this,
                        "Problema ao ler no banco de dados.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDeleteClick(int posicao) {
        ExcluirUsuario(posicao);
    }

    private void ExcluirUsuario(int posicao) {
        String email = usuarios.get(posicao).getEmail();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (email.equals(user.getEmail())) {
            Toast.makeText( ListaUsuarioActivity.this, "Não é possível excluir o usuário logado!", Toast.LENGTH_LONG).show();
        } else {
            try {
                databaseReference.child(usuarios.get(posicao).getUsuarioId()).removeValue();
            } catch (Exception ex) {
                Toast.makeText(ListaUsuarioActivity.this, "Problema ao excluir o cadastro de usuário!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onUpdateClick(int posicao) {
        MostrarUsuario(posicao);
    }

    private void MostrarUsuario(int posicao) {
        String usuarioId = usuarios.get(posicao).getUsuarioId();
        String nome= usuarios.get(posicao).getNome();
        String email = usuarios.get(posicao).getEmail();
        Intent intent = new Intent(this, CadastroUsuarioActivity.class);
        intent.putExtra("KEY_USUARIOID", usuarioId);
        intent.putExtra("KEY_NOME", nome);
        intent.putExtra("KEY_EMAIL", email);

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mniVoltar:
                Voltar();
                return true;
            case R.id.mniCadastroUsuario:
                CadastroUsuario();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listagemusuario, menu);

        return true;
    }

    private void CadastroUsuario() {
        Intent intent = new Intent(ListaUsuarioActivity.this, CadastroUsuarioActivity.class);
        intent.putExtra("KEY_USUARIOID", "");
        startActivity(intent);
    }

    private void Voltar() {
        Intent intent = new Intent(ListaUsuarioActivity.this, PrincipalActivity.class);
        startActivity(intent);
        finish();
    }
}
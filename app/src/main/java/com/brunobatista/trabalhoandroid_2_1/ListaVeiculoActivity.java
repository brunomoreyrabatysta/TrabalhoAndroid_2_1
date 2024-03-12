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

import com.brunobatista.trabalhoandroid_2_1.model.Veiculo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaVeiculoActivity extends AppCompatActivity implements VeiculoAdapter.OnDeleteClickListener,VeiculoAdapter.OnUpdateClickListener {

    private DatabaseReference databaseReference;
    private final String C_TABLE_NAME = "veiculo";

    private List<Veiculo> veiculos;
    private VeiculoAdapter veiculoAdapter;

    private RecyclerView lstVeiculos;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_veiculo);

        firebaseAuth = FirebaseAuthSignleton.getInstance();

        lstVeiculos = findViewById(R.id.lstVeiculo);

        veiculos = new ArrayList<Veiculo>();
        veiculoAdapter = new VeiculoAdapter(this, veiculos, this, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lstVeiculos.setLayoutManager(layoutManager);
        lstVeiculos.setAdapter(veiculoAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference(C_TABLE_NAME);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                veiculos.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Veiculo veiculo = snapshot1.getValue(Veiculo.class);
                    veiculo.setVeiculoId(snapshot1.getKey());
                    veiculos.add(veiculo);
                }
                veiculoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        ListaVeiculoActivity.this,
                        "Problema ao ler no banco de dados.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDeleteClick(int posicao) {
        ExcluirVeiculo(posicao);
    }

    private void ExcluirVeiculo(int posicao) {
        try {
            databaseReference.child(veiculos.get(posicao).getVeiculoId()).removeValue();
            Toast.makeText(ListaVeiculoActivity.this, "Veículo excluído com sucesso!", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(ListaVeiculoActivity.this, "Problema ao excluir o cadastro de veículo!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpdateClick(int posicao) {
        AlterarVeiculo(posicao);
    }

    private void AlterarVeiculo(int posicao) {
        String veiculoId = veiculos.get(posicao).getVeiculoId();
        String marca = veiculos.get(posicao).getMarca();
        String modelo = veiculos.get(posicao).getModelo();
        String cor = veiculos.get(posicao).getCor();
        String anoFabricacao = veiculos.get(posicao).getAnoFabricacao();
        String anoModelo = veiculos.get(posicao).getAnoModelo();
        String placa = veiculos.get(posicao).getPlaca();
        String renavam = veiculos.get(posicao).getRenavam();
        String chassi = veiculos.get(posicao).getChassi();

        Intent intent = new Intent(this, CadastroVeiculoActivity.class);
        intent.putExtra("KEY_VEICULOID", veiculoId);
        intent.putExtra("KEY_MARCA", marca);
        intent.putExtra("KEY_MODELO", modelo);
        intent.putExtra("KEY_COR", cor);
        intent.putExtra("KEY_ANO_FABRICACAO", anoFabricacao);
        intent.putExtra("KEY_ANO_MODELO",anoModelo);
        intent.putExtra("KEY_PLACA", placa);
        intent.putExtra("KEY_RENAVAM", renavam);
        intent.putExtra("KEY_CHASSI", chassi);

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mniVoltar:
                Voltar();
                return true;
            case R.id.mniCadastroVeiculo:
                CadastroVeiculo();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listagemveiculo, menu);
        return true;
    }

    private void CadastroVeiculo() {
        Intent intent = new Intent(ListaVeiculoActivity.this, CadastroVeiculoActivity.class);
        intent.putExtra("KEY_VEICULOID", "");
        startActivity(intent);
    }

    private void Voltar() {
        Intent intent = new Intent(ListaVeiculoActivity.this, PrincipalActivity.class);
        startActivity(intent);
        finish();
    }
}
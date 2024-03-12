package com.brunobatista.trabalhoandroid_2_1;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brunobatista.trabalhoandroid_2_1.model.Usuario;
import com.brunobatista.trabalhoandroid_2_1.model.Veiculo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CadastroVeiculoActivity extends AppCompatActivity {

    private EditText edtMarca, edtModelo, edtCor, edtAnoFabricacao, edtAnoModelo, edtPlaca, edtRenavam, edtChassi;

    private TextView lblTitulo;
    private Button btnSalvar, btnExcluir;

    private boolean novoRegistro = false;

    private final String C_TABLE_NAME = "veiculo";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String veiculoId;
    private List<Veiculo> veiculos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_veiculo);

        firebaseAuth = FirebaseAuthSignleton.getInstance();

        edtMarca = findViewById(R.id.edtMarca);
        edtModelo  = findViewById(R.id.edtModelo);
        edtCor = findViewById(R.id.edtCor);
        edtAnoFabricacao = findViewById(R.id.edtAnoFabricacao);
        edtAnoModelo = findViewById(R.id.edtAnoModelo);
        edtPlaca = findViewById(R.id.edtPlaca);
        edtRenavam  = findViewById(R.id.edtRenavam);
        edtChassi = findViewById(R.id.edtChassi);

        lblTitulo = findViewById(R.id.lblTitulo);

        btnSalvar = findViewById(R.id.btnSalvar);
        btnExcluir = findViewById(R.id.btnExcluir);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("KEY_VEICULOID")) {
            veiculoId = intent.getStringExtra("KEY_VEICULOID").trim();
        } else {
            veiculoId = "";
        }

        databaseReference = FirebaseDatabase.getInstance().getReference(C_TABLE_NAME);

        veiculos = new ArrayList<Veiculo>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                veiculos.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Veiculo veiculo = snapshot1.getValue(Veiculo.class);
                    veiculo.setVeiculoId(snapshot1.getKey());
                    veiculos.add(veiculo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        CadastroVeiculoActivity.this,
                        "Problema ao ler no banco de dados.",
                        Toast.LENGTH_LONG).show();
            }
        });

        novoRegistro = veiculoId.isEmpty();

        if (novoRegistro) {
            lblTitulo.setText("NOVO CADASTRO");

            edtMarca.setText("");
            edtModelo.setText("");
            edtCor.setText("");
            edtAnoFabricacao.setText("");
            edtAnoModelo.setText("");
            edtPlaca.setText("");
            edtRenavam.setText("");
            edtChassi.setText("");

            btnExcluir.setVisibility(View.INVISIBLE);
        } else  {
            lblTitulo.setText("ALTERAR CADASTRO");

            String marca = intent.getStringExtra("KEY_MARCA");
            String modelo = intent.getStringExtra("KEY_MODELO");
            String cor = intent.getStringExtra("KEY_COR");
            String anoFabricacao = intent.getStringExtra("KEY_ANO_FABRICACAO");
            String anoModelo = intent.getStringExtra("KEY_ANO_MODELO");
            String placa = intent.getStringExtra("KEY_PLACA");
            String renavam = intent.getStringExtra("KEY_RENAVAM");
            String chassi = intent.getStringExtra("KEY_CHASSI");

            edtMarca.setText(marca);
            edtModelo.setText(modelo);
            edtCor.setText(cor);
            edtAnoFabricacao.setText(anoFabricacao);
            edtAnoModelo.setText(anoModelo);
            edtPlaca.setText(placa);
            edtRenavam.setText(renavam);
            edtChassi.setText(chassi);
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
        try {
            databaseReference.child(veiculoId).removeValue();
            MensagemFinal("Veículo excluído com sucesso!");
        } catch (Exception ex) {
            Toast.makeText( CadastroVeiculoActivity.this, "Problema ao excluir o cadastro de veículo!", Toast.LENGTH_LONG).show();
        }
    }

    private void Salvar() {
        if (Validar()) {
            String sMarca = edtMarca.getText().toString().trim();
            String sModelo = edtModelo.getText().toString().trim();
            String sCor = edtCor.getText().toString().trim();
            String sAnoFabricacao = edtAnoFabricacao.getText().toString().trim();
            String sAnoModelo = edtAnoModelo.getText().toString().trim();
            String sPlaca = edtPlaca.getText().toString().trim();
            String sRenavam = edtRenavam.getText().toString().trim();
            String sChassi = edtChassi.getText().toString().trim();

            Veiculo veiculo = new Veiculo();

            if (!novoRegistro)
                veiculo.setVeiculoId(veiculoId);

            veiculo.setMarca(sMarca);
            veiculo.setModelo(sModelo);
            veiculo.setCor(sCor);
            veiculo.setAnoFabricacao(sAnoFabricacao);
            veiculo.setAnoModelo(sAnoModelo);
            veiculo.setPlaca(sPlaca);
            veiculo.setRenavam(sRenavam);
            veiculo.setChassi(sChassi);

            if (novoRegistro) {
                Inserir(veiculo);
            } else {
                Alterar(veiculo);
            }
        }
    }

    private void Inserir(Veiculo veiculo) {
        try {
            veiculoId = databaseReference.push().getKey();
            databaseReference.child(veiculoId).setValue(veiculo);
            MensagemFinal("Cadastro inserido com sucesso!");
        } catch (Exception ex) {
            Toast.makeText( CadastroVeiculoActivity.this, "Problema ao inserir o cadastro de veículo!", Toast.LENGTH_LONG).show();
        }
    }

    private void Alterar(Veiculo veiculo) {
        try {
            veiculo.setVeiculoId(null);

            databaseReference = FirebaseDatabase.getInstance().getReference(C_TABLE_NAME).child(veiculoId);
            databaseReference.setValue(veiculo);
            MensagemFinal("Cadastro alterado com sucesso!");
        } catch (Exception ex) {
            Toast.makeText( CadastroVeiculoActivity.this, "Problema ao alterar o cadastro de veículo!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean Validar() {
        String sMarca = edtMarca.getText().toString().trim();
        String sModelo = edtModelo.getText().toString().trim();
        String sCor = edtCor.getText().toString().trim();
        String sAnoFabricacao = edtAnoFabricacao.getText().toString().trim();
        String sAnoModelo = edtAnoModelo.getText().toString().trim();
        String sPlaca = edtPlaca.getText().toString().trim();
        String sRenavam = edtRenavam.getText().toString().trim();
        String sChassi = edtChassi.getText().toString().trim();

        if (sMarca == null || sMarca.length() == 0) {
            Toast.makeText(CadastroVeiculoActivity.this, "A marca deve ser preenchida!", Toast.LENGTH_LONG).show();
            return false;
        } else if (sMarca.length() <  4) {
            Toast.makeText(CadastroVeiculoActivity.this, "A marca deve conter no mínimo 4 (quatro) caracteres!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (sModelo == null || sModelo.length() == 0) {
            Toast.makeText(CadastroVeiculoActivity.this, "O modelo deve ser preenchido!", Toast.LENGTH_LONG).show();
            return false;
        } else if (sModelo.length() <  3) {
            Toast.makeText(CadastroVeiculoActivity.this, "O modelo deve conter no mínimo 3 (três) caracteres!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (sCor == null || sCor.length() == 0) {
            Toast.makeText(CadastroVeiculoActivity.this, "A cor deve ser preenchida!", Toast.LENGTH_LONG).show();
            return false;
        } else if (sCor.length() <  4) {
            Toast.makeText(CadastroVeiculoActivity.this, "A cor deve conter no mínimo 4 (quatro) caracteres!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (sAnoFabricacao == null || sAnoFabricacao.length() == 0) {
            Toast.makeText(CadastroVeiculoActivity.this, "O ano de fabricação deve ser preenchido!", Toast.LENGTH_LONG).show();
            return false;
        } else if (sAnoFabricacao.length() != 4) {
            Toast.makeText(CadastroVeiculoActivity.this, "O ano de fabricação deve conter no 4 (quatro) números!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            try {
                Integer iNumero = Integer.parseInt(sAnoFabricacao);
            } catch (Exception ex) {
                Toast.makeText(CadastroVeiculoActivity.this, "O ano de fabricação deve ser numérico!", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (sAnoModelo == null || sAnoModelo.length() == 0) {
            Toast.makeText(CadastroVeiculoActivity.this, "O ano do modelo deve ser preenchido!", Toast.LENGTH_LONG).show();
            return false;
        } else if (sAnoModelo.length() != 4) {
            Toast.makeText(CadastroVeiculoActivity.this, "O ano do modelo deve conter no 4 (quatro) números!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            try {
                Integer iNumero = Integer.parseInt(sAnoModelo);
            } catch (Exception ex) {
                Toast.makeText(CadastroVeiculoActivity.this, "O ano do modelo deve ser numérico!", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (Integer.parseInt(sAnoModelo) > Integer.parseInt(sAnoFabricacao)) {
            Toast.makeText(CadastroVeiculoActivity.this, "O ano do modelo não pode ser maior do que o ano de fabricação!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            Integer diferenca = Integer.parseInt(sAnoFabricacao) - Integer.parseInt(sAnoModelo);

            if (diferenca > 1) {
                Toast.makeText(CadastroVeiculoActivity.this, "A diferença do ano de fabricação para o ano do modelo, não pode ser maior do 1 (hum).!", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (sPlaca == null || sPlaca.length() == 0) {
            Toast.makeText(CadastroVeiculoActivity.this, "A placa deve ser preenchida!", Toast.LENGTH_LONG).show();
            return false;
        } else if (sPlaca.length() <  6) {
            Toast.makeText(CadastroVeiculoActivity.this, "A placa deve conter no mínimo 6 (seis) caracteres!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (sRenavam == null || sRenavam.length() == 0) {
            Toast.makeText(CadastroVeiculoActivity.this, "O renavam deve ser preenchido!", Toast.LENGTH_LONG).show();
            return false;
        } else if (sRenavam.length() <  8) {
            Toast.makeText(CadastroVeiculoActivity.this, "O renavam deve conter no mínimo 8 (oito) caracteres!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (sChassi == null || sChassi.length() == 0) {
            Toast.makeText(CadastroVeiculoActivity.this, "O chassi deve ser preenchido!", Toast.LENGTH_LONG).show();
            return false;
        } else if (sChassi.length() <  8) {
            Toast.makeText(CadastroVeiculoActivity.this, "O chassi deve conter no mínimo 8 (oito) caracteres!", Toast.LENGTH_LONG).show();
            return false;
        }

        boolean encontrou = false;
        for (Veiculo veiculo : veiculos) {
            if (veiculo.getPlaca().equals(sPlaca) ||
                veiculo.getChassi().equals(sChassi) ||
                veiculo.getRenavam().equals(sRenavam)) {
                if (novoRegistro) {
                    encontrou = true;
                    break;
                } else {
                    if (!veiculo.getVeiculoId().equals(veiculoId)) {
                        encontrou = true;
                        break;
                    }
                }
            }
        }

        if (encontrou) {
            Toast.makeText(CadastroVeiculoActivity.this, "O veículo já existe cadastado (Placa ou Chassi ou Renavam)!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
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
        inflater.inflate(R.menu.cadastroveiculo, menu);

        return true;
    }

    private void Voltar() {
        Intent intent = new Intent(CadastroVeiculoActivity.this, ListaVeiculoActivity.class);
        startActivity(intent);
        finish();
    }

    private void MensagemFinal(String mensagem) {
        Toast.makeText(CadastroVeiculoActivity.this, mensagem, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(CadastroVeiculoActivity.this, ListaVeiculoActivity.class));
                finish();
            }
        }, 3000);
    }
}
package com.brunobatista.trabalhoandroid_2_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brunobatista.trabalhoandroid_2_1.model.Veiculo;

import java.util.List;

public class VeiculoAdapter extends RecyclerView.Adapter<VeiculoAdapter.ViewHolder> {

    private List<Veiculo> veiculos;

    private Context context;

    private OnDeleteClickListener onDeleteClickListener;
    private OnUpdateClickListener onUpdateClickListener;

    public VeiculoAdapter(
            Context _context,
            List<Veiculo> _veiculos,
            OnDeleteClickListener _onDeleteClickListener,
            OnUpdateClickListener _onUpdateClickListener) {
        this.context = _context;
        this.veiculos = _veiculos;

        this.onDeleteClickListener = _onDeleteClickListener;
        this.onUpdateClickListener = _onUpdateClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.veiculo_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Veiculo veiculo = this.veiculos.get(position);
        holder.lblMarca.setText(veiculo.getMarca());
        holder.lblModelo.setText(veiculo.getModelo());
        holder.lblCor.setText(veiculo.getCor());
        holder.lblPlaca.setText(veiculo.getPlaca());

        holder.btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(holder.getAdapterPosition());
                }
            }
        });

        holder.btnAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onUpdateClickListener != null) {
                    onUpdateClickListener.onUpdateClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.veiculos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblMarca, lblModelo, lblPlaca, lblCor;
        ImageButton btnExcluir, btnAlterar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lblMarca = itemView.findViewById(R.id.lblMarca);
            lblModelo = itemView.findViewById(R.id.lblModelo);
            lblPlaca = itemView.findViewById(R.id.lblPlaca);
            lblCor = itemView.findViewById(R.id.lblCor);

            btnExcluir = itemView.findViewById(R.id.btnExcluir);
            btnAlterar = itemView.findViewById(R.id.btnAlterar);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int posicao) ;
    }

    public interface OnUpdateClickListener {
        void onUpdateClick(int posicao) ;
    }
}

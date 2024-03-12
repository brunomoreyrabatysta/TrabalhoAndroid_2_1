package com.brunobatista.trabalhoandroid_2_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brunobatista.trabalhoandroid_2_1.model.Usuario;
import com.brunobatista.trabalhoandroid_2_1.model.Veiculo;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private List<Usuario> usuarios;

    private Context context;

    private OnDeleteClickListener onDeleteClickListener;
    private OnUpdateClickListener onUpdateClickListener;

    public UsuarioAdapter(
            Context _context,
            List<Usuario> _usuarios,
            OnDeleteClickListener _onDeleteClickListener,
            OnUpdateClickListener _onUpdateClickListener) {
        this.context = _context;
        this.usuarios = _usuarios;

        this.onDeleteClickListener = _onDeleteClickListener;
        this.onUpdateClickListener = _onUpdateClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuario_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario usuario = this.usuarios.get(position);
        holder.lblNome.setText(usuario.getNome());
        holder.lblEmail.setText(usuario.getEmail());

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
        return this.usuarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblNome, lblEmail;
        ImageButton btnExcluir, btnAlterar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lblNome = itemView.findViewById(R.id.lblNome);
            lblEmail = itemView.findViewById(R.id.lblEmail);

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

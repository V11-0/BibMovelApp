package com.bibmovel.client.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bibmovel.client.R;
import com.bibmovel.client.model.vo.Classificacao;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by vinibrenobr11 on 14/11/2018 at 13:47
 */
public class ClassificationAdapter extends RecyclerView.Adapter<ClassificationAdapter.ClassificationViewHolder> {

    private List<Classificacao> classificacoes;

    public ClassificationAdapter(List<Classificacao> classificacoes) {
        this.classificacoes = classificacoes;
    }

    @NonNull
    @Override
    public ClassificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClassificationViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_classification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClassificationViewHolder holder, int position) {

        Classificacao classificacao = classificacoes.get(position);

        holder.comment_user.setText(classificacao.getUsuario().getLogin());
        holder.comment_rating.setRating(classificacao.getClassificacao());
        holder.comment_comment.setText(classificacao.getComentario());
    }

    @Override
    public int getItemCount() {
        return classificacoes.size();
    }

    class ClassificationViewHolder extends RecyclerView.ViewHolder {

        private TextView comment_user;
        private RatingBar comment_rating;
        private TextView comment_comment;

        public ClassificationViewHolder(@NonNull View itemView) {
            super(itemView);

            comment_user = itemView.findViewById(R.id.comment_user);
            comment_rating = itemView.findViewById(R.id.comment_rating);
            comment_comment = itemView.findViewById(R.id.comment_comment);
        }
    }
}

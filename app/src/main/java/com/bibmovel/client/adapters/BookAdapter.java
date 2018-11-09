package com.bibmovel.client.adapters;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bibmovel.client.BookDetailsActivity;
import com.bibmovel.client.R;
import com.bibmovel.client.model.vo.Autor;
import com.bibmovel.client.model.vo.Livro;

import java.util.List;

/**
 * Created by vinibrenobr11 on 08/09/18 at 13:19
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Livro> books;

    public BookAdapter(List<Livro> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {

        Livro book = books.get(position);
        holder.bookName.setText(book.getTitulo());

        List<Autor> autores = book.getAutores();

        for (int i = 0; i < autores.size(); i++) {

            if (i > 0)
                holder.bookAuthor.append(", ");

            holder.bookAuthor.setText(autores.get(i).getNome());
        }

        holder.bookRating.setText(String.valueOf(book.getClassificacaoMedia()));

        holder.bookDownload.setOnClickListener(v -> {

            // TODO: 07/11/18 Baixar
        });

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), BookDetailsActivity.class);
            intent.putExtra("bookIsbn", books.get(holder.getAdapterPosition()).getIsbn());

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    class BookViewHolder extends RecyclerView.ViewHolder {

        private TextView bookName;
        private TextView bookAuthor;
        private TextView bookRating;
        private ImageButton bookDownload;

        BookViewHolder(View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.book_name);
            bookAuthor = itemView.findViewById(R.id.book_author);
            bookRating = itemView.findViewById(R.id.book_rating);
            bookDownload = itemView.findViewById(R.id.book_download);
        }
    }
}

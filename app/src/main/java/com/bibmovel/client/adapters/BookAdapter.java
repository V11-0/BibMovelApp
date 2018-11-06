package com.bibmovel.client.adapters;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bibmovel.client.BookDetailsActivity;
import com.bibmovel.client.R;
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
    private String url="";
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {

        //// TODO: 22/10/18 Pegar thumb do arquivo
        holder.bookName.setText(books.get(position).getTitulo());
        holder.bookRating.setText(String.valueOf(books.get(position).getClassificacaoMedia()));

        holder.menuOptions.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(v.getContext(), holder.menuOptions);
            popup.inflate(R.menu.book_menu);

            popup.setOnMenuItemClickListener(item -> {

                int id = item.getItemId();

                switch (id) {

                    case R.id.menu_baixar:

                }

                return true;
            });

            popup.show();
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

        private ImageView bookLogo;
        private TextView bookName;
        private TextView bookRating;
        private TextView menuOptions;

        BookViewHolder(View itemView) {
            super(itemView);

            bookLogo = itemView.findViewById(R.id.book_logo);
            bookName = itemView.findViewById(R.id.book_name);
            bookRating = itemView.findViewById(R.id.book_rating);
            menuOptions = itemView.findViewById(R.id.menuOptions);
        }
    }
}

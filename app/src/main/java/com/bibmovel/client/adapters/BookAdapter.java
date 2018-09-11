package com.bibmovel.client.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bibmovel.client.R;
import com.bibmovel.client.objects.Book;
import com.bibmovel.client.utils.BookColorHelper;

import java.util.List;

/**
 * Created by vinibrenobr11 on 08/09/18 at 13:19
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> books;

    public BookAdapter(List<Book> books) {
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

        holder.book_logo.setBackgroundColor(BookColorHelper.getColorByString(books.get(position).getCoverColor()));
        holder.book_name.setText(books.get(position).getName());
        holder.book_rating.setText(String.valueOf(books.get(position).getRating()));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    class BookViewHolder extends RecyclerView.ViewHolder {

        private ImageView book_logo;
        private TextView book_name;
        private TextView book_rating;

        BookViewHolder(View itemView) {
            super(itemView);

            book_logo = itemView.findViewById(R.id.book_logo);
            book_name = itemView.findViewById(R.id.book_name);
            book_rating = itemView.findViewById(R.id.book_rating);
        }
    }
}

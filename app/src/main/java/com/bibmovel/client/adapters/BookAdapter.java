package com.bibmovel.client.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bibmovel.client.BookDetailsActivity;
import com.bibmovel.client.R;
import com.bibmovel.client.model.vo.Livro;
import com.bibmovel.client.services.DownloadService;
import com.bibmovel.client.utils.Values;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.bookAuthor.setText(book.getAutor());
        holder.bookRating.setText(String.valueOf(book.getClassificacaoMedia()));
        holder.bookDownload.setOnClickListener(v -> {

            Context context = v.getContext();

            Intent download = new Intent(context, DownloadService.class);
            download.putExtra("isBook", true);
            download.putExtra("bookName", book.getNomeArquivo());

            context.startService(download);
        });

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), BookDetailsActivity.class);
            intent.putExtra("bookPath", books.get(holder.getAdapterPosition()).getNomeArquivo());

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void removeNotDownloadedBooks() {

        File download_folder = new File(Values.Path.DOWNLOAD_BOOKS);

        if (download_folder.isDirectory()) {

            for (File file : download_folder.listFiles()) {

                Predicate<Livro> livroPredicate = livro -> livro.getNomeArquivo()
                        .equals(file.getName());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    books.removeIf(livroPredicate);
            }
        }
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

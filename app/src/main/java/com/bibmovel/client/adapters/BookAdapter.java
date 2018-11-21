package com.bibmovel.client.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bibmovel.client.BookDetailsActivity;
import com.bibmovel.client.BuildConfig;
import com.bibmovel.client.R;
import com.bibmovel.client.model.vo.Livro;
import com.bibmovel.client.model.vo.Usuario;
import com.bibmovel.client.services.DownloadService;
import com.bibmovel.client.utils.DownloadResultReceiver;
import com.bibmovel.client.utils.ResulReceiverCallback;
import com.bibmovel.client.utils.Values;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by vinibrenobr11 on 08/09/18 at 13:19
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> implements ResulReceiverCallback {

    private List<Livro> mBooks;

    private GoogleSignInAccount mAccount;
    private Usuario mUser;

    public BookAdapter(List<Livro> mBooks, GoogleSignInAccount account) {
        this.mBooks = mBooks;
        this.mAccount = account;
    }

    public BookAdapter(List<Livro> mBooks, Usuario usuario) {
        this.mBooks = mBooks;
        this.mUser = usuario;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {

        Livro book = mBooks.get(position);

        holder.bookName.setText(book.getTitulo());
        holder.bookAuthor.setText(book.getAutor());
        holder.bookRating.setText(String.valueOf(book.getClassificacaoMedia()));

        Context context = holder.bookName.getContext();

        if (book.isDownloaded()) {

            holder.bookBar.setVisibility(View.GONE);
            holder.bookButton.setVisibility(View.VISIBLE);

            holder.bookButton.setImageDrawable(context.getDrawable(R.drawable.ic_description_black_24dp));
            holder.bookButton.setOnClickListener(v -> {

                Intent pdf_intent = new Intent(Intent.ACTION_VIEW);
                Uri file_uri;
                File file = new File(Values.Path.DOWNLOAD_BOOKS + "/" + book.getNomeArquivo());

                // Para inferior a versão 7.0 do Android
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                    file_uri = Uri.fromFile(file);
                else {
                    // Para Versão 7.0 ou superior do Android
                    file_uri = FileProvider.getUriForFile(context,
                            BuildConfig.APPLICATION_ID + ".provider", file);
                }

                pdf_intent.setDataAndType(file_uri, "application/pdf");
                pdf_intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                pdf_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                context.startActivity(pdf_intent);
            });

        } else {

            holder.bookButton.setOnClickListener(v -> {

                holder.bookButton.setVisibility(View.GONE);
                holder.bookBar.setVisibility(View.VISIBLE);

                DownloadResultReceiver receiver = new DownloadResultReceiver(new Handler(), this);

                Intent download = new Intent(context, DownloadService.class);
                download.putExtra("isBook", true);
                download.putExtra("bookName", book.getNomeArquivo());
                download.putExtra("receiver", receiver);

                context.startService(download);
            });
        }

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), BookDetailsActivity.class);
            intent.putExtra("bookPath", mBooks.get(holder.getAdapterPosition()).getNomeArquivo());

            if (mUser == null)
                intent.putExtra("google_account", mAccount);
            else if (mAccount == null)
                intent.putExtra("user", mUser);

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public void showDownloaded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            mBooks.removeIf(livro -> !livro.isDownloaded());
        else
            for (Livro livro: mBooks)
                if (livro.isDownloaded())
                    mBooks.remove(livro);
    }

    @Override
    public void onDownloaded(String file_name) {

        for (Livro l : mBooks) {

            if (l.getNomeArquivo().equals(file_name)) {
                l.setDownloaded(true);
                notifyItemChanged(mBooks.indexOf(l));
                break;
            }
        }
    }

    class BookViewHolder extends RecyclerView.ViewHolder {

        private TextView bookName;
        private TextView bookAuthor;
        private TextView bookRating;
        private ImageButton bookButton;
        private ProgressBar bookBar;

        BookViewHolder(View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.book_name);
            bookAuthor = itemView.findViewById(R.id.book_author);
            bookRating = itemView.findViewById(R.id.book_rating);
            bookButton = itemView.findViewById(R.id.book_button);
            bookBar = itemView.findViewById(R.id.book_progress);
        }
    }
}

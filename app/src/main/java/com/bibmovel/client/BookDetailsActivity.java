package com.bibmovel.client;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bibmovel.client.adapters.ClassificationAdapter;
import com.bibmovel.client.model.vo.Classificacao;
import com.bibmovel.client.model.vo.Livro;
import com.bibmovel.client.model.vo.Usuario;
import com.bibmovel.client.retrofit.ClassificacaoService;
import com.bibmovel.client.retrofit.LivroService;
import com.bibmovel.client.retrofit.RetroFitInstance;
import com.bibmovel.client.utils.DownloadImage;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookDetailsActivity extends AppCompatActivity implements Observer {

    private Livro livro;
    private CollapsingToolbarLayout mToolbar;

    private Retrofit instance;

    private Usuario mUser;
    private GoogleSignInAccount mAccount;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        // TODO: 19/11/18 Melhorar Layout

        mToolbar = findViewById(R.id.collapsing_toolbar);
        mToolbar.setTitle("Titúlo");

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Deixa os ícones da barra de navegação escuros para Android Oreo ou superior
        if (Build.VERSION.SDK_INT >= 26) {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        String bookPath = getIntent().getStringExtra("bookPath");
        mUser = getIntent().getParcelableExtra("user");
        mAccount = getIntent().getParcelableExtra("google_account");

        instance = RetroFitInstance.getRetrofitInstance();

        LivroService service = instance.create(LivroService.class);
        service.getLivro(bookPath, "nomeArquivo").enqueue(new Callback<Livro>() {

            @Override
            public void onResponse(Call<Livro> call, Response<Livro> response) {
                livro = response.body();
                setDataAndGetComments();
            }

            @Override
            public void onFailure(Call<Livro> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setDataAndGetComments() {

        EditText edt_isbn = findViewById(R.id.det_isbn);
        EditText edt_gender = findViewById(R.id.det_gender);
        EditText edt_year = findViewById(R.id.det_publ_year);
        EditText edt_avg = findViewById(R.id.det_avg_class);
        EditText edt_editor = findViewById(R.id.det_editor);
        EditText edt_author = findViewById(R.id.det_author);

        mToolbar.setTitle(livro.getTitulo());
        edt_isbn.setText(livro.getIsbn());
        edt_gender.setText(livro.getGenero());
        edt_year.setText(String.valueOf(livro.getAnoPublicacao()));
        edt_avg.setText(String.valueOf(livro.getClassificacaoMedia()));
        edt_editor.setText(livro.getEditora());
        edt_author.setText(livro.getAutor());

        recyclerView = findViewById(R.id.rv_comment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL
                , false));

        getComments();

        new DownloadImage(livro.getNomeArquivo(), this);
    }

    private void getComments() {

        ClassificacaoService classificacaoService = instance.create(ClassificacaoService.class);
        classificacaoService.getClassificacoes(livro.getIsbn()).enqueue(new Callback<List<Classificacao>>() {
            @Override
            public void onResponse(Call<List<Classificacao>> call, Response<List<Classificacao>> response) {

                TextView no_comments = findViewById(R.id.comment_no_comments);
                List<Classificacao> body = response.body();

                if ( (body != null) && (body.size() > 0) ) {
                    no_comments.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(new ClassificationAdapter(body));
                } else {
                    recyclerView.setVisibility(View.GONE);
                    no_comments.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Classificacao>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.detail_search_author:
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, livro.getAutor());

                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showCommentDialog(View view) {

        View dialog_view = getLayoutInflater().inflate(R.layout.dialog_comment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialog_view);

        AlertDialog dialog = builder.create();
        dialog.show();

        FloatingActionButton fab = dialog_view.findViewById(R.id.dialog_fab);

        fab.setOnClickListener(v -> {

            ProgressBar bar = dialog_view.findViewById(R.id.dialog_comment_progress);
            v.setVisibility(View.GONE);
            bar.setVisibility(View.VISIBLE);

            RatingBar ratingBar = dialog_view.findViewById(R.id.dialog_rating);
            EditText edt_comment = dialog_view.findViewById(R.id.dialog_comment);

            float rating = ratingBar.getRating();
            String comment = edt_comment.getText().toString();

            Classificacao classificacao = new Classificacao();

            if (mAccount == null)
                classificacao.setUsuario(mUser);
            else if (mUser == null)
                classificacao.setUsuario(new Usuario(mAccount.getGivenName()));

            classificacao.setLivro(livro);
            classificacao.setClassificacao(rating);
            classificacao.setComentario(comment);

            RetroFitInstance.getRetrofitInstance().create(ClassificacaoService.class)
                    .createClassificacao(classificacao).enqueue(new Callback<Classificacao>() {
                @Override
                public void onResponse(Call<Classificacao> call, Response<Classificacao> response) {

                    if (response.isSuccessful()) {
                        Toast.makeText(v.getContext(), "Classificação Inserida", Toast.LENGTH_LONG).show();
                        getComments();
                    }
                    else
                        Toast.makeText(v.getContext(), "Ocorreu um erro", Toast.LENGTH_LONG).show();

                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Classificacao> call, Throwable t) {
                    t.printStackTrace();
                    dialog.dismiss();
                    Toast.makeText(v.getContext(), "Ocorreu um erro", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    public void update(Observable o, Object arg) {

        Bitmap cover = (Bitmap) arg;

        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animation.setDuration(2000);

        ImageView cover_view = findViewById(R.id.book_cover);
        cover_view.setImageBitmap(cover);
        cover_view.startAnimation(animation);
    }
}

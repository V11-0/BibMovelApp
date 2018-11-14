package com.bibmovel.client;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.bibmovel.client.adapters.ClassificationAdapter;
import com.bibmovel.client.model.vo.Classificacao;
import com.bibmovel.client.model.vo.Livro;
import com.bibmovel.client.retrofit.ClassificacaoService;
import com.bibmovel.client.retrofit.LivroService;
import com.bibmovel.client.retrofit.RetroFitInstance;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookDetailsActivity extends AppCompatActivity {

    private Livro livro;
    private CollapsingToolbarLayout mToolbar;

    private Retrofit instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        mToolbar = findViewById(R.id.collapsing_toolbar);
        mToolbar.setTitle("Titúlo");

        String bookPath = getIntent().getStringExtra("bookPath");

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

        RecyclerView recyclerView = findViewById(R.id.rv_comment);

        ClassificacaoService classificacaoService = instance.create(ClassificacaoService.class);
        classificacaoService.getClassificacoes(livro.getIsbn()).enqueue(new Callback<List<Classificacao>>() {
            @Override
            public void onResponse(Call<List<Classificacao>> call, Response<List<Classificacao>> response) {
                recyclerView.setAdapter(new ClassificationAdapter(response.body()));
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
                Intent intent = new Intent(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, livro.getAutor());
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.bibmovel.client;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.bibmovel.client.model.vo.Autor;
import com.bibmovel.client.model.vo.Livro;
import com.bibmovel.client.retrofit.LivroService;
import com.bibmovel.client.retrofit.RetroFitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailsActivity extends AppCompatActivity {

    private Livro livro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        String bookIsbn = getIntent().getStringExtra("bookIsbn");

        LivroService service = RetroFitInstance.getRetrofitInstance().create(LivroService.class);

        service.getLivro(bookIsbn).enqueue(new Callback<Livro>() {

            @Override
            public void onResponse(Call<Livro> call, Response<Livro> response) {
                livro = response.body();
                setData();
            }

            @Override
            public void onFailure(Call<Livro> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setData() {

        EditText edt_title = findViewById(R.id.det_title);
        EditText edt_isbn = findViewById(R.id.det_isbn);
        EditText edt_gender = findViewById(R.id.det_gender);
        EditText edt_year = findViewById(R.id.det_publ_year);
        EditText edt_avg = findViewById(R.id.det_avg_class);
        EditText edt_editor = findViewById(R.id.det_editor);
        EditText edt_author = findViewById(R.id.det_author);

        edt_title.setText(livro.getTitulo());
        edt_isbn.setText(livro.getIsbn());
        edt_gender.setText(livro.getGenero());
        edt_year.setText(String.valueOf(livro.getAnoPublicacao()));
        edt_avg.setText(String.valueOf(livro.getClassificacaoMedia()));
        edt_editor.setText(livro.getEditora().getNome());

        for (Autor a : livro.getAutores()) {
            edt_author.append(a.getNome());
        }
    }
}

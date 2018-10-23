package com.bibmovel.client.retrofit;

import com.bibmovel.client.model.vo.Livro;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by vinibrenobr11 on 17/10/18 at 17:57
 */
public interface LivrosService {

    @GET("livros")
    Call<List<Livro>> getLivros();

    @GET("livros/{isbn}")
    Call<Livro> getLivro(@Path("isbn") String isbn);

    @POST("livros")
    Call<Livro> criarLivro(@Body Livro livro);
}

package com.bibmovel.client.retrofit;

import com.bibmovel.client.model.vo.Livro;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by vinibrenobr11 on 17/10/18 at 17:57
 */
public interface LivroService {

    @GET("livro")
    Call<List<Livro>> getLivros();

    @GET("livro/{value}")
    Call<Livro> getLivro(@Path("value") String value, @Query("column") String column);

    @POST("livro")
    Call<Livro> criarLivro(@Body Livro livro);
}

package com.bibmovel.client.retrofit;

import com.bibmovel.client.model.vo.Classificacao;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by vinibrenobr11 on 14/11/2018 at 14:18
 */
public interface ClassificacaoService {

    @GET("classificacao/{livroIsbn}")
    Call<List<Classificacao>> getClassificacoes(@Path("livroIsbn") String livroIsbn);

    @POST("classificacao")
    Call<Classificacao> createClassificacao(@Body Classificacao classificacao);
}

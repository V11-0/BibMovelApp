package com.bibmovel.client.retrofit;

import com.bibmovel.client.model.vo.Classificacao;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by vinibrenobr11 on 14/11/2018 at 14:18
 */
public interface ClassificacaoService {

    @GET("classificacao")
    Call<List<Classificacao>> getClassificacoes(String livroIsbn);
}

package com.bibmovel.client.retrofit;

import com.bibmovel.client.model.vo.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by vinibrenobr11 on 27/10/18 at 16:03
 */
public interface UsuarioService {

    @POST("usuario/login")
    Call<Usuario> login(@Body Usuario usuario);

    @POST("usuario")
    Call<Usuario> createUser(@Body Usuario usuario);

    @POST("usuario/google")
    Call<Usuario> verifyGoogleAccount(@Body Usuario google);
}

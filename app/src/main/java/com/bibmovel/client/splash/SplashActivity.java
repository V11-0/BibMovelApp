package com.bibmovel.client.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bibmovel.client.LoginActivity;
import com.bibmovel.client.MainActivity;
import com.bibmovel.client.R;
import com.bibmovel.client.model.vo.Usuario;
import com.bibmovel.client.utils.Values;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


/**
 * Created by vinibrenobr11 on 03/03/2017 at 00:54<br />
 * <p>
 * Essa Classe que tem a função de gerenciar e exibir tela
 * de "Splash", ou de apresentação no início da execução
 * do app, ultilizando uma interface @{@link Runnable}
 */
public class SplashActivity extends Activity implements Runnable {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Deixa os ícones da barra de navegação escuros para Android Oreo ou superior
        if (Build.VERSION.SDK_INT >= 26) {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        ImageView logo = findViewById(R.id.splash_logo);
        Animation logoAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        logoAnimation.setDuration(1800);
        logo.startAnimation(logoAnimation);

        // Espera 2,5 sec para executar o método run()
        Handler handler = new Handler();
        handler.postDelayed(this, 2500);
    }

    /**
     * Método da interface Runnable que inicia a Activity especificada
     */
    @Override
    public void run() {

        ProgressBar progressBar = findViewById(R.id.splash_bar);
        progressBar.setVisibility(View.VISIBLE);

        Intent it;

        GoogleSignInAccount signedInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (signedInAccount != null)
            it = new Intent(this, MainActivity.class).putExtra("google_account", signedInAccount);
        else {

            SharedPreferences prefs = getSharedPreferences(Values.Preferences.PREFS_LOGIN, MODE_PRIVATE);

            if (prefs.getBoolean(Values.Preferences.IS_LOGEED_VALUE_NAME, false)) {

                String login = prefs.getString(Values.Preferences.USER_LOGIN_VALUE_NAME, null);
                String email = prefs.getString(Values.Preferences.USER_EMAIL_VALUE_NAME, null);

                Usuario usuario = new Usuario();
                usuario.setLogin(login);
                usuario.setEmail(email);

                it = new Intent(this, MainActivity.class).putExtra("user", usuario);
            } else it = new Intent(this, LoginActivity.class);
        }

        startActivity(it);
        finish();
    }
}

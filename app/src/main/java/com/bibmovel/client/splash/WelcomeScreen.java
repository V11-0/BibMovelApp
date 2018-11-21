package com.bibmovel.client.splash;


import android.os.Build;

import com.bibmovel.client.R;
import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

/**
 * Created by vinibrenobr11 on 27/10/18 at 00:51
 */
public class WelcomeScreen extends WelcomeActivity {

    @Override
    protected WelcomeConfiguration configuration() {

        WelcomeConfiguration.Builder builder = new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.welcome_background1)

                .page(new TitlePage(0, "O que é o aplicativo?"))
                .page(new BasicPage(0, "BibMóvel", "Este é um aplicativo de gerenciamento de livros" +
                        " que te possibilita compartilhar e baixar sem poblemas.")
                        .background(R.color.welcome_background2))
                .page(new BasicPage(0, "Esperamos que goste", "Utilize-o com responsabilidades.")
                        .background(R.color.welcome_background3));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            builder.page(new BasicPage(0, "E para tudo funcionar...", "Nos de as permissões necessárias na proxima tela"));

        builder.swipeToDismiss(true);

        return builder.build();
    }
}

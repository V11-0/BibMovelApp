package com.bibmovel.client.splash;


import com.bibmovel.client.R;
import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.stephentuso.welcome.WelcomeActivity;

/**
 * Created by vinibrenobr11 on 27/10/18 at 00:51
 */
public class WelcomeScreen extends WelcomeActivity {

    @Override
    protected WelcomeConfiguration configuration() {

        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.welcome_background1)

                .page(new TitlePage(0, "Oque é o aplicativo"))
                .page(new BasicPage(0, "Header", "BibMovel é um aplicativo de gerenciamento de livros" +
                        " que te possibilita compartilhar e baixar sem poblemas.")
                        .background(R.color.welcome_background2))
                .page(new BasicPage(0, "Header", "Otilize-o com responsabilidades.")
                        .background(R.color.welcome_background3))

                .swipeToDismiss(true)
                .build();
    }
}

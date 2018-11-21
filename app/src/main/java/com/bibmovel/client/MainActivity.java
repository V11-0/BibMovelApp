package com.bibmovel.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bibmovel.client.adapters.BookAdapter;
import com.bibmovel.client.model.vo.Livro;
import com.bibmovel.client.model.vo.Usuario;
import com.bibmovel.client.retrofit.LivroService;
import com.bibmovel.client.retrofit.RetroFitInstance;
import com.bibmovel.client.settings.SettingsActivity;
import com.bibmovel.client.utils.Values;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GoogleSignInAccount mGoogleSignInAccount = null;
    private Usuario mUser = null;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private NavigationView navigationView;

    private Call<List<Livro>> listCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, Values.Codes.PICKFILE_REQUEST_CODE);
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_books_all);
        getSupportActionBar().setTitle("Todos os Livros");

        recyclerView = findViewById(R.id.rv_books);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView nav_name = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        TextView nav_email = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);

        mGoogleSignInAccount = getIntent().getParcelableExtra("google_account");
        mUser = getIntent().getParcelableExtra("user");

        if (mUser != null) {
            nav_name.setText(mUser.getLogin());
            nav_email.setText(mUser.getEmail());
        } else if (mGoogleSignInAccount != null) {
            nav_name.setText(mGoogleSignInAccount.getDisplayName());
            nav_email.setText(mGoogleSignInAccount.getEmail());
        }

        LivroService service = RetroFitInstance.getRetrofitInstance().create(LivroService.class);

        listCall = service.getLivros();
        Log.d("URL", listCall.request().url().toString());

        listCall.enqueue(buildBooksCallback());

        mSwipeRefreshLayout = findViewById(R.id.main_swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeColors(Values.Colors.SWIPE_REFRESH_SCHEME);
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(() -> listCall.clone().enqueue(buildBooksCallback()));
    }

    private Callback<List<Livro>> buildBooksCallback() {

        return new Callback<List<Livro>>() {
            @Override
            public void onResponse(Call<List<Livro>> call, Response<List<Livro>> response) {

                Log.d("URL", "Entrou no onResponse");

                if (response.body() != null) {

                    showRecyclerView();

                    List<Livro> livros = response.body();
                    BookAdapter adapter = null;

                    File download_folder = new File(Values.Path.DOWNLOAD_BOOKS);

                    if (download_folder.isDirectory())
                        for (File file : download_folder.listFiles())
                            for (Livro livro : livros) {
                                if (livro.isDownloaded())
                                    continue;

                                livro.setDownloaded(file.getName().equals(livro.getNomeArquivo()));
                            }

                    if (mGoogleSignInAccount == null)
                        adapter = new BookAdapter(livros, mUser);
                    else if (mUser == null)
                        adapter = new BookAdapter(livros, mGoogleSignInAccount);

                    recyclerView.setAdapter(adapter);
                    recyclerView.scheduleLayoutAnimation();

                } else {
                    Log.wtf("onResponse", "There is no Response");
                    dismissRecyclerAndShowMessage(getString(R.string.no_books));
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Livro>> call, Throwable t) {
                t.printStackTrace();
                dismissRecyclerAndShowMessage(t.getMessage());
            }
        };
    }

    private void dismissRecyclerAndShowMessage(String message) {
        recyclerView.setVisibility(View.GONE);

        TextView text = findViewById(R.id.main_error_text);
        text.setVisibility(View.VISIBLE);
        text.setText(message);

        mSwipeRefreshLayout.setRefreshing(false);

        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.nav_books_all).setVisible(false);
        nav_menu.findItem(R.id.nav_books_downloaded).setVisible(false);
    }

    private void showRecyclerView() {
        findViewById(R.id.main_error_text).setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.nav_books_all).setVisible(true);
        nav_menu.findItem(R.id.nav_books_downloaded).setVisible(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Values.Codes.PICKFILE_REQUEST_CODE) {

            if (data != null) {

                File file = new File(data.getData().getPath());

                // TODO: 06/11/2018 Realizar Upload
                Toast.makeText(this, file.getName(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void signOut() {

        if (mGoogleSignInAccount != null) {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            GoogleSignInClient client = GoogleSignIn.getClient(this, gso);
            client.signOut();

        } else if (mUser != null) {

            SharedPreferences loginPreferences = getSharedPreferences(Values.Preferences.PREFS_LOGIN
                    , MODE_PRIVATE);

            SharedPreferences.Editor editor = loginPreferences.edit();

            editor.putBoolean(Values.Preferences.IS_LOGEED_VALUE_NAME, false);
            editor.remove(Values.Preferences.USER_LOGIN_VALUE_NAME);
            editor.remove(Values.Preferences.USER_EMAIL_VALUE_NAME);

            editor.apply();
        }

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_books_all:
                mSwipeRefreshLayout.setRefreshing(true);
                listCall.clone().enqueue(buildBooksCallback());
                break;

            case R.id.nav_books_downloaded:
                getSupportActionBar().setTitle("Livros Baixados");
                BookAdapter adapter = (BookAdapter) recyclerView.getAdapter();
                adapter.showDownloaded();
                recyclerView.setAdapter(adapter);
                recyclerView.scheduleLayoutAnimation();
                break;

            case R.id.nav_exit:
                signOut();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

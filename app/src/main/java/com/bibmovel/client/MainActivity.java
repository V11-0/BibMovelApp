package com.bibmovel.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GoogleSignInAccount mGoogleSignInAccount = null;
    private Usuario mUser = null;

    private RecyclerView recyclerView;

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

        NavigationView navigationView = findViewById(R.id.nav_view);
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

        Call<List<Livro>> listCall = service.getLivros();
        Log.d("URL", listCall.request().url().toString());

        listCall.enqueue(new Callback<List<Livro>>() {
            @Override
            public void onResponse(Call<List<Livro>> call, Response<List<Livro>> response) {

                Log.d("URL", "Entrou no onResponse");

                if (response.body() != null) {
                    List<Livro> livros = response.body();
                    BookAdapter adapter = new BookAdapter(livros);
                    recyclerView.setAdapter(adapter);
                    recyclerView.scheduleLayoutAnimation();
                } else {
                    Log.wtf("onResponse", "There is no Response");
                }

            }

            @Override
            public void onFailure(Call<List<Livro>> call, Throwable t) {
                t.printStackTrace();
            }
        });
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
                break;

            case R.id.nav_books_downloaded:
                BookAdapter adapter = (BookAdapter) recyclerView.getAdapter();
                adapter.removeNotDownloadedBooks();
                recyclerView.setAdapter(adapter);
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

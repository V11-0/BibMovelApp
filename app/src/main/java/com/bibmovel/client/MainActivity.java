package com.bibmovel.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bibmovel.client.adapters.BookAdapter;
import com.bibmovel.client.model.vo.Livro;
import com.bibmovel.client.model.vo.Usuario;
import com.bibmovel.client.retrofit.LivroService;
import com.bibmovel.client.retrofit.RetroFitInstance;
import com.bibmovel.client.services.UploadService;
import com.bibmovel.client.settings.SettingsActivity;
import com.bibmovel.client.utils.Values;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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

        // Deixa os ícones da barra de navegação escuros para Android Oreo ou superior
        if (Build.VERSION.SDK_INT >= 26) {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(v -> new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(Values.Codes.PICKFILE_REQUEST_CODE)
                .withFilter(Pattern.compile(".*\\.pdf$")) // Filtering files and directories by file name using regexp
                .withHiddenFiles(false) // Show hidden files and folders
                .start());

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
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.menu_common, menu);

        // Associate searchable configuration with the SearchView
        /*SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.main_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                BookAdapter adapter = (BookAdapter) recyclerView.getAdapter();
                recyclerView.setAdapter(adapter);

                List<Livro> books = adapter.getBooks();

                for (int i = 0; i < books.size(); i++) {

                    if (!books.get(i).getTitulo().contains(query))
                        recyclerView.removeViewAt(i);
                }

                if (recyclerView.getChildAt(0) == null)
                    Snackbar.make(searchView, "Não há livros com esse titulo", Snackbar.LENGTH_LONG)
                            .show();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.item_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Values.Codes.PICKFILE_REQUEST_CODE) {

            if (data != null) {

                String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                File file = new File(path);

                Intent upload = new Intent(this, UploadService.class);
                upload.putExtra("file", file);

                startService(upload);

                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle("Seu arquivo está sendo enviado" +
                        "\nMas precisamos de algumas informações");

                View view = LayoutInflater.from(this).inflate(R.layout.dialog_send_book, null);

                dlg.setView(view);
                dlg.setNegativeButton("Cancelar", null);
                dlg.setPositiveButton("Ok", (dialog, which) -> {

                    EditText edt_titulo = view.findViewById(R.id.send_titulo);
                    EditText edt_isbn = view.findViewById(R.id.send_isbn);
                    EditText edt_genero = view.findViewById(R.id.send_genero);
                    EditText edt_ano = view.findViewById(R.id.send_ano);
                    EditText edt_autor = view.findViewById(R.id.send_autor);
                    EditText edt_editora = view.findViewById(R.id.send_editora);

                    Livro livro = new Livro();
                    livro.setTitulo(edt_titulo.getText().toString());
                    livro.setIsbn(edt_isbn.getText().toString());
                    livro.setGenero(edt_genero.getText().toString());
                    livro.setAnoPublicacao(Short.valueOf(edt_ano.getText().toString()));
                    livro.setAutor(edt_autor.getText().toString());
                    livro.setEditora(edt_editora.getText().toString());

                    livro.setNomeArquivo(file.getName());

                    Retrofit retrofit = RetroFitInstance.getRetrofitInstance();
                    Call<Livro> livroCall = retrofit.create(LivroService.class).criarLivro(livro);

                    livroCall.enqueue(new Callback<Livro>() {
                        @Override
                        public void onResponse(Call<Livro> call, Response<Livro> response) {

                            if (response.isSuccessful() && response.body() != null)
                                Toast.makeText(dlg.getContext(), "Sucesso", Toast.LENGTH_LONG).show();
                            else
                                Log.wtf("Response", "With no response");
                        }

                        @Override
                        public void onFailure(Call<Livro> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(dlg.getContext(), "Falhou", Toast.LENGTH_LONG).show();
                        }
                    });
                });

                dlg.create().show();
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
                mSwipeRefreshLayout.setEnabled(true);
                mSwipeRefreshLayout.setRefreshing(true);
                listCall.clone().enqueue(buildBooksCallback());
                getSupportActionBar().setTitle("Todos os Livrosg");
                break;

            case R.id.nav_books_downloaded:
                getSupportActionBar().setTitle("Livros Baixados");
                BookAdapter adapter = (BookAdapter) recyclerView.getAdapter();
                adapter.showDownloaded();
                recyclerView.setAdapter(adapter);
                recyclerView.scheduleLayoutAnimation();
                mSwipeRefreshLayout.setEnabled(false);
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

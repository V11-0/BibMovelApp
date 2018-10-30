package com.bibmovel.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bibmovel.client.model.vo.Usuario;
import com.bibmovel.client.retrofit.RetroFitInstance;
import com.bibmovel.client.retrofit.UsuarioService;
import com.bibmovel.client.settings.SettingsActivity;
import com.bibmovel.client.splash.WelcomeScreen;
import com.bibmovel.client.utils.Values;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.stephentuso.welcome.WelcomeHelper;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;

    private EditText edtUser;
    private EditText edtPass;

    private WelcomeHelper mWelcomeHelper;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWelcomeHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWelcomeHelper = new WelcomeHelper(this, WelcomeScreen.class);
        mWelcomeHelper.show(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.g_sign_in).setOnClickListener(v -> startGoogleSignIn());

        edtUser = findViewById(R.id.edt_user);
        edtPass = findViewById(R.id.edt_pass);

        findViewById(R.id.btn_login).setOnClickListener(v -> {

            String login = edtUser.getText().toString();

            Usuario user = new Usuario(login, new String(Hex.encodeHex(DigestUtils
                    .sha256(edtPass.getText().toString()))));

            login(user);
        });
    }

    private void startGoogleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Values.getRcGSignIn());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Values.getRcGSignIn()) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult();
                login(account);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void login(Usuario usuario) {

        UsuarioService service = RetroFitInstance.getRetrofitInstance().create(UsuarioService.class);

        service.login(usuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                if (response.isSuccessful()) {
                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                    it.putExtra("user_login", usuario.getLogin());

                    SharedPreferences.Editor editor = getSharedPreferences(Values.getPrefsLogin()
                            , MODE_PRIVATE).edit();

                    editor.putBoolean(Values.getIsLogeedValueName(), true);
                    editor.putString(Values.getUserLoginValueName(), usuario.getLogin());

                    editor.apply();

                    startActivity(it);
                }
                else if (response.code() == 404) {
                    edtUser.setError("Credenciais Incorretas");
                    edtPass.setError("Credenciais Incorretas");
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                t.printStackTrace();
                edtUser.setError(t.getMessage());
            }
        });
    }

    private void login(GoogleSignInAccount account) {

        Intent it = new Intent(this, MainActivity.class);
        it.putExtra("google_account", account);

        startActivity(it);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.item_register:
                register();
                break;
            case R.id.item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void register() {

        View dialog_view = View.inflate(this, R.layout.dialog_register, null);

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle("Cadastrar")
                .setView(dialog_view)
                .setPositiveButton("Registrar", null)
                .setNegativeButton("Cancelar", null)
                .setCancelable(false)
                .create();

        dlg.show();

        Button positiveButton = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {

            ProgressBar progressBar = dialog_view.findViewById(R.id.dialog_progress);
            progressBar.setVisibility(View.VISIBLE);

            EditText edt_login = dialog_view.findViewById(R.id.edt_register_login);
            EditText edt_name = dialog_view.findViewById(R.id.edt_register_name);
            EditText edt_email = dialog_view.findViewById(R.id.edt_register_email);
            EditText edt_pass = dialog_view.findViewById(R.id.edt_register_pass);

            Usuario usuario = new Usuario(edt_login.getText().toString()
                    , edt_name.getText().toString(), edt_email.getText().toString()
                    , new String(Hex.encodeHex(DigestUtils.sha256(edt_pass.getText().toString()))));

            UsuarioService service = RetroFitInstance.getRetrofitInstance()
                    .create(UsuarioService.class);

            service.createUser(usuario).enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                    if (response.code() == 200) {
                        Snackbar.make(LoginActivity.this.edtUser, "Usuário Criado"
                                , Snackbar.LENGTH_LONG).show();
                        dlg.dismiss();
                    }
                    else if (response.code() == 409) {
                        Toast.makeText(LoginActivity.this, "Já existe um usuário com este" +
                                " login, por favor, escolha outro", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    t.printStackTrace();
                    edt_login.setError(t.getMessage());
                    progressBar.setVisibility(View.GONE);
                }
            });
        });
    }
}

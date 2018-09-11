package com.bibmovel.client;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.bibmovel.client.settings.SettingsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;

    private EditText edtUser;
    private EditText edtPass;

    private static final int RC_SIGN_IN = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.g_sign_in).setOnClickListener(v -> signIn());

        edtUser = findViewById(R.id.edt_user);
        edtPass = findViewById(R.id.edt_pass);

        findViewById(R.id.btn_login).setOnClickListener(v -> {

            String user = edtUser.getText().toString();
            String pass = edtPass.getText().toString();

            // TODO: 11/08/18 Verificação dos dados

            logOn(user, pass);
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult();
            logOn(account);
        }

    }

    private void logOn(String user, String pass) {

        if (user.equals("foo") && pass.equals("bar")) {
            // TODO: 11/08/18 Pegar credenciais do servidor usando asynctask
            Intent it = new Intent(this, MainActivity.class);
            it.putExtra("user", user);
            it.putExtra("email", "email");

            startActivity(it);

        } else
            edtUser.setError("Credenciais incorretas");
    }

    private void logOn(GoogleSignInAccount account) {

        Intent it = new Intent(this, MainActivity.class);
        it.putExtra("google_account", account);

        startActivity(it);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Verifica se usuário já está logado com conta google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null)
            logOn(account);
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
                // TODO: 10/08/18 Cadastro por dialog
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setView(R.layout.dialog_register)
                        .setTitle("Cadastrar")
                        .setPositiveButton("Continuar", (dialog, which) -> {

                }).create().show();
                break;
            case R.id.item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

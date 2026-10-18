package com.nordhub.aguiachallenge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nordhub.aguiachallenge.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etSenha;
    private Button btnLogin, btnCadastro;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);

        btnLogin = findViewById(R.id.btnLogin);
        btnCadastro = findViewById(R.id.btnCadastro);

        btnCadastro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {

        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Informe email e senha", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener(authResult -> {

                    if (auth.getCurrentUser() == null) {
                        Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String uid = auth.getCurrentUser().getUid();

                    db.collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener(document -> {

                                String tipo = document.getString("tipo");
                                if (tipo == null) {
                                    Toast.makeText(this, "Perfil não cadastrado", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Intent intent;

                                switch (tipo.toLowerCase()) {

                                    case "operador":
                                        intent = new Intent(this, OperatorActivity.class);
                                        break;

                                    case "gestor":
                                        intent = new Intent(this, ManagerActivity.class);
                                        break;

                                    case "lider":
                                        intent = new Intent(this, LeaderActivity.class);
                                        break;

                                    default:
                                        auth.signOut();
                                        Toast.makeText(this, "Perfil inválido. Consulte a administração.", Toast.LENGTH_SHORT).show();
                                        return;
                                }

                                startActivity(intent);
                                finish();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Erro no login: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}

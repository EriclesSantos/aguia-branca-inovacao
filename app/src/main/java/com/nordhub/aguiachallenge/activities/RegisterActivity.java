package com.nordhub.aguiachallenge.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nordhub.aguiachallenge.R;
import com.nordhub.aguiachallenge.models.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNome, etEmail, etSenha;
    private Button btnCadastrar;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etNome = findViewById(R.id.etNome);
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);

        btnCadastrar = findViewById(R.id.btnCadastrar);



        btnCadastrar.setOnClickListener(v -> cadastrar());
    }

    private void cadastrar() {

        String nome = etNome.getText().toString();
        String email = etEmail.getText().toString();
        String senha = etSenha.getText().toString();
        String tipo = "operador";

        if (nome.trim().isEmpty() || email.trim().isEmpty() || senha.trim().length() < 6) {
            Toast.makeText(this, "Preencha os dados. A senha deve ter 6 ou mais caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener(authResult -> {

                    if (auth.getCurrentUser() == null) {
                        Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String uid = auth.getCurrentUser().getUid();

                    User user = new User(uid, nome, email, tipo);

                    db.collection("users")
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(this,
                                        "Usuário cadastrado",
                                        Toast.LENGTH_SHORT).show();

                                finish();
                            });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this,
                            "Erro ao cadastrar: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}

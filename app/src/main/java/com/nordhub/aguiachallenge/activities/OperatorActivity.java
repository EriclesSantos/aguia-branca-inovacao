package com.nordhub.aguiachallenge.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nordhub.aguiachallenge.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OperatorActivity extends AppCompatActivity {

    private EditText etTituloIdeia, etDescricaoIdeia;
    private Button btnSalvarIdeia;
    private ListView listIdeas, listStrategies;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private final ArrayList<String> ideias = new ArrayList<>();
    private final ArrayList<String> estrategias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        etTituloIdeia = findViewById(R.id.etTituloIdeia);
        etDescricaoIdeia = findViewById(R.id.etDescricaoIdeia);
        btnSalvarIdeia = findViewById(R.id.btnSalvarIdeia);
        listIdeas = findViewById(R.id.listIdeas);
        listStrategies = findViewById(R.id.listStrategies);

        btnSalvarIdeia.setOnClickListener(v -> salvarIdeia());

        carregarEstrategias();
        carregarIdeias();
    }

    private void salvarIdeia() {
        String titulo = etTituloIdeia.getText().toString().trim();
        String descricao = etDescricaoIdeia.getText().toString().trim();

        if (titulo.isEmpty() || descricao.isEmpty()) {
            Toast.makeText(this, "Informe título e descrição da ideia", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Faça login novamente", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> idea = new HashMap<>();
        idea.put("titulo", titulo);
        idea.put("descricao", descricao);
        idea.put("status", "Pendente");
        idea.put("prioridade", "Normal");
        idea.put("usuarioId", auth.getCurrentUser().getUid());

        db.collection("ideas")
                .add(idea)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Ideia salva", Toast.LENGTH_SHORT).show();
                    etTituloIdeia.setText("");
                    etDescricaoIdeia.setText("");
                    carregarIdeias();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao salvar ideia: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void carregarIdeias() {
        if (auth.getCurrentUser() == null) {
            return;
        }

        db.collection("ideas")
                .whereEqualTo("usuarioId", auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ideias.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String titulo = value(doc.getString("titulo"));
                        String status = value(doc.getString("status"));
                        String prioridade = value(doc.getString("prioridade"));
                        ideias.add(titulo + " | " + status + " | Prioridade: " + prioridade);
                    }
                    listIdeas.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ideias));
                });
    }

    private void carregarEstrategias() {
        db.collection("strategies")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    estrategias.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        estrategias.add(value(doc.getString("titulo")) + "\n" + value(doc.getString("descricao")));
                    }
                    listStrategies.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, estrategias));
                });
    }

    private String value(String text) {
        return text == null || text.trim().isEmpty() ? "Sem informação" : text;
    }
}

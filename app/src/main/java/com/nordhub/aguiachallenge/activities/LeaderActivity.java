package com.nordhub.aguiachallenge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nordhub.aguiachallenge.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeaderActivity extends AppCompatActivity {

    private TextView tvProjetos, tvInvestimento, tvRetorno;
    private EditText etTituloEstrategia, etDescricaoEstrategia;
    private Button btnSalvarEstrategia, btnExcluirEstrategia, btnDashboard;
    private ListView listProjects, listStrategiesLeader;
    private FirebaseFirestore db;
    private final ArrayList<String> projetos = new ArrayList<>();
    private final ArrayList<String> estrategias = new ArrayList<>();
    private final ArrayList<String> strategyIds = new ArrayList<>();
    private int selectedStrategyIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader);

        db = FirebaseFirestore.getInstance();
        tvProjetos = findViewById(R.id.tvProjetos);
        tvInvestimento = findViewById(R.id.tvInvestimento);
        tvRetorno = findViewById(R.id.tvRetorno);
        etTituloEstrategia = findViewById(R.id.etTituloEstrategia);
        etDescricaoEstrategia = findViewById(R.id.etDescricaoEstrategia);
        btnSalvarEstrategia = findViewById(R.id.btnSalvarEstrategia);
        btnExcluirEstrategia = findViewById(R.id.btnExcluirEstrategia);
        btnDashboard = findViewById(R.id.btnDashboard);
        listProjects = findViewById(R.id.listProjects);
        listStrategiesLeader = findViewById(R.id.listStrategiesLeader);

        listStrategiesLeader.setOnItemClickListener((parent, view, position, id) -> {
            selectedStrategyIndex = position;
            String[] parts = estrategias.get(position).split("\n", 2);
            etTituloEstrategia.setText(parts[0]);
            etDescricaoEstrategia.setText(parts.length > 1 ? parts[1] : "");
        });
        btnSalvarEstrategia.setOnClickListener(v -> salvarEstrategia());
        btnExcluirEstrategia.setOnClickListener(v -> excluirEstrategia());
        btnDashboard.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));

        carregarProjetos();
        carregarEstrategias();
    }

    private void salvarEstrategia() {
        String titulo = etTituloEstrategia.getText().toString().trim();
        String descricao = etDescricaoEstrategia.getText().toString().trim();

        if (titulo.isEmpty() || descricao.isEmpty()) {
            Toast.makeText(this, "Informe título e descrição da estratégia", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> strategy = new HashMap<>();
        strategy.put("titulo", titulo);
        strategy.put("descricao", descricao);

        if (selectedStrategyIndex >= 0 && selectedStrategyIndex < strategyIds.size()) {
            db.collection("strategies")
                    .document(strategyIds.get(selectedStrategyIndex))
                    .set(strategy)
                    .addOnSuccessListener(unused -> estrategiaSalva())
                    .addOnFailureListener(e -> Toast.makeText(this, "Erro ao atualizar estratégia: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            db.collection("strategies")
                    .add(strategy)
                    .addOnSuccessListener(documentReference -> estrategiaSalva())
                    .addOnFailureListener(e -> Toast.makeText(this, "Erro ao salvar estratégia: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void estrategiaSalva() {
        Toast.makeText(this, "Estratégia salva", Toast.LENGTH_SHORT).show();
        selectedStrategyIndex = -1;
        etTituloEstrategia.setText("");
        etDescricaoEstrategia.setText("");
        carregarEstrategias();
    }

    private void excluirEstrategia() {
        if (selectedStrategyIndex < 0 || selectedStrategyIndex >= strategyIds.size()) {
            Toast.makeText(this, "Selecione uma estratégia", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("strategies")
                .document(strategyIds.get(selectedStrategyIndex))
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Estratégia excluída", Toast.LENGTH_SHORT).show();
                    selectedStrategyIndex = -1;
                    etTituloEstrategia.setText("");
                    etDescricaoEstrategia.setText("");
                    carregarEstrategias();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao excluir estratégia: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void carregarEstrategias() {
        db.collection("strategies")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    estrategias.clear();
                    strategyIds.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        estrategias.add(value(doc.getString("titulo")) + "\n" + value(doc.getString("descricao")));
                        strategyIds.add(doc.getId());
                    }
                    listStrategiesLeader.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, estrategias));
                });
    }

    private void carregarProjetos() {
        db.collection("projects")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    projetos.clear();
                    int totalProjetos = 0;
                    double totalInvestimento = 0;
                    double totalRetorno = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        totalProjetos++;
                        totalInvestimento += number(doc.getString("investimento"));
                        totalRetorno += number(doc.getString("retorno"));
                        projetos.add(value(doc.getString("nome")) + " | " + value(doc.getString("status")) + " | " + value(doc.getString("progresso")) + "%");
                    }

                    tvProjetos.setText("Projetos: " + totalProjetos);
                    tvInvestimento.setText("Investimento: R$ " + String.format("%.2f", totalInvestimento));
                    tvRetorno.setText("Retorno: R$ " + String.format("%.2f", totalRetorno));
                    listProjects.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, projetos));
                });
    }

    private double number(String value) {
        if (value == null) {
            return 0;
        }
        try {
            return Double.parseDouble(value.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String value(String text) {
        return text == null || text.trim().isEmpty() ? "Sem informação" : text;
    }
}

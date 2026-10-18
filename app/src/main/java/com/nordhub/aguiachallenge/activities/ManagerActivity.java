package com.nordhub.aguiachallenge.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nordhub.aguiachallenge.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManagerActivity extends AppCompatActivity {

    private EditText etNomeProjeto, etInvestimento, etPrazo, etRetorno, etProgresso;
    private Button btnAprovar, btnPriorizar, btnSalvarProjeto, btnAtualizarProgresso;
    private ListView listIdeasManager, listProjectsManager;
    private FirebaseFirestore db;
    private final ArrayList<String> ideias = new ArrayList<>();
    private final ArrayList<String> ideaIds = new ArrayList<>();
    private final ArrayList<String> projetos = new ArrayList<>();
    private final ArrayList<String> projectIds = new ArrayList<>();
    private int selectedIdeaIndex = -1;
    private int selectedProjectIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        db = FirebaseFirestore.getInstance();
        etNomeProjeto = findViewById(R.id.etNomeProjeto);
        etInvestimento = findViewById(R.id.etInvestimento);
        etPrazo = findViewById(R.id.etPrazo);
        etRetorno = findViewById(R.id.etRetorno);
        etProgresso = findViewById(R.id.etProgresso);
        btnAprovar = findViewById(R.id.btnAprovar);
        btnPriorizar = findViewById(R.id.btnPriorizar);
        btnSalvarProjeto = findViewById(R.id.btnSalvarProjeto);
        btnAtualizarProgresso = findViewById(R.id.btnAtualizarProgresso);
        listIdeasManager = findViewById(R.id.listIdeasManager);
        listProjectsManager = findViewById(R.id.listProjectsManager);

        listIdeasManager.setOnItemClickListener((parent, view, position, id) -> selectedIdeaIndex = position);
        listProjectsManager.setOnItemClickListener((parent, view, position, id) -> selectedProjectIndex = position);
        btnAprovar.setOnClickListener(v -> atualizarIdeia("Aprovada", null));
        btnPriorizar.setOnClickListener(v -> atualizarIdeia("Aprovada", "Alta"));
        btnSalvarProjeto.setOnClickListener(v -> salvarProjeto());
        btnAtualizarProgresso.setOnClickListener(v -> atualizarProgresso());

        carregarIdeias();
        carregarProjetos();
    }

    private void carregarIdeias() {
        db.collection("ideas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ideias.clear();
                    ideaIds.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ideias.add(value(doc.getString("titulo")) + " | " + value(doc.getString("status")) + " | Prioridade: " + value(doc.getString("prioridade")));
                        ideaIds.add(doc.getId());
                    }
                    listIdeasManager.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ideias));
                    selectedIdeaIndex = ideias.isEmpty() ? -1 : 0;
                });
    }

    private void atualizarIdeia(String status, String prioridade) {
        if (selectedIdeaIndex < 0 || selectedIdeaIndex >= ideaIds.size()) {
            Toast.makeText(this, "Selecione uma ideia", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        if (prioridade != null) {
            updates.put("prioridade", prioridade);
        }

        db.collection("ideas")
                .document(ideaIds.get(selectedIdeaIndex))
                .update(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Ideia atualizada", Toast.LENGTH_SHORT).show();
                    carregarIdeias();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao atualizar ideia: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void salvarProjeto() {
        String nome = etNomeProjeto.getText().toString().trim();
        String investimento = etInvestimento.getText().toString().trim();
        String prazo = etPrazo.getText().toString().trim();
        String retorno = etRetorno.getText().toString().trim();
        String progresso = etProgresso.getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(this, "Informe o nome do projeto", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> project = new HashMap<>();
        project.put("nome", nome);
        project.put("status", "Em andamento");
        project.put("investimento", investimento.isEmpty() ? "0" : investimento);
        project.put("prazo", prazo);
        project.put("retorno", retorno.isEmpty() ? "0" : retorno);
        project.put("progresso", progresso.isEmpty() ? "0" : progresso);

        db.collection("projects")
                .add(project)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Projeto salvo", Toast.LENGTH_SHORT).show();
                    etNomeProjeto.setText("");
                    etInvestimento.setText("");
                    etPrazo.setText("");
                    etRetorno.setText("");
                    etProgresso.setText("");
                    carregarProjetos();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao salvar projeto: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void carregarProjetos() {
        db.collection("projects")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    projetos.clear();
                    projectIds.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        projetos.add(value(doc.getString("nome")) + " | " + value(doc.getString("status")) + " | " + value(doc.getString("progresso")) + "%");
                        projectIds.add(doc.getId());
                    }
                    listProjectsManager.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, projetos));
                    selectedProjectIndex = projetos.isEmpty() ? -1 : 0;
                });
    }

    private void atualizarProgresso() {
        if (selectedProjectIndex < 0 || selectedProjectIndex >= projectIds.size()) {
            Toast.makeText(this, "Selecione um projeto", Toast.LENGTH_SHORT).show();
            return;
        }

        String progresso = etProgresso.getText().toString().trim();
        if (progresso.isEmpty()) {
            Toast.makeText(this, "Informe o progresso", Toast.LENGTH_SHORT).show();
            return;
        }

        String status = "100".equals(progresso) ? "Concluído" : "Em andamento";
        db.collection("projects")
                .document(projectIds.get(selectedProjectIndex))
                .update("progresso", progresso, "status", status)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Progresso atualizado", Toast.LENGTH_SHORT).show();
                    carregarProjetos();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao atualizar projeto: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private String value(String text) {
        return text == null || text.trim().isEmpty() ? "Sem informação" : text;
    }
}

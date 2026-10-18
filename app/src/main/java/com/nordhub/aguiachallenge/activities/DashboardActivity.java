package com.nordhub.aguiachallenge.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nordhub.aguiachallenge.R;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvDashboardRoi, tvDashboardProjetosAtivos, tvDashboardProdutividade, tvDashboardEconomia, tvDashboardIdeias;
    private FirebaseFirestore db;
    private int totalIdeias = 0;
    private int ideiasAprovadas = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        db = FirebaseFirestore.getInstance();
        tvDashboardRoi = findViewById(R.id.tvDashboardRoi);
        tvDashboardProjetosAtivos = findViewById(R.id.tvDashboardProjetosAtivos);
        tvDashboardProdutividade = findViewById(R.id.tvDashboardProdutividade);
        tvDashboardEconomia = findViewById(R.id.tvDashboardEconomia);
        tvDashboardIdeias = findViewById(R.id.tvDashboardIdeias);

        carregarIdeias();
        carregarProjetos();
    }

    private void carregarIdeias() {
        db.collection("ideas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    totalIdeias = 0;
                    ideiasAprovadas = 0;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        totalIdeias++;
                        if ("Aprovada".equalsIgnoreCase(doc.getString("status"))) {
                            ideiasAprovadas++;
                        }
                    }
                    atualizarIdeias();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao carregar ideias: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void carregarProjetos() {
        db.collection("projects")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int ativos = 0;
                    double investimento = 0;
                    double retorno = 0;
                    double progressoTotal = 0;
                    int projetos = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        projetos++;
                        String status = doc.getString("status");
                        if (status == null || !"Concluído".equalsIgnoreCase(status)) {
                            ativos++;
                        }
                        investimento += number(doc.getString("investimento"));
                        retorno += number(doc.getString("retorno"));
                        progressoTotal += number(doc.getString("progresso"));
                    }

                    double roi = investimento == 0 ? 0 : ((retorno - investimento) / investimento) * 100;
                    double produtividade = projetos == 0 ? 0 : progressoTotal / projetos;
                    double economia = Math.max(0, retorno - investimento);

                    tvDashboardRoi.setText("ROI: " + String.format("%.1f", roi) + "%");
                    tvDashboardProjetosAtivos.setText("Projetos ativos: " + ativos);
                    tvDashboardProdutividade.setText("Produtividade média: " + String.format("%.1f", produtividade) + "%");
                    tvDashboardEconomia.setText("Economia estimada: R$ " + String.format("%.2f", economia));
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao carregar projetos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void atualizarIdeias() {
        tvDashboardIdeias.setText("Ideias: " + totalIdeias + " cadastradas | " + ideiasAprovadas + " aprovadas");
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
}

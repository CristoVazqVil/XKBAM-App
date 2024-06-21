package com.example.xkbam.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.xkbam.R;
import com.example.xkbam.utilidades.ClienteGrpc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReporteVentasActivity extends AppCompatActivity {

    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Button generateReportButton;
    private ClienteGrpc clienteGrpc;
    private static final String TAG = "ReporteVentasActivity";
    private static final int REQUEST_CODE_PICK_DIR = 1;
    private Uri directoryUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_ventas);

        clienteGrpc = new ClienteGrpc("10.0.2.2:8080");

        startDatePicker = findViewById(R.id.start_date);
        endDatePicker = findViewById(R.id.end_date);
        generateReportButton = findViewById(R.id.generate_report_button);

        generateReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDate = getDateFromDatePicker(startDatePicker);
                String endDate = getDateFromDatePicker(endDatePicker);
                Log.d(TAG, "Start Date: " + startDate + " End Date: " + endDate);
                chooseDirectory();
            }
        });


    }

    private String getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(calendar.getTime());
    }

    private void chooseDirectory() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_PICK_DIR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_DIR && resultCode == RESULT_OK) {
            if (data != null) {
                directoryUri = data.getData();
                Log.d(TAG, "Directory selected: " + directoryUri.toString());
                saveReportToSelectedDirectory(directoryUri);
            }
        }
    }

    private void saveReportToSelectedDirectory(Uri directoryUri) {
        String startDate = getDateFromDatePicker(startDatePicker);
        String endDate = getDateFromDatePicker(endDatePicker);

        String fileName = "Reporte_Ventas_" + startDate + "_" + endDate + ".txt";

        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, directoryUri);
        DocumentFile file = pickedDir.createFile("text/plain", fileName);

        try {
            if (file != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(file.getUri());
                if (outputStream != null) {
                    String reportContent = generateReport(startDate, endDate);
                    outputStream.write(reportContent.getBytes());
                    outputStream.close();
                    Toast.makeText(this, "Reporte guardado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No se pudo abrir el archivo para escribir", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No se pudo crear el archivo en la ubicación seleccionada", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar el reporte", Toast.LENGTH_SHORT).show();
        }
    }
    private String generateReport(String startDate, String endDate) {
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("Reporte de ventas desde ")
                .append(startDate)
                .append(" hasta ")
                .append(endDate)
                .append("\n\n");

        reportBuilder.append("Total ventas: $5000\n");
        reportBuilder.append("Total clientes nuevos: 20\n");
        reportBuilder.append("Productos más vendidos: \n");
        reportBuilder.append("- Camisas: 50 unidades\n");
        reportBuilder.append("- Pantalones: 30 unidades\n");

        return reportBuilder.toString();
    }


}
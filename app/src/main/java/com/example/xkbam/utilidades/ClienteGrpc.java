package com.example.xkbam.utilidades;

import android.content.Context;
import android.util.Log;

import com.proto.xkbamproject.MultimediaServiceGrpc;
import com.proto.xkbamproject.MultimediaServiceGrpc.MultimediaServiceBlockingStub;
import com.proto.xkbamproject.MultimediaServiceGrpc.MultimediaServiceStub;
import com.proto.xkbamproject.Multimedia.UploadMultimediaRequest;
import com.proto.xkbamproject.Multimedia.UploadMultimediaResponse;
import com.proto.xkbamproject.Multimedia.GenerateReportRequest;
import com.proto.xkbamproject.Multimedia.GenerateReportResponse;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class ClienteGrpc {

    private static final String TAG = "GrpcClient";
    private final MultimediaServiceStub asyncStub;
    private final MultimediaServiceBlockingStub blockingStub;

    public ClienteGrpc(String address) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(address)
                .usePlaintext()
                .build();
        asyncStub = MultimediaServiceGrpc.newStub(channel);
        blockingStub = MultimediaServiceGrpc.newBlockingStub(channel);
    }

    public void uploadMultimediaAsync(String itemId, String nombre, String imagePath) {
        StreamObserver<UploadMultimediaRequest> requestObserver = asyncStub.uploadMultimedia(new StreamObserver<UploadMultimediaResponse>() {
            @Override
            public void onNext(UploadMultimediaResponse value) {
                Log.d(TAG, "Upload completed: " + value.getResponse());
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "gRPC error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                Log.d(TAG, "Upload completed");
            }
        });

        try {
            requestObserver.onNext(UploadMultimediaRequest.newBuilder().setItem(itemId).build());
            Log.d(TAG, "Sent item ID: " + itemId);

            requestObserver.onNext(UploadMultimediaRequest.newBuilder().setNombre(nombre).build());
            Log.d(TAG, "Sent file name: " + nombre);

            byte[] buffer = new byte[1024];
            try (FileInputStream fs = new FileInputStream(imagePath)) {
                int bytesRead;
                while ((bytesRead = fs.read(buffer)) > 0) {
                    requestObserver.onNext(UploadMultimediaRequest.newBuilder().setData(ByteString.copyFrom(buffer, 0, bytesRead)).build());
                    Log.d(TAG, "Uploading data chunk...");
                }
            }

            requestObserver.onCompleted();
        } catch (IOException e) {
            Log.e(TAG, "File I/O error: " + e.getMessage());
            requestObserver.onError(e);
        }
    }

    public void generateReportAsync(String startDate, String endDate, String savePath, Context context) {
        GenerateReportRequest request = GenerateReportRequest.newBuilder()
                .setStartDate(startDate)
                .setEndDate(endDate)
                .build();

        asyncStub.generateReport(request, new StreamObserver<GenerateReportResponse>() {
            @Override
            public void onNext(GenerateReportResponse value) {
                String fileName = value.getName();
                fileName = sanitizeFileName(fileName);

                File directory = new File(context.getFilesDir(), savePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                File reportFile = new File(directory, fileName);
                try {
                    if (!reportFile.exists()) {
                        reportFile.createNewFile();
                    }
                    byte[] data = value.getData().toByteArray();
                    java.nio.file.Files.write(reportFile.toPath(), data);
                    Log.d(TAG, "Report generated and saved to: " + reportFile.getAbsolutePath());
                } catch (IOException e) {
                    Log.e(TAG, "File I/O error: " + e.getMessage());
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "gRPC error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                Log.d(TAG, "Report generation completed");
            }
        });
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }
}



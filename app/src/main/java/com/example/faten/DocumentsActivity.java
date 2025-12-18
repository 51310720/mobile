package com.example.faten;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;

public class DocumentsActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 101;
    private static final int GALLERY_REQUEST = 102;
    private static final int PERMISSION_REQUEST = 100;

    private String currentCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbarDocuments);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Boutons pour Ordonnances
        MaterialButton btnVoirOrdonnance = findViewById(R.id.btnVoirOrdonnance);
        MaterialButton btnAjouterOrdonnance = findViewById(R.id.btnAjouterOrdonnance);

        // Boutons pour Résultats
        MaterialButton btnVoirResultat = findViewById(R.id.btnVoirResultat);
        MaterialButton btnAjouterResultat = findViewById(R.id.btnAjouterResultat);

        // 👁️ VOIR ORDONNANCE
        btnVoirOrdonnance.setOnClickListener(v -> {
            voirDocument("ordonnance", "Ordonnance Dentiste");
        });

        // ➕ AJOUTER ORDONNANCE
        btnAjouterOrdonnance.setOnClickListener(v -> {
            currentCategory = "ordonnance";
            showImageSourceDialog();
        });

        // 👁️ VOIR RÉSULTAT
        btnVoirResultat.setOnClickListener(v -> {
            voirDocument("resultat", "Résultat analyse sanguine");
        });

        // ➕ AJOUTER RÉSULTAT
        btnAjouterResultat.setOnClickListener(v -> {
            currentCategory = "resultat";
            showImageSourceDialog();
        });
    }

    // 📱 Voir le document
    private void voirDocument(String category, String title) {
        // Chercher le fichier dans le dossier
        File directory = new File(getFilesDir(), "medical_docs");
        File[] files = directory.listFiles((dir, name) -> name.startsWith(category));

        if (files != null && files.length > 0) {
            // Prendre le fichier le plus récent
            File latestFile = files[0];
            for (File file : files) {
                if (file.lastModified() > latestFile.lastModified()) {
                    latestFile = file;
                }
            }

            Intent intent = new Intent(this, ViewImageActivity.class);
            intent.putExtra("IMAGE_PATH", latestFile.getAbsolutePath());
            intent.putExtra("TITLE", title);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Aucun document disponible", Toast.LENGTH_SHORT).show();
        }
    }

    // Dialog pour choisir caméra ou galerie
    private void showImageSourceDialog() {
        String[] options = {"📷 Prendre une photo", "🖼️ Galerie"};

        new AlertDialog.Builder(this)
                .setTitle("Source de l'image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkPermissionAndOpenCamera();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = null;

                if (requestCode == CAMERA_REQUEST) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                } else if (requestCode == GALLERY_REQUEST) {
                    Uri imageUri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                }

                if (bitmap != null) {
                    saveImage(bitmap);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImage(Bitmap bitmap) {
        try {
            // Nom du fichier avec timestamp
            String fileName = currentCategory + "_" + System.currentTimeMillis() + ".jpg";

            // Créer le dossier
            File directory = new File(getFilesDir(), "medical_docs");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Sauvegarder l'image
            File file = new File(directory, fileName);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();

            Toast.makeText(this, "✅ Document ajouté!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "❌ Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
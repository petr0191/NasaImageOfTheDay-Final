package com.example.nasaimageoftheday;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ListView listViewImages;
    private ArrayList<NasaImage> imageList;
    private NasaImageAdapter adapter;
    private NasaImageDbHelper dbHelper;
    private TextView txtSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle("NASA Image of the day");
        String versionName = "v1";
        getSupportActionBar().setSubtitle("Version " + versionName);

        progressBar = findViewById(R.id.progressBar);
        listViewImages = findViewById(R.id.listViewImages);
        txtSelectedDate = findViewById(R.id.txtSelectedDate);

        dbHelper = new NasaImageDbHelper(this);
        imageList = new ArrayList<>();
        adapter = new NasaImageAdapter(this, imageList);
        listViewImages.setAdapter(adapter);

        loadSavedImages();

        listViewImages.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected image from the ListView
            NasaImage selectedImage = (NasaImage) parent.getItemAtPosition(position);

            // Create an intent to open the ImageDetailsActivity
            Intent intent = new Intent(MainActivity.this, ImageDetailsActivity.class);
            intent.putExtra("IMAGE_URL", selectedImage.getUrl());
            intent.putExtra("HD_IMAGE_URL", selectedImage.getHdurl());
            intent.putExtra("IMAGE_DATE", selectedImage.getDate());
            startActivity(intent);
        });

    }
    private void loadSavedImages() {
        imageList.clear();
        imageList.addAll(dbHelper.getAllImages());
        adapter.notifyDataSetChanged();
    }

    private void insertImage(NasaImage nasaImage) {
        long newRowId = dbHelper.insertImage(nasaImage);
        if (newRowId != -1) {
            nasaImage.setId((int) newRowId);
            imageList.add(nasaImage);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Failed to save the image.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteImage(NasaImage nasaImage) {
        int deletedRows = dbHelper.deleteImage(nasaImage.getId());
        if (deletedRows > 0) {
            imageList.remove(nasaImage);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Failed to delete the image.", Toast.LENGTH_SHORT).show();
        }
    }

    public void invalidDate() {
        Toast.makeText(this, "Please select a date.", Toast.LENGTH_SHORT).show();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void setSelectedDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = sdf.format(calendar.getTime());
        txtSelectedDate.setText(selectedDate);
    }

    public void fetchImageForDate(String selectedDate) {
        progressBar.setVisibility(View.VISIBLE);

        NasaImageTask task = new NasaImageTask(progressBar, listViewImages, this);
        task.execute(selectedDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help");
        builder.setMessage("Select the day to view NASA's image for the selected day...");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
package com.example.nasaimageoftheday;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDetailsActivity extends AppCompatActivity {

    private String imageUrl;
    private String hdImageUrl;
    private String imageDate;

    private NasaImageDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        dbHelper = new NasaImageDbHelper(this);

        imageUrl = getIntent().getStringExtra("IMAGE_URL");
        hdImageUrl = getIntent().getStringExtra("HD_IMAGE_URL");
        imageDate = getIntent().getStringExtra("IMAGE_DATE");

        TextView txtImageDate = findViewById(R.id.txtImageDate);
        ImageView imageView = findViewById(R.id.imageView);
        Button btnOpenHD = findViewById(R.id.btnOpenHD);

        txtImageDate.setText(imageDate);

        NasaImage nasaImage = dbHelper.getImageByDate(imageDate);
        if (nasaImage != null) {
            imageView.setImageBitmap(nasaImage.getBitmap());
        } else{
            new LoadImageTask(imageView, imageUrl, hdImageUrl, imageDate).execute(imageUrl);
        }

        btnOpenHD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(hdImageUrl));
                startActivity(browserIntent);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;
        private String imageUrl;
        private String hdImageUrl;
        private String imageDate;

        public LoadImageTask(ImageView imageView, String imageUrl, String hdImageUrl, String imageDate) {
            this.imageView = imageView;
            this.imageUrl = imageUrl;
            this.hdImageUrl = hdImageUrl;
            this.imageDate = imageDate;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(imageUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
                NasaImage nasaImage = new NasaImage();
                nasaImage.setDate(imageDate);
                nasaImage.setUrl(imageUrl);
                nasaImage.setHdurl(hdImageUrl);
                nasaImage.setBitmap(result);
                saveImageToDatabase(nasaImage);
            }
        }
    }

    private void saveImageToDatabase(NasaImage nasaImage) {
        dbHelper.insertImage(nasaImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help");
        builder.setMessage("Click Open in HD to open the image in your browser in full resolution.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}

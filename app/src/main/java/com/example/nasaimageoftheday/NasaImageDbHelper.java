package com.example.nasaimageoftheday;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NasaImageDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "nasa_image.db";
    private static final int DATABASE_VERSION = 2;

    // Table name and column names
    public static final String TABLE_NAME = "nasa_images";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_HDURL = "hdurl";
    private static final String COLUMN_IMAGE = "image";

    public NasaImageDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_URL + " TEXT, " +
                COLUMN_HDURL + " TEXT," +
                COLUMN_IMAGE + " BLOB);";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertImage(NasaImage nasaImage) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, nasaImage.getDate());
        values.put(COLUMN_URL, nasaImage.getUrl());
        values.put(COLUMN_HDURL, nasaImage.getHdurl());
        byte[] imageData = getByteArrayFromBitmap(nasaImage.getBitmap());
        values.put(COLUMN_IMAGE, imageData);

        return db.insert(TABLE_NAME, null, values);
    }

    public int deleteImage(int id) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};

        return db.delete(TABLE_NAME, selection, selectionArgs);
    }

    public List<NasaImage> getAllImages() {
        List<NasaImage> imageList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COLUMN_ID,
                COLUMN_DATE,
                COLUMN_URL,
                COLUMN_HDURL,
                COLUMN_IMAGE
        };

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL));
                String hdurl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HDURL));

                NasaImage nasaImage = new NasaImage();
                nasaImage.setId(id);
                nasaImage.setDate(date);
                nasaImage.setUrl(url);
                nasaImage.setHdurl(hdurl);
                byte[] imageData = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
                Bitmap imageBitmap = getBitmapFromByteArray(imageData);
                nasaImage.setBitmap(imageBitmap);
                imageList.add(nasaImage);
            }
            cursor.close();
        }

        return imageList;
    }

    public NasaImage getImageByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        NasaImage nasaImage = null;

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID, COLUMN_DATE, COLUMN_URL, COLUMN_HDURL, COLUMN_IMAGE},
                COLUMN_DATE + "=?", new String[]{date}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            nasaImage = new NasaImage();
            nasaImage.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            nasaImage.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
            nasaImage.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL)));
            String hdurl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HDURL));
            nasaImage.setHdurl(hdurl);

            // Convert byte array back to Bitmap image
            byte[] imageData = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
            Bitmap imageBitmap = getBitmapFromByteArray(imageData);
            nasaImage.setBitmap(imageBitmap);

            cursor.close();
        }
        db.close();
        return nasaImage;
    }


    private byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap getBitmapFromByteArray(byte[] imageData) {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }
}

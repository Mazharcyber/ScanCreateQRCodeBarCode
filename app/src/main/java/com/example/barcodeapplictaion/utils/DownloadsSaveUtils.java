package com.example.barcodeapplictaion.utils;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.airbnb.lottie.animation.content.Content;
import com.example.barcodeapplictaion.BuildConfig;
import com.example.barcodeapplictaion.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;

public class DownloadsSaveUtils {

    public static Uri saveBitmapToImageAndGetURI(Bitmap finalBitmap, Context context) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/DCIM/" + context.getResources().getString(R.string.app_name));
        myDir.mkdirs();
        Random generator = new Random();
        Uri photoUri;
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".png";
        File file = new File(myDir, fname);
//        if (file.exists()) file.delete(); FileUriExposedException exposed beyond app through ClipData.Item.getUri
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException ignored){} catch (Exception e) {
            e.printStackTrace();
        }

        photoUri = Uri.fromFile(file);
        String stringPath = String.valueOf(photoUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            photoUri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider", file);
        }

        //shareImageIntent(photoUri);
        return photoUri;
    }

    public static Uri saveBitmapToCacheAndGetURI(Bitmap finalBitmap, Context context) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(context.getCacheDir() + "/shared/");
        myDir.mkdirs();
        Random generator = new Random();
        Uri photoUri;
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".png";
        File file = new File(myDir, fname);
//        if (file.exists()) file.delete(); FileUriExposedException exposed beyond app through ClipData.Item.getUri
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException ignored){} catch (Exception e) {
            e.printStackTrace();
        }

        photoUri = Uri.fromFile(file);
        String stringPath = String.valueOf(photoUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            photoUri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider", file);
        }

        //shareImageIntent(photoUri);
        return photoUri;
    }

    public static void shareImageIntent(Context context, Uri bitmapUri) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("palin/text");
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        context.startActivity(Intent.createChooser(share,"Share via"));
    }

    public static void openLinkInBrowser(Context context, String url) {
        if (!url.startsWith("http://") || !url.startsWith("http://")) {
            url = "https://" + url;
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

}

package com.hunterdavis.easysymmetrycheck;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class EasySymmetryCheck extends Activity{

	int SELECT_PICTURE = 22;

	Uri selectedImageUri = null;
	// this will take a while, set up a progressbar dialog
	ProgressDialog pbarDialog;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) this.findViewById(R.id.adView);
		adView.loadAd(new AdRequest());

		// Create an anonymous implementation of OnClickListener
		OnClickListener loadButtonListner = new OnClickListener() {
			public void onClick(View v) {
				// do something when the button is clicked

				// in onCreate or any event where your want the user to
				// select a file
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Source Photo"),
						SELECT_PICTURE);
			}
		};

		OnClickListener leftButtonListner = new OnClickListener() {
			public void onClick(View v) {
				if (selectedImageUri != null) {
					// do something when the button is clicked
					flipImageLeft(v.getContext());
				}
			}
		};

		OnClickListener rightButtonListner = new OnClickListener() {
			public void onClick(View v) {
				if (selectedImageUri != null) {
					// do something when the button is clicked
					flipImageRight(v.getContext());
				}
			}
		};

		Button loadButton = (Button) findViewById(R.id.loadButton);
		loadButton.setOnClickListener(loadButtonListner);

		Button leftButton = (Button) findViewById(R.id.leftButton);
		leftButton.setOnClickListener(leftButtonListner);

		Button rightButton = (Button) findViewById(R.id.rightButton);
		rightButton.setOnClickListener(rightButtonListner);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				selectedImageUri = data.getData();
				ImageView imgView = (ImageView) findViewById(R.id.ImageView01);
				Boolean scaleDisplay = scaleURIAndDisplay(getBaseContext(),
						selectedImageUri, imgView);
			}
		}
	}

	public boolean flipImageLeft(Context context) {
		return flipImage(context, true);
	}

	public boolean flipImageRight(Context context) {
		return flipImage(context, false);
	}

	// flip image actually does the pixel processing work
	public boolean flipImage(Context context, Boolean flipLeft) {

		// from A to Z we flip, middle flip outward
		ImageView imgView = (ImageView) findViewById(R.id.ImageView01);
		Bitmap scaledBitmap = scaleUriAndReturnBitmap(context,
				selectedImageUri, imgView);
		int height = scaledBitmap.getHeight();
		int width = scaledBitmap.getWidth();

		// scaledBitmap.getPixels(pixels, offset, stride, x, y, width, height)
		// scaledBitmap.setPixels(pixels, offset, stride, x, y, width, height);

		pbarDialog = new ProgressDialog(context);

		// After that, just set the Progress Style to STYLE_HORIZONTAL,

		pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		// Also enter the dialog message text to whatever you want and whether
		// it is cancelable or not.

		pbarDialog.setMessage("Loading...");

		pbarDialog.setCancelable(false);

		if (flipLeft == true) {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < (width / 2); j++) {
					int pixel = scaledBitmap.getPixel(j, i);
					scaledBitmap.setPixel(width - j - 1, i, pixel);
				}
			}
			imgView.setImageBitmap(scaledBitmap);
		} else {
			for (int i = 0; i < height; i++) {
				for (int j = (width / 2); j < width; j++) {
					int pixel = scaledBitmap.getPixel(j, i);
					scaledBitmap.setPixel(width - j, i, pixel);
				}
			}

			imgView.setImageBitmap(scaledBitmap);
		}

		return true;
	}

	private class FlipImageTask extends AsyncTask<Context, Void, Void> {
		protected void onPreExecute() {
			pbarDialog.show();
		}

		@Override
		protected Void doInBackground(Context... params) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static Bitmap scaleUriAndReturnBitmap(Context context, Uri uri,
			ImageView imgview) {

		double divisorDouble = 400;
		InputStream photoStream;
		try {
			photoStream = context.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap photoBitmap;

		photoBitmap = BitmapFactory.decodeStream(photoStream, null, options);
		if (photoBitmap == null) {
			return null;
		}
		int h = photoBitmap.getHeight();
		int w = photoBitmap.getWidth();
		if ((w > h) && (w > divisorDouble)) {
			double ratio = divisorDouble / w;
			w = (int) divisorDouble;
			h = (int) (ratio * h);
		} else if ((h > w) && (h > divisorDouble)) {
			double ratio = divisorDouble / h;
			h = (int) divisorDouble;
			w = (int) (ratio * w);
		}

		Bitmap scaled = Bitmap.createScaledBitmap(photoBitmap, w, h, true);
		photoBitmap.recycle();
		return scaled;
	}

	public static Boolean scaleURIAndDisplay(Context context, Uri uri,
			ImageView imgview) {
		double divisorDouble = 400;
		InputStream photoStream;
		try {
			photoStream = context.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap photoBitmap;

		photoBitmap = BitmapFactory.decodeStream(photoStream, null, options);
		if (photoBitmap == null) {
			return false;
		}
		int h = photoBitmap.getHeight();
		int w = photoBitmap.getWidth();
		if ((w > h) && (w > divisorDouble)) {
			double ratio = divisorDouble / w;
			w = (int) divisorDouble;
			h = (int) (ratio * h);
		} else if ((h > w) && (h > divisorDouble)) {
			double ratio = divisorDouble / h;
			h = (int) divisorDouble;
			w = (int) (ratio * w);
		}

		Bitmap scaled = Bitmap.createScaledBitmap(photoBitmap, w, h, true);
		photoBitmap.recycle();
		imgview.setImageBitmap(scaled);
		return true;
	}

}
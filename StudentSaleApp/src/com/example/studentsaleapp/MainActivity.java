package com.example.studentsaleapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.PushService;
import com.parse.SaveCallback;

import android.os.Bundle;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.telephony.TelephonyManager;

public class MainActivity extends Activity implements View.OnClickListener{
	
	public final static String PHOTO = "com.example.StudentSaleApp.PHOTO";
	public final static String OBJECT_ID = "com.example.StudentSaleApp.OBJECTID";
	public final static String ITEM_NAME = "com.example.StudentSaleApp.ITEM_NAME";
	public final static String ITEM_DESC = "com.example.StudentSaleApp.ITEM_DESC";
	
	ImageButton ib;
	ImageView iv;
	Intent i;
	final static int cameraData = 0;
	Bitmap itemPhoto;
	byte[] photoByteStream;
	WallpaperManager wallpaperManager;
	String itemName;
	String itemDescription;
	String itemID;
	String userID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "oSVz4UWfUShEgXckKkqA2G4gESIle3GL0egGqEQI","BU5O1f2A26nLwQOzTB4WEucrKGH7JU7tSyC1d7GW"); 
		setContentView(R.layout.activity_main);
		initialize();
		
		//make photo a default picture then write over byte[] later with real photo
		InputStream is = getResources().openRawResource(R.drawable.ic_launcher);
		itemPhoto = BitmapFactory.decodeStream(is);
		photoByteStream = convertBmpToBytes(itemPhoto);
	}

	private void initialize() {
		// TODO Auto-generated method stub
		iv = (ImageView) findViewById (R.id.ivReturnedPic);
		ib = (ImageButton)findViewById(R.id.ibTakePhoto);
		ib.setOnClickListener(this);
	
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibTakePhoto :
			i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(i, cameraData);
		break;
			
		}
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			itemPhoto = (Bitmap)extras.get("data");
			
			photoByteStream = convertBmpToBytes(itemPhoto);
			
			iv.setImageBitmap(itemPhoto);
		}
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void postItem(View view) {
		EditText editName = (EditText) findViewById(R.id.itemName);
		EditText editDesc = (EditText) findViewById(R.id.itemDescription);
		itemName = editName.getText().toString();
		itemDescription = editDesc.getText().toString();
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		userID = telephonyManager.getDeviceId();
		//for debug purpose ===>    Log.d("haha", "getDeviceId() " + userID);
		
		final ParseObject itemPost = new ParseObject("ItemPost");
		itemPost.put("ItemName", itemName);
		itemPost.put("ItemDescription", itemDescription);
		itemPost.put("UserID", userID);
		if (photoByteStream != null)
			itemPost.put("ItemPhoto", photoByteStream);
		itemPost.saveInBackground(new SaveCallback() {
			  public void done(ParseException e) {
				    //  Access the object id here
				  if (e != null) {
					  	itemID = e.toString();}
				  else {
					  	itemID = itemPost.getObjectId();}
				  }
				});
		


		Intent newIntent = new Intent(this, PostedActivity.class);
		newIntent.putExtra(OBJECT_ID, itemID);
		newIntent.putExtra(PHOTO, photoByteStream);
		newIntent.putExtra(ITEM_NAME, itemName);
		newIntent.putExtra(ITEM_DESC, itemDescription);
		startActivity(newIntent);
	}
	
	public byte[] convertBmpToBytes(Bitmap bitmap) {
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}
	
	

}

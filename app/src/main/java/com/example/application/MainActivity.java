package com.example.application;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.model.DialogConfigs;
import android.os.Environment;
import java.io.File;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.os.Looper;
import java.security.GeneralSecurityException;
import com.example.application.utils.ApkSignatureHandler;
import com.android.apksig.apk.ApkFormatException;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Button;
import java.util.List;
import java.util.ArrayList;
import android.view.View.OnClickListener;
import android.view.View;
import java.util.Arrays;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import java.io.IOException;
import androidx.core.content.ContextCompat;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import com.example.application.utils.KeyLoader;
import android.view.LayoutInflater;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
	private EditText editText;
	private ImageView imageView;
    private Spinner spinner;
	private Spinner spinner2;
	private Button button,button2;

	List<String> assetList;
	List<String> fileNameList = new ArrayList<>();
	
	
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		checkPermissions();
		editText =  findViewById(R.id.activitymainEditText1);
		imageView  = findViewById(R.id.activitymainImageView1);
		spinner = findViewById(R.id.activitymainSpinner1);
		spinner2 =  findViewById(R.id.activitymainSpinner2);
		button = findViewById(R.id.activitymainButton1);
		button2 = findViewById(R.id.clearButton);
		
		
        
    
	imageView.setOnClickListener(new OnClickListener() {

	@Override
	public void onClick(View view) {
		apkImportDialog();
	}
	});

	try {
		final String[] S = getAssets().list("keys/");
		assetList = new ArrayList<String>(Arrays.asList(S));

		for (String file : assetList) {
			String fileName = file.split("\\.")[0];

			if (assetList.contains(fileName + ".pk8") && assetList.contains(fileName + ".x509.pem")) {
				if (!fileNameList.contains(fileName)) {
					fileNameList.add(fileName);
				}
			}
		}
	} catch (Exception e) {toast(e.toString());} 

	ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fileNameList);
	spinner.setAdapter(adapter);
	int index = fileNameList.indexOf("Tusar");
	if (index != -1)
	spinner.setSelection(index);
	spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	@Override
	public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		KeyLoader.SIGN_KEY_NAME = fileNameList.get(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	});
	
	/// Hehe copy past 😁
	ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.sign_secheme));
	spinner2.setAdapter(adapter2);
	spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	@Override
	public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		switch (position) {
			case 0:
				ApkSignatureHandler.setV1SigningEnabled = true;
				ApkSignatureHandler.setV2SigningEnabled = true;
				ApkSignatureHandler.setV3SigningEnabled = true;
				break;
			case 1:
				ApkSignatureHandler.setV1SigningEnabled = true;
				ApkSignatureHandler.setV2SigningEnabled = true;
				ApkSignatureHandler.setV3SigningEnabled = false;
				break;
			case 2:
				ApkSignatureHandler.setV1SigningEnabled = true;
				ApkSignatureHandler.setV2SigningEnabled = false;
				ApkSignatureHandler.setV3SigningEnabled = true;
				break;
			case 3:
				ApkSignatureHandler.setV1SigningEnabled = true;
				ApkSignatureHandler.setV2SigningEnabled = false;
				ApkSignatureHandler.setV3SigningEnabled = false;
				break;
			case 4:
				ApkSignatureHandler.setV1SigningEnabled = false;
				ApkSignatureHandler.setV2SigningEnabled = true;
				ApkSignatureHandler.setV3SigningEnabled = true;
				break;
			case 5:
				ApkSignatureHandler.setV1SigningEnabled = false;
				ApkSignatureHandler.setV2SigningEnabled = true;
				ApkSignatureHandler.setV3SigningEnabled = false;
				break;
			case 6:
				ApkSignatureHandler.setV1SigningEnabled = false;
				ApkSignatureHandler.setV2SigningEnabled = false;
				ApkSignatureHandler.setV3SigningEnabled = true;
				break;
		}
	}
	//Hehe copy past ended 😁

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	});


	button.setOnClickListener(new View.OnClickListener(){
	@Override
	public void onClick(View p1) {
		if (!editText.getText().toString().isEmpty())
			StartSign();
		else
			toast("Enter file path or select apk manualy");
	}
	});


    
	
	button2.setOnClickListener(new View.OnClickListener(){
	@Override
	public void onClick(View p1) {
		if (!editText.getText().toString().isEmpty())
			editText.setText("");
		else
			toast("Enter file path or select apk manualy");
	}
	});


    }
	
    
    // Take The Permission For Android 10,11,12,13,14

	private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
	
	//file picker 
	private void apkImportDialog() {
		DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{"apk", "APK"};
        FilePickerDialog  fpdialog = new FilePickerDialog(this, properties);
        fpdialog.setProperties(properties);
        fpdialog.setTitle("Select Apk");
        fpdialog.setPositiveBtnName("Select");
        fpdialog.setNegativeBtnName("Cancel");
        fpdialog.setDialogSelectionListener(new DialogSelectionListener() {
                @Override
                public void onSelectedFilePaths(String[] files) {
                    for (int i = 0; i < files.length; ++i) {
                        File file1 = new File(files[i]);
						if (file1.getName().endsWith(".apk") || file1.getName().endsWith(".APK")) {
							editText.setText(file1.getAbsolutePath());
						} else {
							toast("Error");
						}
                    }         
                }
            });
        fpdialog.show();      
    }

	
    

	public void StartSign() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View dialogView = inflater.inflate(R.layout.dialog_progress, null);
		final AlertDialog loadingDialog = new AlertDialog.Builder(this, R.style.CustomDialog)
			.setView(dialogView)
			.setCancelable(false) 
			.create();

		loadingDialog.show();

		new Thread() {
			public void run() {
				try {
					new ApkSignatureHandler().calculateSignature(
						editText.getText().toString(),
						editText.getText().toString().replace(".apk", "_sign.apk")
					);

					runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dialogFinished();
								editText.setText("");
								loadingDialog.dismiss();
							}
						});

				} catch (final ApkFormatException | GeneralSecurityException | IOException e) {
					runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
								
							}
						});
				}

				
			}
		}.start();
	}
	

	public void dialogFinished() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View dialogView = inflater.inflate(R.layout.dialog_signed, null);
		TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
		Button dialogOkButton = dialogView.findViewById(R.id.dialogOkButton);
		String inputPath = editText.getText().toString();
		String signedPath = inputPath.replace(".apk", "_sign.apk");
		dialogMessage.setText("Path: " + signedPath);
		final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setView(dialogView);
		final AlertDialog dialog = alertDialog.create();
		dialogOkButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

		dialog.show();
	}
	public void toast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	
	
	
	
    
}

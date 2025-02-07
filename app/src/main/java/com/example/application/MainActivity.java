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
import com.example.application.utils.Signer;
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

public class MainActivity extends AppCompatActivity {
	private EditText editText;
	private ImageView imageView;
    private Spinner spinner;
	private Spinner spinner2;
	private Button button;

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
	} catch (Exception e) {print(e.toString());} 

	ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fileNameList);
	spinner.setAdapter(adapter);
	int index = fileNameList.indexOf("Tusar");
	if (index != -1)
	spinner.setSelection(index);
	spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	@Override
	public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		Signer.signKeyName = fileNameList.get(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	});
	
	ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.sign_secheme));
	spinner2.setAdapter(adapter2);
	spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	@Override
	public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		switch (position) {
			case 0:
				Signer.setV1SigningEnabled = true;
				Signer.setV2SigningEnabled = true;
				Signer.setV3SigningEnabled = true;
				break;
			case 1:
				Signer.setV1SigningEnabled = true;
				Signer.setV2SigningEnabled = true;
				Signer.setV3SigningEnabled = false;
				break;
			case 2:
				Signer.setV1SigningEnabled = true;
				Signer.setV2SigningEnabled = false;
				Signer.setV3SigningEnabled = true;
				break;
			case 3:
				Signer.setV1SigningEnabled = true;
				Signer.setV2SigningEnabled = false;
				Signer.setV3SigningEnabled = false;
				break;
			case 4:
				Signer.setV1SigningEnabled = false;
				Signer.setV2SigningEnabled = true;
				Signer.setV3SigningEnabled = true;
				break;
			case 5:
				Signer.setV1SigningEnabled = false;
				Signer.setV2SigningEnabled = true;
				Signer.setV3SigningEnabled = false;
				break;
			case 6:
				Signer.setV1SigningEnabled = false;
				Signer.setV2SigningEnabled = false;
				Signer.setV3SigningEnabled = true;
				break;
		}
	}

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
          print("Enter file path or select apk manualy");
		
	}
	});


        }

	private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
           }
           }
	
	
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
							print("error");
							
							
							}
                                        }         
                }
            });
        fpdialog.show();      
    }

	
    

	public void StartSign() {
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Signing...");
		progressDialog.setMessage("Your apk is almost ready...");
		progressDialog.setIndeterminate(true);
		progressDialog.show();

		final Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				progressDialog.dismiss();
			}
		};

		new Thread() {
			public void run() {
				try {
					new Signer().calculateSignature(
						editText.getText().toString(), 
						editText.getText().toString().replace(".apk", "_sign.apk")
					);

					runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dialogFinished();
								editText.setText("");
							}
						});

				} catch (final ApkFormatException e) {
					runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
							}
						});
				} catch (final GeneralSecurityException e) {
					runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
							}
						});
				} catch (final IOException e) {
					runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
							}
						});
				}

				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	public void dialogFinished() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Signed!!");
		alertDialog.setMessage("Path: " + editText.getText().toString().replace(".apk", "_sign.apk"));
		alertDialog.setCancelable(true);
		alertDialog.setPositiveButton("Ok", null);
		alertDialog.show();
	}



	private void print (String srt){
	Toast.makeText(this, srt, Toast.LENGTH_SHORT).show();
	}
	
	
	
    
}

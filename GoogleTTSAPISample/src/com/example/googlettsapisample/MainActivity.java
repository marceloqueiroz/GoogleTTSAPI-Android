package com.example.googlettsapisample;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import br.com.marcelo.googlettsapi.GoogleTTSAPI;

public class MainActivity extends Activity {
	
	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.editText = (EditText) this.findViewById(R.id.editText1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void btnTTSClicked(View v) {
		new TTSTask().execute(this.editText.getText().toString(), "pt-BR");
	}

	 private class TTSTask extends AsyncTask<String, Void, Long> {
	     protected Long doInBackground(String... params) {
	    	GoogleTTSAPI ttsAPI = new GoogleTTSAPI(null);
	    	long audioSize = 0;
	 		try {
	 			byte[] audio = ttsAPI.textToSpeech(params[0],params[1]);
	 			audioSize = audio.length;
	 			
	 			File extStore = Environment.getExternalStorageDirectory();
	 			File f = new File(extStore.getAbsolutePath(), "audio.mp3");
	 				 			
	 			FileOutputStream stream = new FileOutputStream(f);
	 			stream.write(audio);
	 			stream.flush();
	 			stream.close();
	 			Log.d("lkjskjhdfh", "size: " + audioSize);
	 			
	 		} catch (Exception e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
	 		return audioSize;
	     }

	     protected void onPostExecute(Long result) {
	      
	     }
	 }
}

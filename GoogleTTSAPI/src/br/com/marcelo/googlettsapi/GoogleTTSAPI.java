package br.com.marcelo.googlettsapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import org.apache.http.client.utils.URLEncodedUtils;

import android.content.Context;
import android.util.Log;

public class GoogleTTSAPI {
	private static final int GoogleTTSSourceTextLimitLength = 100;
	
	private Context currentContext;
	
	public GoogleTTSAPI(Context context) {
		this.currentContext = context;
	}

	public boolean checkGoogleTTSAPIAvailability() throws Exception {
		byte[] audio = this.textToSpeech("Hello World", "en-US", false);
		return (audio != null && audio.length > 0);
	}
	
	public byte[] textToSpeech(String text) throws Exception {
		return this.textToSpeech(text, Locale.getDefault().getDisplayLanguage());
	}
	
	public byte[] textToSpeech(String text, String language) throws Exception {
		return this.textToSpeech(text, language, true);
	}
	
	private byte[] textToSpeech(String text, String language, boolean cached) throws Exception {
		if (text != null && text.trim().length() > 0) {
			if (language != null && language.trim().length() > 0) {
				return this.googleTextToSpeech(text, language);
			} else {
				throw new Exception("Invalid locale, please provided a valid locale.");
			}
		} else {
			throw new Exception("Invalid text, please provided a valid text.");
		}
	}
	
	private byte[] googleTextToSpeech(String text, String language) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		String editedText = text.replace("\n", "");
		String[] textComponents = editedText.split("(\\,|\\.)");
		
		for (int i = 0; i < textComponents.length; i++) {
			String trimmedText = textComponents[i].trim();
			
			if (trimmedText.length() <= GoogleTTSSourceTextLimitLength) {
				this.googleTextToSpeechData(trimmedText, language, byteArrayOutputStream);
			} else {
				String[] subparts = trimmedText.split(" ");
				StringBuffer subString = new StringBuffer();
				
				for (int j = 0; j < subparts.length; j++) {
					String element = subparts[j];
					
					if (element.length() + subString.length() <= GoogleTTSSourceTextLimitLength) {
						subString.append(element);
						subString.append(" ");
					} else {
						this.googleTextToSpeechData(subString.toString(), language, byteArrayOutputStream);
						subString = new StringBuffer();
					}
				}
				
				if (subString.length() > 0) {
					this.googleTextToSpeechData(subString.toString(), language, byteArrayOutputStream);
				}
			}
		}
		byteArrayOutputStream.flush();
		return byteArrayOutputStream.toByteArray();
	}
	
	private void googleTextToSpeechData(String text, String language, ByteArrayOutputStream outputStream) throws IOException {
		String urlString = "http://translate.google.com/translate_tts?ie=UTF-8&q=" +  URLEncoder.encode(text, "UTF-8") + "&tl=" + language + "&total=1&idx=0&textlen=" + text.length() + "&prev=input";
		Log.d("OIA", urlString);
        URL url = new URL(urlString);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setDoOutput(true);
        c.connect();
        
        InputStream is = c.getInputStream();
        
        byte[] buffer = new byte[1024];
        int len1 = 0;
        while ((len1 = is.read(buffer)) != -1) {
        	outputStream.write(buffer, 0, len1);
        }
        is.close();
	}
}

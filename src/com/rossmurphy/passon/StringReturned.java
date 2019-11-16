package com.rossmurphy.passon;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StringReturned {
		URL url = null;
	  public StringReturned(URL url) {
	    this.url = url;
	    //System.out.println(url);
	  }

	  public StringBuffer getContents() throws Exception {
	    StringBuffer buffer;
	    String line;
	    int responseCode;
	    HttpURLConnection connection;
	    InputStream input;
	    BufferedReader dataInput;
	    connection = (HttpURLConnection) url.openConnection();
	    responseCode = connection.getResponseCode();
	    if (responseCode != HttpURLConnection.HTTP_OK) {
	      throw new Exception("HTTP response code: " +
	                          String.valueOf(responseCode));
	    }
	    try {
	      buffer = new StringBuffer();
	      input = connection.getInputStream();
	      dataInput = new BufferedReader(new InputStreamReader(input));
	      while ( (line = dataInput.readLine()) != null) {
	        buffer.append(line);
	        buffer.append("\r\n");
	      }
	      input.close();
	    }
	    catch (Exception ex) {
	      ex.printStackTrace(System.err);
	      return null;
	    }
	    return buffer;
	  }
}

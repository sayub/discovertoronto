/*
* @author  Saad Muhammad Ayub
* Copyright 2016, Saad Muhammad Ayub, All rights reserved.
*/

package toronto.amazinglocations.com.discovertoronto.misc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BikesLocationReaderAsyncTask extends AsyncTask<String, Void, String> {
    private Handler mPostExecuteHandler;

    public BikesLocationReaderAsyncTask(Handler postExecuteHandler) {
        mPostExecuteHandler = postExecuteHandler;
    }

    protected String doInBackground(String... urls) {
        InputStream inputStream;
        BufferedReader bufferedReader;
        String line;
        String JSONInput = "";

        try {
            // The url to fetch JSON data from.
            URL url = new URL(urls[0]);
            // Connecting with the url.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            // Getting the InputStream from the connection.
            inputStream = connection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            // Reading lines until null is returned.
            while((line = bufferedReader.readLine()) != null) {
                // Appending the lines to JSONInput.
                JSONInput += line;
            }

            // Returning JSONInput.
            return JSONInput;
        }
        catch(MalformedURLException ex) {

        }
        catch(IOException ex) {

        }
        finally {

        }

        return null;
    }

    protected void onPostExecute(String result) {
        // Sending the downloaded data to the Handler object.
        if(result != null) {
            Bundle msgBundle = new Bundle();
            msgBundle.putString("bikesLocationDetails", result);
            Message msg = new Message();
            msg.setData(msgBundle);
            mPostExecuteHandler.sendMessage(msg);
        }
    }
}
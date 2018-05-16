/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.client;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class RequestManager {

    private static final int TIMEOUT = 15 * 1000;
    String data;

    public void setData(String values) {
        this.data = values;
    }

    public interface RequestHandler {
        void onComplete(boolean success);
    }

    public interface RequestListener{
        void onResultCompleted(String result);
    }

    private RequestListener listener;

    public RequestManager(){}

    public void setListener(RequestListener listener) {
        this.listener = listener;
    }

    private class RequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        private RequestHandler handler;
        String method;

        RequestAsyncTask(String method, RequestHandler handler) {
            this.handler = handler;
            this.method = method;
        }

        RequestAsyncTask(RequestHandler handler){
            this.handler = handler;
        }

        @Override
        protected Boolean doInBackground(String... request) {
            return sendRequest(request[0],method);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            handler.onComplete(result);
        }
    }

    public boolean sendRequest(String request, @Nullable String method) {
        InputStream inputStream = null;
        try {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(TIMEOUT);
            connection.setConnectTimeout(TIMEOUT);
            connection.setRequestMethod((method != null) ? "GET" : "POST");
            if (data != null) {
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                connection.setRequestProperty("Accept","application/json");


                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                os.writeBytes(data);
                os.flush();
                os.close();

                Log.e("JSON RESULT",data);

            }

            connection.connect();
            if(data!=null){
                Log.e("RESPONSE",connection.getResponseMessage());
            }
            inputStream = connection.getInputStream();

            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            listener.onResultCompleted(sb.toString());
            br.close();
            return true;
        } catch (IOException error) {
            Log.e("ERROR", error.getLocalizedMessage());
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException secondError) {
                Log.w(RequestManager.class.getSimpleName(), secondError);
            }
        }
    }

    public void sendRequestAsync(String request, RequestHandler handler) {
        RequestAsyncTask task = new RequestAsyncTask(handler);
        task.execute(request);
    }

    public void sendRequestAsync(String request, String method, RequestHandler handler) {
        RequestAsyncTask task = new RequestAsyncTask(method,handler);
        task.execute(request);
    }

}

package com.klnm.houseofconversation.common;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class DoFileUpload {

    //String requestURL = "http://192.168.42.171:8080/imageUpload.do";

    String requestURL = "http://192.168.42.155:8080/add/image";

    String taskPhoneNum = "";

    List<String> taskImageList;
    TextView textView;
    ProgressBar progress;
    //백그라운드Task 정의
    MultipartSubmitTask multipartSubmitTask;

    JsonSubmitTask jsonSubmitTask;
    JSONObject jObj;


    int value;

    public void httpMultipartUpload(String requestURL, String phoneNum, List<String> imageList) {

        taskPhoneNum = phoneNum;

        taskImageList = imageList;

        multipartSubmitTask = new MultipartSubmitTask();
        //excute를 통해 실행시킨다
        //여기선 100을 매개변수로 보내는데 여기 예제에서는 이 매개변수를 doInBackGround에서 사용을 안했다.
        multipartSubmitTask.execute(100);


    }

    public void httpJsonUpload(String requestURL, String phoneNum, JSONObject jsonObj) {

        taskPhoneNum = phoneNum;

        jObj = jsonObj;

        requestURL = "http://192.168.42.109:8080/add/joinMember";

        jsonSubmitTask = new JsonSubmitTask();
        //excute를 통해 실행시킨다
        //여기선 100을 매개변수로 보내는데 여기 예제에서는 이 매개변수를 doInBackGround에서 사용을 안했다.
        jsonSubmitTask.execute(requestURL);


    }


    //새로운 TASK정의 (AsyncTask)
    // < >안에 들은 자료형은 순서대로 doInBackground, onProgressUpdate, onPostExecute의 매개변수 자료형을 뜻한다.(내가 사용할 매개변수타입을 설정하면된다)
    class MultipartSubmitTask extends AsyncTask<Integer , Integer , Integer> {
        //초기화 단계에서 사용한다. 초기화관련 코드를 작성했다.
        protected void onPreExecute() {

        }

        //스레드의 주작업 구현
        //여기서 매개변수 Intger ... values란 values란 이름의 Integer배열이라 생각하면된다.
        //배열이라 여러개를 받을 수 도 있다. ex) excute(100, 10, 20, 30); 이런식으로 전달 받으면 된다.
        protected Integer doInBackground(Integer ... values) {
            HttpResponse httpResponse;

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(requestURL);

                HttpParams params = client.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 10000);
                HttpConnectionParams.setSoTimeout(params, 10000);


                post.setHeader("Connection", "Keep-Alive");
                post.setHeader("Accept-Charset", "UTF-8");
                post.setHeader("ENCTYPE", "multipart/form-data");

                MultipartEntityBuilder meb = MultipartEntityBuilder.create();

                meb.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                meb.addTextBody("phoneNum", taskPhoneNum, ContentType.create("Multipart/related", "UTF-8"));

                for (String image : taskImageList) {
                    meb.addPart("pic", new FileBody(new File(image)));
                }
                HttpEntity entity = meb.build();

                post.setEntity(entity);

                httpResponse = client.execute(post);
                HttpEntity httpEntity = httpResponse.getEntity();

            }catch (Exception e) {
                e.printStackTrace();
            }

            return 1;
        }


        //이 Task에서(즉 이 스레드에서) 수행되던 작업이 종료되었을 때 호출됨
        protected void onPostExecute(Integer result) {


        }

        //Task가 취소되었을때 호출
        protected void onCancelled() {

        }
    }

    //새로운 TASK정의 (AsyncTask)
    // < >안에 들은 자료형은 순서대로 doInBackground, onProgressUpdate, onPostExecute의 매개변수 자료형을 뜻한다.(내가 사용할 매개변수타입을 설정하면된다)
    class JsonSubmitTask extends AsyncTask<String, Integer, Integer> {
        //초기화 단계에서 사용한다. 초기화관련 코드를 작성했다.
        protected void onPreExecute() {

        }

        //스레드의 주작업 구현
        //여기서 매개변수 Intger ... values란 values란 이름의 Integer배열이라 생각하면된다.
        //배열이라 여러개를 받을 수 도 있다. ex) excute(100, 10, 20, 30); 이런식으로 전달 받으면 된다.
        protected Integer doInBackground(String ... values) {
            HttpResponse httpResponse;

            Log.d("JsonSubmitTask", "doInBackground===" + jObj.toString());
            Log.d("JsonSubmitTask", "doInBackground values ===" + values.toString());

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(values[0]);

                HttpParams params = client.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 10000);
                HttpConnectionParams.setSoTimeout(params, 10000);

                post.setHeader("Content-type", "application/json; charset=utf-8");
                post.setHeader("Accept", "application/json");
                //post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");


                ArrayList<NameValuePair> postParameters;

                postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("jsonParam", jObj.toString()));
                //postParameters.add(new BasicNameValuePair("param1", "param1_value"));
                postParameters.add(new BasicNameValuePair("param2", "{\"id\":\"11\",\"pw\":\"22\"}"));

                post.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));



//                StringEntity entity = new StringEntity(jObj.toString(), "UTF-8");
//                entity.setContentType("application/json");
//                post.setEntity(entity);

                Log.d("JsonSubmitTask", "doInBackground postParameters ===" + postParameters.get(0));

                Log.d("JsonSubmitTask", "doInBackground getURI ===" + post.getURI());
                Log.d("JsonSubmitTask", "doInBackground post ===" + post.getEntity().toString());

                httpResponse = client.execute(post);
                HttpEntity httpEntity = httpResponse.getEntity();
            }catch (Exception e) {
                e.printStackTrace();
            }

            return 1;
        }


        //이 Task에서(즉 이 스레드에서) 수행되던 작업이 종료되었을 때 호출됨
        protected void onPostExecute(Integer result) {


        }

        //Task가 취소되었을때 호출
        protected void onCancelled() {

        }
    }


    public static String forJSON(String aText){
        final StringBuilder result = new StringBuilder();
        StringCharacterIterator iterator = new StringCharacterIterator(aText);
        char character = iterator.current();
        while (character != StringCharacterIterator.DONE){
            if( character == '\"' ){
                result.append("\\\"");
            }
            else if(character == '\\'){
                result.append("\\\\");
            }
            else if(character == '/'){
                result.append("\\/");
            }
            else if(character == '\b'){
                result.append("\\b");
            }
            else if(character == '\f'){
                result.append("\\f");
            }
            else if(character == '\n'){
                result.append("\\n");
            }
            else if(character == '\r'){
                result.append("\\r");
            }
            else if(character == '\t'){
                result.append("\\t");
            }
            else {
                //the char is not a special one
                //add it to the result as is
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }


    public void HttpFileUpload(String urlString, String params, String fileName) {



        String lineEnd = "\r\n";

        String twoHyphens = "--";

        String boundary = "*****";

        try {

            File sourceFile = new File(fileName);

            DataOutputStream dos;

            if (!sourceFile.isFile()) {

                Log.e("uploadFile", "Source File not exist :" + fileName);

            } else {

                FileInputStream mFileInputStream = new FileInputStream(sourceFile);

                URL connectUrl = new URL(urlString);

                // open connection

                HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();

                conn.setDoInput(true);

                conn.setDoOutput(true);

                conn.setUseCaches(false);

                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");

                conn.setRequestProperty("ENCTYPE", "multipart/form-data");

                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                conn.setRequestProperty("uploaded_file", fileName);

                // write data

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);



                int bytesAvailable = mFileInputStream.available();

                int maxBufferSize = 1024 * 1024;

                int bufferSize = Math.min(bytesAvailable, maxBufferSize);



                byte[] buffer = new byte[bufferSize];

                int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);



                // read image

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);

                    bytesAvailable = mFileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                }



                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                mFileInputStream.close();

                dos.flush(); // finish upload...

                if (conn.getResponseCode() == 200) {

                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    BufferedReader reader = new BufferedReader(tmp);

                    StringBuffer stringBuffer = new StringBuffer();

                    String line;

                    while ((line = reader.readLine()) != null) {

                        stringBuffer.append(line);

                    }

                }

                mFileInputStream.close();

                dos.close();

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}

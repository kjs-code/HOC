package com.klnm.houseofconversation.common;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class DoDataUpload {

    String requestURL = "http://192.168.42.155:8080/add/image";

    String serverIp = "http://192.168.42.13:8080/";

    String taskPhoneNum = "";

    List<String> taskImageList;
    TextView textView;
    ProgressBar progress;
    //백그라운드Task 정의
    DoFileUpload.MultipartSubmitTask multipartSubmitTask;

    DoDataUpload.DataSubmitTask dataSubmitTask;
    JSONObject jObj;
    String returnResult = "";

    public void httpMultipartUpload(String requestURL, String phoneNum, List<String> imageList) {

        taskPhoneNum = phoneNum;

        taskImageList = imageList;

        requestURL = serverIp + "add/image";

        dataSubmitTask = new DoDataUpload.DataSubmitTask();
        //excute를 통해 실행시킨다
        //여기선 100을 매개변수로 보내는데 여기 예제에서는 이 매개변수를 doInBackGround에서 사용을 안했다.
        dataSubmitTask.execute(requestURL, "image");


    }

    public String httpJsonUpload(String title, String phoneNum, JSONObject jsonObj) throws Exception {

        taskPhoneNum = phoneNum;

        jObj = jsonObj;

        requestURL = serverIp + "add/joinMember";

        dataSubmitTask = new DoDataUpload.DataSubmitTask();
        //excute를 통해 실행시킨다
        //여기선 100을 매개변수로 보내는데 여기 예제에서는 이 매개변수를 doInBackGround에서 사용을 안했다.
        dataSubmitTask.execute(requestURL, "data", title);

        returnResult = dataSubmitTask.get();

        return returnResult;


    }


    //새로운 TASK정의 (AsyncTask)
    // < >안에 들은 자료형은 순서대로 doInBackground, onProgressUpdate, onPostExecute의 매개변수 자료형을 뜻한다.(내가 사용할 매개변수타입을 설정하면된다)
    class DataSubmitTask extends AsyncTask<String, String, String> {
        //초기화 단계에서 사용한다. 초기화관련 코드를 작성했다.
        protected void onPreExecute() {

        }

        //스레드의 주작업 구현
        //여기서 매개변수 Intger ... values란 values란 이름의 Integer배열이라 생각하면된다.
        //배열이라 여러개를 받을 수 도 있다. ex) excute(100, 10, 20, 30); 이런식으로 전달 받으면 된다.
        protected String doInBackground(String ... values) {
            HttpResponse httpResponse;
            String returnStr = "";

            Log.d("JsonSubmitTask", "doInBackground===" + jObj.toString());
            Log.d("JsonSubmitTask", "doInBackground values ===" + values.toString());

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(values[0]);

                HttpParams params = client.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 10000);
                HttpConnectionParams.setSoTimeout(params, 10000);

                if(values[1].equals("image")){
                    imageExecuteClient(client, post);
                } else {
                    dataExecuteClient(client, post, values[2]);
                }


                httpResponse = client.execute(post);

                returnStr = changeInputStream(httpResponse);

                Log.d("JsonSubmitTask", "returnStr values ===" + returnStr);

            }catch (Exception e) {
                e.printStackTrace();
            }

            returnResult = returnStr;
            return returnStr;
        }


        //이 Task에서(즉 이 스레드에서) 수행되던 작업이 종료되었을 때 호출됨
        protected void onPostExecute(Integer result) {


        }

        //Task가 취소되었을때 호출
        protected void onCancelled() {

        }

        // 실제 전송하는 부분
        public void dataExecuteClient(HttpClient client, HttpPost post, String title) {
            try {

                post.setHeader("Content-type", "application/json; charset=utf-8");
                post.setHeader("Accept", "application/json");

                ArrayList<NameValuePair> postParameters;

                postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair(title, jObj.toString()));

                post.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));

                Log.d("JsonSubmitTask", "doInBackground postParameters ===" + postParameters.get(0));

                Log.d("JsonSubmitTask", "doInBackground getURI ===" + post.getURI());
                Log.d("JsonSubmitTask", "doInBackground post ===" + post.getEntity().toString());


            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // 실제 전송하는 부분
        public void imageExecuteClient(HttpClient client, HttpPost post) {
            try {

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

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // 실제 전송하는 부분
        public String changeInputStream(HttpResponse httpResponse) {
            String line = "";
            try {

                HttpEntity httpEntity = httpResponse.getEntity();

                Log.d("JsonSubmitTask", "httpEntity values ===" + httpEntity.toString());
                Log.d("JsonSubmitTask", "httpEntity values ===" + httpEntity.getContent());

                BufferedReader br = new BufferedReader(new InputStreamReader(httpEntity.getContent(),"UTF-8"));
                if (br != null){
                    line = URLDecoder.decode(br.readLine());
                }

                Log.d("JsonSubmitTask", "line values ===" + line);

            }catch (Exception e){
                e.printStackTrace();
                return e.getMessage();
            }
            return line;
        }


    }
}

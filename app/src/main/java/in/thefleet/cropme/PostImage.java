package in.thefleet.cropme;

import android.content.Context;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by DILEEP on 09-12-2016.
 */

public class PostImage {

    private Context context;

    public PostImage(Context context){
        this.context=context;
    }

    public static final MediaType JSON
            = MediaType.parse("text/x-markdown; charset=utf-8");

    OkHttpClient client = new OkHttpClient();
  /*  OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();*/

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Log.d("PostImage",json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.d("Post Image","Response messageis:"+response.code()+response.message());
            return response.body().string();


        }
    }
}

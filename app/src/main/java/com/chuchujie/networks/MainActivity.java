package com.chuchujie.networks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.chuchujie.core.network.okhttp.callback.Callback;
import com.chuchujie.core.network.okhttp.utils.OkHttpUtils;
import com.chuchujie.core.network.retrofit.NetworkManager;
import com.chuchujie.core.network.retrofit.RetrofitManager;
import com.chuchujie.core.network.retrofit.RxJava2CallAdapterWrapper;
import com.chuchujie.core.network.retrofit.convert.StringJsonParseException;
import com.chuchujie.download.DownloadActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;

public class MainActivity extends AppCompatActivity {

    String tag = "xixihaha";

    String url = "https://ads-api.chuchuguwen.com/";

    private RetrofitManager mRetrofitManager;
    private NetworkManager mNetworkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mNetworkManager = new NetworkManager
                .Builder()
                .context(getApplicationContext())
                .enableStetho(BuildConfig.DEBUG)
                .rxJava2CallErrorHandler(new RxJava2CallAdapterWrapper.RxJava2CallErrorHandler() {
                    @Override
                    public void handleNetworkError(Throwable throwable, Retrofit retrofit, Call call) {
                    //处理解析异常
                        if (throwable instanceof StringJsonParseException){

                        }
                    }
                })
                .strengthenNetwork(true)
                .logger(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Log.e("okhttp++++", message);
                    }
                })
                .logLevel(HttpLoggingInterceptor.Level.BODY)
                .build();

        mRetrofitManager = mNetworkManager.retrofitManager();
    }


    public void retrofit(View view) {
        ApiService service = mRetrofitManager.getService(url, ApiService.class);

        service.testGet(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TestResponse>() {
                    @Override
                    public void accept(TestResponse s) throws Exception {
                        Log.d(tag, "doOnNext:" + s);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(tag, "doOnError:" + throwable.getMessage());
                    }
                });
    }

    public void okhttpclient(View view) {
        OkHttpUtils.getInstance().setOkHttpClient(mNetworkManager.okHttpClient());

        OkHttpUtils.get().url(url).build().execute(new Callback<String>() {
            @Override
            public String parseNetworkResponse(Response response, int id) throws Exception {
                return response.body().string();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.d(tag, "okhttpclient:" + response);
            }

            @Override
            public void onError(okhttp3.Call call, Exception exception, int id) {
                Log.d(tag, "okhttpclient:" + exception.getMessage());
            }
        });
    }


    public interface ApiService {

        @GET("")
        Observable<TestResponse> testGet(@Url String url);

    }

    public class TestResponse {

        private int status;

        private TestData data;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public TestData getData() {
            return data;
        }

        public void setData(TestData data) {
            this.data = data;
        }

        public class TestData {

        }

    }


    public void open_download_page(View view) {
        startActivity(new Intent(this, DownloadActivity.class));
    }

}

package com.tartagliaeg.grmjava.setup;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GrmApiConfiguration {

  public static Retrofit create(Context context) {

    OkHttpClient client = enableTls12OnPreLollipop(new OkHttpClient.Builder())
      .build();

    return new Retrofit.Builder()
      .baseUrl("https://api.github.com")
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .client(client)
      .build();

  }


  /**
   * Workaround to stop SSLHandshakeException on old devices.
   *
   * @link https://github.com/square/okhttp/issues/2372
   */
  private static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
    if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
      try {
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, null, null);

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
          throw new IllegalStateException("Unexpected default trust managers:"
            + Arrays.toString(trustManagers));
        }
        X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

        client.sslSocketFactory(
          new Tls12SocketFactory(sc.getSocketFactory()),
          trustManager
        );

        ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
          .tlsVersions(TlsVersion.TLS_1_2)
          .build();

        List<ConnectionSpec> specs = new ArrayList<>();
        specs.add(cs);
        specs.add(ConnectionSpec.COMPATIBLE_TLS);
        specs.add(ConnectionSpec.CLEARTEXT);

        client.connectionSpecs(specs);
      } catch (Exception exc) {
        Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
      }
    }

    return client;
  }

}

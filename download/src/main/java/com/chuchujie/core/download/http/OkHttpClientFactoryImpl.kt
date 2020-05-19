package com.chuchujie.core.download.http

import com.chuchujie.core.download.utils.d
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * Created by wangjing on 2018/3/20.
 */
class OkHttpClientFactoryImpl : OkHttpClientFactory {

    private val DEFAULT_CONNECT_TIMEOUT: Long = 10
    private val DEFAULT_READ_TIMEOUT: Long = 15
    private val DEFAULT_WRITE_TIMEOUT: Long = 15
    private val DEFAULT_TIME_UNIT = TimeUnit.SECONDS

    override fun build(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        // set read write connect timeout
        builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, DEFAULT_TIME_UNIT)
        builder.readTimeout(DEFAULT_READ_TIMEOUT, DEFAULT_TIME_UNIT)
        builder.writeTimeout(DEFAULT_WRITE_TIMEOUT, DEFAULT_TIME_UNIT)

        // add http logging
        val httpLoggingInterceptor = HttpLoggingInterceptor(
                HttpLoggingInterceptor.Logger { message -> d(message) })
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(httpLoggingInterceptor)

        // ignore https hostname verification
        builder.hostnameVerifier({ _, _ -> true })

        // ignore https certificate verification
        val sslSocketFactoryInfo = createSslSocketFactory()
        builder.sslSocketFactory(sslSocketFactoryInfo.first, sslSocketFactoryInfo.second)
        builder.followSslRedirects(true)

        return builder.build()
    }

    private fun createSslSocketFactory(): Pair<SSLSocketFactory, X509TrustManager> {
        val sslContext = SSLContext.getInstance("TLS")
        val trustManager = object : X509TrustManager {

            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()

        }
        val trustManagers = mutableListOf<X509TrustManager>()
        trustManagers.add(trustManager)
        sslContext.init(null, trustManagers.toTypedArray(), null)
        return Pair(sslContext.socketFactory, trustManager)
    }

}
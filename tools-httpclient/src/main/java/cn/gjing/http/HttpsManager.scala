package cn.gjing.http

import java.security.cert.X509Certificate

import javax.net.ssl.X509TrustManager

/**
 * @author Gjing
 **/
class HttpsManager extends X509TrustManager{
  override def checkClientTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = {}

  override def checkServerTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = {}

  override def getAcceptedIssuers: Array[X509Certificate] = new Array[X509Certificate](0)
}

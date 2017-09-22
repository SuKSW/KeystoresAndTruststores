import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;

/**
 * Created by subhashinie on 7/24/17.
 */
public class Test4 {
    final char[] PASSWORD_TO_LOAD_KEYSTORE  = "kube_conn_keystore_pass".toCharArray();
    final char[] PASSWORD_TO_LOAD_TRUSTSTORE  = "kube_conn_truststore_pass".toCharArray();

    public void startTest() throws Exception{

        /*
         * Keystore has apiserver.crt and apiserver.key
         * Truststore has ca.crt
         *
         */



        //Loading the keystore
        InputStream keystoreStream = new FileInputStream("/home/subhashinie/Documents/kubeAuthTest/forTest4/kube_conn_keystore");
        KeyStore clientKeystore = KeyStore.getInstance("JKS");
        clientKeystore.load(keystoreStream, PASSWORD_TO_LOAD_KEYSTORE);
        keystoreStream.close();

        //Loading the truststore
        InputStream truststoreStream = new FileInputStream("/home/subhashinie/Documents/kubeAuthTest/forTest4/kube_conn_truststore");
        KeyStore clientTruststore = KeyStore.getInstance("JKS");
        clientTruststore.load(truststoreStream, PASSWORD_TO_LOAD_TRUSTSTORE);
        truststoreStream.close();

        // build the KeyManager (SSL client credentials we can send)
        KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyFactory.init(clientKeystore, PASSWORD_TO_LOAD_KEYSTORE);
        KeyManager[] km = keyFactory.getKeyManagers();

        // build the TrustManager (Server certificates we trust)
        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(clientTruststore);
        TrustManager[] tm = trustFactory.getTrustManagers();

        // build a SSLContext with both our trust/key managers
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(km, tm, null);
        SSLSocketFactory sslSf = sslContext.getSocketFactory();

        URL url = new URL("https://192.168.99.100:8443/api/v1/namespaces/default/services");
        HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) url.openConnection();

        httpsUrlConnection.setSSLSocketFactory(sslSf);

        //start reading
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpsUrlConnection.getInputStream()));
        String string = null;
        while ((string = bufferedreader.readLine()) != null) {
            System.out.println("Received " + string);
        }
    }
}

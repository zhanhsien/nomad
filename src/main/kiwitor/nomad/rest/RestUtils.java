package main.kiwitor.nomad.rest;

import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.*;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

public class RestUtils implements AutoCloseable {
    private WebTarget target;
    private Client client;

    RestUtils(String targetUrl) {
        getWebTarget(targetUrl);
    }

    RestUtils(String targetUrl, String path) {
        getWebTarget(targetUrl, path);
    }

    private void getWebTarget(String targetUrl) {
        getWebTarget(targetUrl, null);
    }


    private void getWebTarget(String targetUrl, String path) {
        client = ClientBuilder.newBuilder()
                .sslContext(getSSLContext())
                .hostnameVerifier((s, sslSession) -> true)
                .build();

        target = StringUtils.isNotEmpty(path) ? client.target(targetUrl).path(path) : client.target(targetUrl);
    }

    void query(String[]... query) {
        for(String[] q : query) {
            target = target.queryParam(q[0], q[1]);
        }
    }

    Response get() {
        return call(HttpMethod.GET, null, null);
    }

    Response get(MultivaluedMap<String, Object> headers) {
        return call(HttpMethod.GET, headers, null);
    }

    Response post(Form form) {
        return call(HttpMethod.POST, null, form);
    }

    Response post(String entity, MultivaluedMap<String, Object> headers) {
        return call(HttpMethod.POST, headers, entity);
    }

    private Response call(String method, MultivaluedMap<String, Object> headers, Object entity) {
        System.out.println(target.getUri().toString());
        Invocation.Builder builder = headers != null ? target.request().headers(headers) : target.request();
        switch(method) {
            case HttpMethod.POST:
                MediaType type = entity instanceof Form ?
                        MediaType.APPLICATION_FORM_URLENCODED_TYPE : MediaType.APPLICATION_JSON_TYPE;
                return builder.post(Entity.entity(entity, type));
            default:
                return builder.get();
        }
    }

    private static SSLContext getSSLContext() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(X509Certificate[] certs, String authType) { }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return sc;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void close() {
        client.close();
    }
}

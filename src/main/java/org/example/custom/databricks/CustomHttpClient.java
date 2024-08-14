//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.example.custom.databricks;

import com.databricks.sdk.core.DatabricksConfig;
import com.databricks.sdk.core.DatabricksException;
import com.databricks.sdk.core.ProxyConfig;
import com.databricks.sdk.core.http.HttpClient;
import com.databricks.sdk.core.http.Request;
import com.databricks.sdk.core.http.Response;
import com.databricks.sdk.core.utils.CustomCloseInputStream;
import com.databricks.sdk.core.utils.ProxyUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomHttpClient implements HttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(CustomHttpClient.class);
    private final PoolingHttpClientConnectionManager connectionManager;
    private final CloseableHttpClient hc;
    private final int timeout;

    public CustomHttpClient(int timeoutSeconds) {
        this.connectionManager = new PoolingHttpClientConnectionManager();
        this.timeout = timeoutSeconds * 1000;
        // modify the connection manager
        this.connectionManager.setDefaultMaxPerRoute(100);
        this.hc = this.makeClosableHttpClient();
    }

    public CustomHttpClient(DatabricksConfig databricksConfig) {
        this(databricksConfig.getHttpTimeoutSeconds() == null ? 300 : databricksConfig.getHttpTimeoutSeconds(), new ProxyConfig(databricksConfig));
    }

    public CustomHttpClient(int timeoutSeconds, ProxyConfig proxyConfig) {
        this.connectionManager = new PoolingHttpClientConnectionManager();
        this.timeout = timeoutSeconds * 1000;
        this.connectionManager.setMaxTotal(100);
        this.hc = this.makeClosableHttpClient(proxyConfig);
    }

    private RequestConfig makeRequestConfig() {
        return RequestConfig.custom().setConnectionRequestTimeout(this.timeout).setConnectTimeout(this.timeout).setSocketTimeout(this.timeout).build();
    }

    private CloseableHttpClient makeClosableHttpClient() {
        return HttpClientBuilder.create().setConnectionManager(this.connectionManager).setDefaultRequestConfig(this.makeRequestConfig()).build();
    }

    private CloseableHttpClient makeClosableHttpClient(ProxyConfig proxyConfig) {
        HttpClientBuilder builder = HttpClientBuilder.create().setConnectionManager(this.connectionManager).setDefaultRequestConfig(this.makeRequestConfig());
        ProxyUtils.setupProxy(proxyConfig, builder);
        return builder.build();
    }

    public Response execute(Request in) throws IOException {
        HttpUriRequest request = this.transformRequest(in);
        boolean handleRedirects = (Boolean)in.getRedirectionBehavior().orElse(true);
        if (!handleRedirects) {
            request.getParams().setParameter("http.protocol.handle-redirects", false);
        }

        in.getHeaders().forEach(request::setHeader);
        HttpContext context = new BasicHttpContext();
        CloseableHttpResponse response = this.hc.execute(request, context);
        return this.computeResponse(in, context, response);
    }

    private URL getTargetUrl(HttpContext context) {
        try {
            HttpHost targetHost = (HttpHost)context.getAttribute("http.target_host");
            HttpUriRequest request = (HttpUriRequest)context.getAttribute("http.request");
            URI uri = new URI(targetHost.getSchemeName(), (String)null, targetHost.getHostName(), targetHost.getPort(), request.getURI().getPath(), request.getURI().getQuery(), request.getURI().getFragment());
            return uri.toURL();
        } catch (URISyntaxException | MalformedURLException var5) {
            Exception e = var5;
            throw new DatabricksException("Unable to get target URL", e);
        }
    }

    private Response computeResponse(Request in, HttpContext context, CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        StatusLine statusLine = response.getStatusLine();
        Map<String, List<String>> hs = (Map)Arrays.stream(response.getAllHeaders()).collect(Collectors.groupingBy(NameValuePair::getName, Collectors.mapping(NameValuePair::getValue, Collectors.toList())));
        URL url = this.getTargetUrl(context);
        if (entity == null) {
            response.close();
            return new Response(in, url, statusLine.getStatusCode(), statusLine.getReasonPhrase(), hs);
        } else {
            boolean streamResponse = in.getHeaders().containsKey("Accept") && !ContentType.APPLICATION_JSON.getMimeType().equals(in.getHeaders().get("Accept")) && !ContentType.APPLICATION_JSON.getMimeType().equals(response.getFirstHeader("Content-Type").getValue());
            if (streamResponse) {
                CustomCloseInputStream inputStream = new CustomCloseInputStream(entity.getContent(), () -> {
                    try {
                        response.close();
                    } catch (Exception var2) {
                        Exception e = var2;
                        throw new DatabricksException("Unable to close connection", e);
                    }
                });
                return new Response(in, url, statusLine.getStatusCode(), statusLine.getReasonPhrase(), hs, inputStream);
            } else {
                Response var12;
                try {
                    InputStream inputStream = entity.getContent();
                    Throwable var10 = null;

                    try {
                        String body = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                        var12 = new Response(in, url, statusLine.getStatusCode(), statusLine.getReasonPhrase(), hs, body);
                    } catch (Throwable var28) {
                        var10 = var28;
                        throw var28;
                    } finally {
                        if (inputStream != null) {
                            if (var10 != null) {
                                try {
                                    inputStream.close();
                                } catch (Throwable var27) {
                                    var10.addSuppressed(var27);
                                }
                            } else {
                                inputStream.close();
                            }
                        }

                    }
                } finally {
                    response.close();
                }

                return var12;
            }
        }
    }

    private HttpUriRequest transformRequest(Request in) {
        switch (in.getMethod()) {
            case "GET":
                return new HttpGet(in.getUri());
            case "HEAD":
                return new HttpHead(in.getUri());
            case "DELETE":
                return new HttpDelete(in.getUri());
            case "POST":
                return this.withEntity(new HttpPost(in.getUri()), in);
            case "PUT":
                return this.withEntity(new HttpPut(in.getUri()), in);
            case "PATCH":
                return this.withEntity(new HttpPatch(in.getUri()), in);
            default:
                throw new IllegalArgumentException("Unknown method: " + in.getMethod());
        }
    }

    private HttpRequestBase withEntity(HttpEntityEnclosingRequestBase request, Request in) {
        if (in.isBodyString()) {
            request.setEntity(new StringEntity(in.getBodyString(), StandardCharsets.UTF_8));
        } else if (in.isBodyStreaming()) {
            request.setEntity(new InputStreamEntity(in.getBodyStream()));
        } else {
            LOG.warn("withEntity called with a request with no body, so no request entity will be set. URI: {}", in.getUri());
        }

        return request;
    }
}

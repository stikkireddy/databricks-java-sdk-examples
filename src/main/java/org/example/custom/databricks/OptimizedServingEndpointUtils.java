package org.example.custom.databricks;

import java.net.MalformedURLException;
import java.net.URL;

public class OptimizedServingEndpointUtils {

    public static String getEndpointHost(String endpointUrl) {
        try {
            URL url = new URL(endpointUrl);
            return new URL(url.getProtocol(), url.getHost(), -1, "").toString();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static String getEndpointId(String endpointUrl) {
        try {
            URL url = new URL(endpointUrl);
            String host = url.getHost();
            return host.split("\\.")[0];
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static String getWorkspaceId(String endpointUrl) {
        try {
            URL url = new URL(endpointUrl);
            String path = url.getPath();
            // path starts with a / so we need to get the first index
            return path.split("/")[1];
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static String getEndpointName(String endpointUrl) {
        try {
            URL url = new URL(endpointUrl);
            String path = url.getPath();
            return path.split("/")[3];
        } catch (MalformedURLException e) {
            return null;
        }
    }

}

package org.example.custom.databricks;

import com.databricks.sdk.core.ApiClient;
import com.databricks.sdk.service.serving.QueryEndpointResponse;

import java.util.HashMap;
import java.util.Map;


public class OptimizedServingEndpointService {

    private final ApiClient apiClient;

    public OptimizedServingEndpointService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public QueryEndpointResponse query(OptimizedEndpointQueryEndpointInput request) {
        String path = String.format("/%s/serving-endpoints/%s/invocations", request.getWorkspaceId(), request.getName());
        Map<String, String> headers = new HashMap();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        return (QueryEndpointResponse) this.apiClient.POST(path, request, QueryEndpointResponse.class, headers);
    }
}

package org.example.custom.databricks;

import com.databricks.sdk.core.ApiClient;
import com.databricks.sdk.service.serving.QueryEndpointInput;
import com.databricks.sdk.service.serving.QueryEndpointResponse;

import java.util.HashMap;
import java.util.Map;


public class OptimizedServingEndpointService {

    private final ApiClient apiClient;

    public OptimizedServingEndpointService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public QueryEndpointResponse query(QueryEndpointInput request, String workspaceId) {
        String path = String.format("/%s/serving-endpoints/%s/invocations", workspaceId, request.getName());
        Map<String, String> headers = new HashMap();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        return this.apiClient.POST(path, request, QueryEndpointResponse.class, headers);
    }
}

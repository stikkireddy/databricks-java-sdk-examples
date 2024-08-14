package org.example.custom.databricks;

import com.databricks.sdk.WorkspaceClient;
import com.databricks.sdk.core.DatabricksConfig;


public class OptimizedWorkspaceClient extends WorkspaceClient {

    private final OptimizedServingEndpointService optimizedServingEndpointService;

    public OptimizedWorkspaceClient(DatabricksConfig config) {
        super(config);
        this.optimizedServingEndpointService = new OptimizedServingEndpointService(super.apiClient());
    }

    public OptimizedServingEndpointService optimizedServingEndpointService() {
        return this.optimizedServingEndpointService;
    }

}

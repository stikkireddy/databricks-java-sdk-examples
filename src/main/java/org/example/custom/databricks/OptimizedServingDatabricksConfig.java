package org.example.custom.databricks;

import com.databricks.sdk.core.DatabricksConfig;
import com.databricks.sdk.core.oauth.OpenIDConnectEndpoints;

import java.io.IOException;

public class OptimizedServingDatabricksConfig extends DatabricksConfig {

    private final String tokenEndpoint;

    private final String authorizationEndpoint;

    public OptimizedServingDatabricksConfig(String tokenEndpoint, String authorizationEndpoint) {
        super();
        this.tokenEndpoint = tokenEndpoint;
        this.authorizationEndpoint = authorizationEndpoint;
    }

    @Override
    public OpenIDConnectEndpoints getOidcEndpoints() throws IOException {
        return new OpenIDConnectEndpoints(this.tokenEndpoint, this.authorizationEndpoint);
    }

}
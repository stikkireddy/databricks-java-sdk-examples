package org.example.custom.databricks;

import com.databricks.sdk.core.DatabricksConfig;
import com.databricks.sdk.core.oauth.OpenIDConnectEndpoints;

import java.io.IOException;

public class OptimizedServingDatabricksConfig extends DatabricksConfig {

    private String tokenEndpoint;

    private String authorizationEndpoint;

    public OptimizedServingDatabricksConfig(String tokenEndpoint, String authorizationEndpoint) {
        super();
        this.tokenEndpoint = tokenEndpoint;
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public OptimizedServingDatabricksConfig setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
        return this;
    }

    public OptimizedServingDatabricksConfig setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
        return this;
    }


    @Override
    public OpenIDConnectEndpoints getOidcEndpoints() throws IOException {
        return new OpenIDConnectEndpoints(this.tokenEndpoint, this.authorizationEndpoint);
    }

}
package org.example;

import com.databricks.sdk.WorkspaceClient;
import com.databricks.sdk.core.DatabricksConfig;
import com.databricks.sdk.core.oauth.OpenIDConnectEndpoints;
import com.databricks.sdk.service.serving.QueryEndpointInput;
import com.databricks.sdk.service.serving.QueryEndpointResponse;
import org.example.custom.databricks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {
    public static void main(String[] args) throws Exception {

        // the serving endpoint url you want to query
        // it looks like https://<some-id>.serving...com/<workspace-id>/serving-endpoints/<endpoint-name>/invocations
        String servingEndpointUrl = "";
        // The workspace url no slash at the end just https://.....com
        String workspaceUrl = "";
        // The databricks managed oauth client
        String clientId = "";
        // The databricks managed oauth secret (use the databricks managed one in the UI)
        String clientSecret = "";

        // The throw away client is used to get the oidc endpoints and then discarded
        WorkspaceClient throwAwayClient = new WorkspaceClient(new DatabricksConfig().setHost(workspaceUrl));
        OpenIDConnectEndpoints oidcEndpoints = throwAwayClient.config().getOidcEndpoints();
        String authorizationEndpoint = oidcEndpoints.getAuthorizationEndpoint();
        String tokenEndpoint = oidcEndpoints.getTokenEndpoint();

        String servingEndpointHost = OptimizedServingEndpointUtils.getEndpointHost(servingEndpointUrl);
        String endpointId = OptimizedServingEndpointUtils.getEndpointId(servingEndpointUrl);
        //  query_inference_endpoint or manage_inference_endpoint
        String[] actions = new String[]{"query_inference_endpoint"};
        String authType = "oauth-m2m";
        String endpointName = OptimizedServingEndpointUtils.getEndpointName(servingEndpointUrl);
        String workspaceId = OptimizedServingEndpointUtils.getWorkspaceId(servingEndpointUrl);

        DatabricksConfig config = new OptimizedServingDatabricksConfig(tokenEndpoint, authorizationEndpoint)
                .setHost(servingEndpointHost)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setAuthType(authType)
                .setCredentialsProvider(
                        // you can add multiple serving endpoints with different actions to the same auth token
                        new OAuthM2MWithAuthDetailsServicePrincipalCredentialsProvider()
                                .addServingEndpoint(endpointId, actions)
                );

        OptimizedWorkspaceClient workspace = new OptimizedWorkspaceClient(config);
        // this is inputs for testing, your inputs may vary
        Map<String, List<Double>> inputs = new HashMap<>();
        inputs.put("fixed_acidity", new ArrayList<>(List.of(7.4)));
        inputs.put("volatile_acidity", new ArrayList<>(List.of(0.7)));
        inputs.put("citric_acid", new ArrayList<>(List.of(0.0)));
        inputs.put("residual_sugar", new ArrayList<>(List.of(1.9)));
        inputs.put("chlorides", new ArrayList<>(List.of(0.076)));
        inputs.put("free_sulfur_dioxide", new ArrayList<>(List.of(11.0)));
        inputs.put("total_sulfur_dioxide", new ArrayList<>(List.of(34.0)));
        inputs.put("density", new ArrayList<>(List.of(0.9978)));
        inputs.put("pH", new ArrayList<>(List.of(3.51)));
        inputs.put("sulphates", new ArrayList<>(List.of(0.56)));
        inputs.put("alcohol", new ArrayList<>(List.of(9.4)));
        inputs.put("is_red", new ArrayList<>(List.of(0.0)));
        QueryEndpointInput request = new OptimizedEndpointQueryEndpointInput()
                .setName(endpointName)
                .setInputs(inputs);
        QueryEndpointResponse results = workspace.optimizedServingEndpointService().query(request, workspaceId);
        System.out.println(results.getPredictions());
    }
}
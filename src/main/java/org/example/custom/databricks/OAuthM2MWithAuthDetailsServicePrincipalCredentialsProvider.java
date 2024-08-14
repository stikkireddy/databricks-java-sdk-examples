package org.example.custom.databricks;

import com.databricks.sdk.core.DatabricksConfig;
import com.databricks.sdk.core.DatabricksException;
import com.databricks.sdk.core.HeaderFactory;
import com.databricks.sdk.core.oauth.*;

import java.io.IOException;
import java.util.*;

public class OAuthM2MWithAuthDetailsServicePrincipalCredentialsProvider extends OAuthM2MServicePrincipalCredentialsProvider {

    private List<String> authorizationDetails = new ArrayList<>();

    public OAuthM2MWithAuthDetailsServicePrincipalCredentialsProvider addServingEndpoint(String endpointId,
                                                                                         String[] action) {
        if (action.length == 0) {
            throw new IllegalArgumentException("At least one action must be provided");
        }
        String actionsString = Arrays.stream(action).map((a) -> '"' + a + '"').reduce((a, b) -> a + "," + b).get();
        String details = String.format(
                "{\"type\":\"workspace_permission\",\"object_type\":\"serving-endpoints\",\"object_path\":\"/serving-endpoints/%s\",\"actions\": [%s]}", endpointId, actionsString);
        this.authorizationDetails.add(details);
        return this;
    }

    @Override
    public HeaderFactory configure(DatabricksConfig config) {
        if (config.getClientId() != null && config.getClientSecret() != null && config.getHost() != null) {
            try {
                OpenIDConnectEndpoints jsonResponse = config.getOidcEndpoints();
                String authDetails = '[' + authorizationDetails.stream().reduce((a, b) -> a + "," + b).get() + ']';
                Map<String, String> endpointParameters = new HashMap<>();
                endpointParameters.put("authorization_details", authDetails);
                ClientCredentials tokenSource = (new ClientCredentials.Builder())
                        .withHttpClient(config.getHttpClient())
                        .withClientId(config.getClientId())
                        .withClientSecret(config.getClientSecret())
                        .withTokenUrl(jsonResponse.getTokenEndpoint())
                        .withScopes(Collections.singletonList("all-apis"))
                        .withAuthParameterPosition(AuthParameterPosition.HEADER)
                        .withEndpointParameters(endpointParameters)
                        .build();
                return () -> {
                    Token token = tokenSource.getToken();
                    Map<String, String> headers = new HashMap();
                    headers.put("Authorization", token.getTokenType() + " " + token.getAccessToken());
                    return headers;
                };
            } catch (IOException var4) {
                IOException e = var4;
                throw new DatabricksException("Unable to fetch OIDC endpoint: " + e.getMessage(), e);
            }
        } else {
            return null;
        }
    }
}
# databricks-java-sdk-examples

This repository contains examples of how to use the Databricks Java SDK to interact with Databricks optimized model
serving endpoints.

The goal is to use as much of the databricks sdk for auth and extend classes as needed
to make api calls to the new model serving endpoints.

The code is provided as is.

All the code is in src/main/java/org/example/custom/databricks folder

1. OAuthM2MWithAuthDetailsServicePrincipalCredentialsProvider.java - This class is used to authenticate with Databricks
   using OAuth 2.0 M2M flow with Auth Details Service Principal.
2. OptimizedEndpointQueryEndpointInput.java - This class is used to query the optimized model serving endpoint.

Look at the Main.java file to see how to use the classes.

## Disclaimer

The code is provided as is and no support will be provided for this specific code repository. This is not affiliated
with the databricks sdk by any means.
The primary use is as a workaround to use the databricks sdk to interact with the optimized model serving endpoints as
that is not yet natively supported.
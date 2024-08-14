package org.example.custom.databricks;

import com.databricks.sdk.service.serving.QueryEndpointInput;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class OptimizedEndpointQueryEndpointInput extends QueryEndpointInput {

    // when doing inputs this should be ignored and not sent as part of the payload
    @Override
    @JsonIgnore
    public String getName() {
        return super.getName();
    }

    @Override
    public QueryEndpointInput setName(String name) {
        super.setName(name);
        return this;
    }
}
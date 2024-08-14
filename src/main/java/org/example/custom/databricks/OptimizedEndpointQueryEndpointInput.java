package org.example.custom.databricks;

import com.databricks.sdk.service.serving.QueryEndpointInput;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class OptimizedEndpointQueryEndpointInput extends QueryEndpointInput {

    private String workspaceId;

    @JsonIgnore
    public String getWorkspaceId() {
        return workspaceId;
    }

    public OptimizedEndpointQueryEndpointInput setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
        return this;
    }

    @Override
    @JsonIgnore
    public String getName() {
        return super.getName();
    }

    @Override
    public OptimizedEndpointQueryEndpointInput setName(String name) {
        super.setName(name);
        return this;
    }
}
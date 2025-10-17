package de.christophdick.tenga.mcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class JsonRpcRequest {

    @NotBlank(message = "JSON-RPC version must be specified")
    private String jsonrpc = "2.0";

    @NotBlank(message = "Method name is required")
    private String method;

    private Object params;

    @NotNull(message = "Request ID is required")
    private Object id;

    public JsonRpcRequest() {
    }

    public JsonRpcRequest(String method, Object params, Object id) {
        this.jsonrpc = "2.0";
        this.method = method;
        this.params = params;
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }
}

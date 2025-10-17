package de.christophdick.tenga.mcp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonRpcResponse {

    private String jsonrpc = "2.0";
    private Object result;
    private JsonRpcError error;
    private Object id;

    public JsonRpcResponse() {
    }

    public JsonRpcResponse(Object result, Object id) {
        this.jsonrpc = "2.0";
        this.result = result;
        this.id = id;
    }

    public JsonRpcResponse(JsonRpcError error, Object id) {
        this.jsonrpc = "2.0";
        this.error = error;
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public JsonRpcError getError() {
        return error;
    }

    public void setError(JsonRpcError error) {
        this.error = error;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }
}

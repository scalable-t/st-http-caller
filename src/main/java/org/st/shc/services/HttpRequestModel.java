package org.st.shc.services;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author abomb4 2022-06-25
 */
@Data
public class HttpRequestModel {

    private HttpMethod method = HttpMethod.GET;

    private String url;

    private Map<String, List<String>> headers = new HashMap<>();

    private BodyType bodyType;

    private String body;

    public enum HttpMethod {
        GET,
        POST,
        DELETE,
        PUT,
    }

    public enum BodyType {
        RAW
    }
}

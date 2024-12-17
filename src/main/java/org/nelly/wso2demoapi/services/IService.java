package org.nelly.wso2demoapi.services;

import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.List;

public interface IService {
    default void requires(List<String> fields, JSONObject request, String errorMessage) {
        List<String> missingFields = new ArrayList<>();

        for (String field : fields) {
            if (!request.containsKey(field) || request.get(field) == null || request.getString(field).isEmpty()) {
                missingFields.add(field.replaceAll("_", " "));
            }
        }

        if (!missingFields.isEmpty()) {
            String missingFieldsMessage = "Fields : (" + String.join(", ", missingFields) + ") are required but not provided.";
            throw new IllegalArgumentException(errorMessage + " " + missingFieldsMessage);
        }
    }

    default void requires(List<String> fields, JSONObject request) {
        requires(fields, request, "");
    }
}

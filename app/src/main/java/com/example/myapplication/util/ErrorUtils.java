package com.example.myapplication.util;


import com.example.myapplication.dto.ErrorResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ErrorUtils {
    private static final Gson gson = new Gson();

    private static final Map<Integer, String> HTTP_ERROR_MESSAGES = new HashMap<>() {
        {
            put(400, "Invalid request");
            put(401, "Authentication failed");
            put(403, "You don't have permission to access this resource");
            put(404, "Resource not found");
            put(500, "Server error");

        }
    };

    public static ErrorResponse parseError(ResponseBody errorBody) {
        try {
            if (errorBody == null) {
                return createDefaultError("No error body");
            }


            String errorJson = errorBody.string();
            ErrorResponse errorResponse = gson.fromJson(errorJson, ErrorResponse.class);

            if (errorResponse.getError() == null) {
                errorResponse.setError("Unknown error");
            }

            return errorResponse;
        } catch (IOException e) {
            return createDefaultError("Error reading error body: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            return createDefaultError("Error parsing error response: " + e.getMessage());
        }
    }


    private static ErrorResponse createDefaultError(String errorMessage) {
        return new ErrorResponse(errorMessage);
    }


    public static String getFormattedErrorMessage(ErrorResponse errorResponse) {
        if (errorResponse == null) {
            return "Unknown error occurred";
        }

        String errorMessage = errorResponse.getError();
        if (errorResponse.getError() != null && !errorResponse.getError().isEmpty()) {
            errorMessage += ": " + errorResponse.getError();
        }

        return errorMessage;
    }


    public static String getUserFriendlyErrorMessage(int statusCode) {
        return HTTP_ERROR_MESSAGES.getOrDefault(statusCode, "An unexpected error occurred");
    }


    public static String handleThrowable(Throwable throwable) {
        if (throwable instanceof IOException) {
            return "Network error. Please check your internet connection and try again.";
        } else {
            return "An unexpected error occurred: " + throwable.getMessage();
        }
    }


    public static <T> ErrorResponse processError(Response<T> response) {
        ErrorResponse errorResponse = parseError(response.errorBody());

        if (errorResponse.getError() == null || errorResponse.getError().isEmpty()) {
            errorResponse.setError(getUserFriendlyErrorMessage(response.code()));
        }

        return errorResponse;
    }
}
package com.herminio.superduper.pancake.validator;

import java.util.ArrayList;
import java.util.List;

public class StartupValidator {

    private static List<String> requiredEnvVars = List.of(
        "AZURE_EMAIL_CONNECTION_STRING",
        "AZURE_EMAIL_KEY"
    );

    private static List<String> errors = new ArrayList<String>();

    public static void validateEnvironmentVariables() {

        System.out.println("### STARTUP VALIDATION INITIATED ###.");

        checkRequiredEnvVars(requiredEnvVars, errors);

        if (!errors.isEmpty()) {
            System.out.println("STARTUP VALIDATION FAILED due to the following errors:");
            for (String error : errors) {
                System.out.println(" - " + error);
            }
            throw new IllegalStateException("STARTUP VALIDATION FAILED. See errors above.");
        }

        System.out.println("### STARTUP VALIDATION COMPLETED ###.");

    }

    private static void checkRequiredEnvVars(List<String> requiredEnvVars, List<String> errors) {

        for (String varName : requiredEnvVars) {

            String varValue = System.getenv(varName);

            if (varValue == null || varValue.isEmpty()) {
                errors.add("Environment variable " + varName + " is not set.");
            }
        }
    }
}

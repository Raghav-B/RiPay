package com.example.ripay;

import org.json.JSONException;
import org.json.JSONObject;

public class Constants {
    public static final String USER_LIST_COL = "UserList";

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_ACCOUNT_ID = "account_id";

    private static final String newClientTemplateString = "{\n" +
            "    \"client\": {\n" +
            "        \"firstName\": \"\",\n" +
            "        \"lastName\": \"\",\n" +
            "        \"preferredLanguage\": \"ENGLISH\",\n" +
            "        \"notes\": \"Enjoys playing RPG\",\n" +
            "        \"assignedBranchKey\": \"8a8e878e71c7a4d70171ca6401ba1256\"\n" +
            "    },\n" +
            "    \"idDocuments\": [\n" +
            "        {\n" +
            "            \"identificationDocumentTemplateKey\": \"8a8e867271bd280c0171bf7e4ec71b01\",\n" +
            "            \"issuingAuthority\": \"Immigration Authority of Singapore\",\n" +
            "            \"documentType\": \"NRIC/Passport Number\",\n" +
            "            \"validUntil\": \"\",\n" +
            "            \"documentId\": \"\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"addresses\": [],\n" +
            "    \"customInformation\": []\n" +
            "}";

    private static final String newCurrentAccountTemplateString = "{\n" +
            "    \"savingsAccount\": {\n" +
            "        \"name\": \"Digital Account\",\n" +
            "        \"accountHolderType\": \"CLIENT\",\n" +
            "        \"accountHolderKey\": \"\",\n" +
            "        \"accountState\": \"APPROVED\",\n" +
            "        \"productTypeKey\": \"8a8e878471bf59cf0171bf6979700440\",\n" +
            "        \"accountType\": \"CURRENT_ACCOUNT\",\n" +
            "        \"currencyCode\": \"SGD\",\n" +
            "        \"allowOverdraft\": \"true\",\n" +
            "        \"overdraftLimit\": \"100\",\n" +
            "        \"overdraftInterestSettings\": {\n" +
            "            \"interestRate\": 5\n" +
            "        },\n" +
            "            \"interestSettings\": {\n" +
            "        \"interestRate\": \"1.01\"\n" +
            "    }\n" +
            "    }\n" +
            "\n" +
            "}";

    public static JSONObject createNewClientTemplate() {
        JSONObject newClientTemplate = null;
        try {
            newClientTemplate = new JSONObject(newClientTemplateString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newClientTemplate;
    }

    public static JSONObject createNewCurrentAccountTemplate() {
        JSONObject newCurrentAccountTemplate = null;
        try {
            newCurrentAccountTemplate = new JSONObject(newCurrentAccountTemplateString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newCurrentAccountTemplate;
    }
}

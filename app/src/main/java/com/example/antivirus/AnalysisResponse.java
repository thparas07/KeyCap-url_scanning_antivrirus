package com.example.antivirus;

public class AnalysisResponse {
    private final Data data;

    public AnalysisResponse(Data data) {
        this.data = data;
    }

    // Getter for the data object
    public Data getData() {
        return data;
    }

    // Inner Data class
    public static class Data {
        private final String id;

        public Data(String id) {
            this.id = id;
        }

        // Getter for the ID
        public String getId() {
            return id;
        }
    }
}

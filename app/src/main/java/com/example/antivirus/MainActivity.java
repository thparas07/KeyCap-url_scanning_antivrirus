package com.example.antivirus;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "9912093f432269753b6a58dbbc62171930c2af996138e9366424c2e4d1e49ff1";  // Replace with your VirusTotal API Key
    private static final String URL_SCAN_URL = "https://www.virustotal.com/api/v3/urls";

    private EditText urlInput;
    private TextView resultsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlInput = findViewById(R.id.urlInput);
        Button scanUrlButton = findViewById(R.id.scanUrlButton);
        resultsText = findViewById(R.id.resultsText);

        scanUrlButton.setOnClickListener(v -> {
            String urlToScan = urlInput.getText().toString().trim();
            if (!urlToScan.isEmpty()) {
                new ScanUrlTask().execute(urlToScan);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a valid URL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class ScanUrlTask extends AsyncTask<String, Void, SpannableStringBuilder> {
        @Override
        protected SpannableStringBuilder doInBackground(String... urls) {
            String urlToScan = urls[0];
            return scanUrl(urlToScan);
        }

        @Override
        protected void onPostExecute(SpannableStringBuilder result) {
            resultsText.setText(result);
        }
    }

    private SpannableStringBuilder scanUrl(String urlToScan) {
        try {
            URL url = new URL(URL_SCAN_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("x-apikey", API_KEY);
            connection.setDoOutput(true);

            String urlData = "url=" + urlToScan;
            OutputStream os = connection.getOutputStream();
            os.write(urlData.getBytes());
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject responseObject = new Gson().fromJson(response.toString(), JsonObject.class);
            String scanId = responseObject.getAsJsonObject("data").get("id").getAsString();
            return fetchAnalysis(scanId);
        } catch (Exception e) {
            e.printStackTrace();
            SpannableStringBuilder errorResult = new SpannableStringBuilder();
            errorResult.append("Error submitting URL: ").append(e.getMessage());
            return errorResult;
        }
    }

    private SpannableStringBuilder fetchAnalysis(String scanId) {
        try {
            String analysisUrl = "https://www.virustotal.com/api/v3/analyses/" + scanId;
            URL url = new URL(analysisUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("x-apikey", API_KEY);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject result = new Gson().fromJson(response.toString(), JsonObject.class);
            JsonObject attributes = result.getAsJsonObject("data").getAsJsonObject("attributes");
            if ("completed".equals(attributes.get("status").getAsString())) {
                return formatScanResults(attributes.getAsJsonObject("results"));
            } else {
                SpannableStringBuilder inProgress = new SpannableStringBuilder();
                inProgress.append("Analysis is still in progress.");
                return inProgress;
            }
        } catch (Exception e) {
            e.printStackTrace();
            SpannableStringBuilder errorResult = new SpannableStringBuilder();
            errorResult.append("Error fetching analysis: ").append(e.getMessage());
            return errorResult;
        }
    }

    private SpannableStringBuilder formatScanResults(JsonObject results) {
        SpannableStringBuilder spannableResults = new SpannableStringBuilder();
        spannableResults.append("Scan Results:\n\n");

        for (String engine : results.keySet()) {
            JsonObject engineResult = results.getAsJsonObject(engine);
            String scanResult = engineResult.has("result") ? engineResult.get("result").getAsString() : "No result available";

            spannableResults.append(engine).append(": ");

            int color;
            if ("clean".equalsIgnoreCase(scanResult)) {
                color = ContextCompat.getColor(MainActivity.this, android.R.color.holo_green_light);
            } else if ("unrated".equalsIgnoreCase(scanResult)) {
                color = ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_light);
            } else if ("malicious".equalsIgnoreCase(scanResult) || "phishing".equalsIgnoreCase(scanResult)) {
                color = ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_light);
            } else {
                color = ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray);
            }

            spannableResults.append(scanResult, new BackgroundColorSpan(color), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableResults.append("\n");


        }

        return spannableResults;
    }
}

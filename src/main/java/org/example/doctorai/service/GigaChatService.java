package org.example.doctorai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.doctorai.exception.GigaChataException;
import org.example.doctorai.model.entity.Chat;
import org.example.doctorai.model.entity.Message;
import org.example.doctorai.model.request.AnswerGigaChatMessageRequest;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GigaChatService {

    private final CustomUserDetailsService customUserDetailsService;
    private final String secretToken = "OWRhOTU4ZTctODcyYi00NDA1LWE2NGEtYTM1M2EzMGFmZTIwOjkyNTZiMjJhLWM0ZDgtNGY5Ny1iMGMxLTA5NjJkNjJjNzExZg==";
    private String accessToken;
    private final String urlChat = "https://gigachat.devices.sberbank.ru/api/v1/chat/completions";

//    private final Map<String , ChatRequest> chatRequests;




    /**
     * Обновление токена какждые 25 минут
     */
    @Scheduled(cron = "0 */1 * * * *")
    private void updateToken() {
        try {
            String url = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth";
            String authorizationHeader = "Bearer " + secretToken;
            String formData = "scope=GIGACHAT_API_PERS";


            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("RqUID", "6f0b1291-c7f3-43c6-bb2e-9f3efb2dc98e")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", authorizationHeader)
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonObject = new JSONObject(response.body());

            accessToken = jsonObject.getString("access_token");
            System.out.println(accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param rawBody
     * @return
     */
    public Message requestDoctorOrthopedist(Chat rawBody) {
        try {
            String authorizationHeader = "Bearer " + accessToken;
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlChat))
                    .header("Authorization", authorizationHeader)
                    .header("Content-Type", "application/json")
                    .header("X-Client-ID", customUserDetailsService.getCurrentUser().id.toString())
                    .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(rawBody)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return new ObjectMapper().readValue(response.body(), AnswerGigaChatMessageRequest.class).getChoices().get(0).getMessage();
            } else {
                throw new GigaChataException("Извините, доктор устал.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}


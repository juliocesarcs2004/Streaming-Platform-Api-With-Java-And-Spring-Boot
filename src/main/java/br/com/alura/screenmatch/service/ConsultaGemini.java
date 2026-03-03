package br.com.alura.screenmatch.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;

public class ConsultaGemini {

    private static final String GEMINI_API_KEY = "(Colocar a api_key aqui)";

    public static String obterTraducao(String texto) {

        Client client = Client.builder()
                .apiKey(GEMINI_API_KEY)
                .build();

        GenerateContentConfig config = GenerateContentConfig.builder()
                .candidateCount(1)
                .maxOutputTokens(1000)
                .build();

        GenerateContentResponse response = client
                .models
                .generateContent("gemini-2.0-flash-001", "Traduza para o português brasileiro o texto: " + texto, config);

        var resposta = response.text();
        return resposta.trim();
    }
}

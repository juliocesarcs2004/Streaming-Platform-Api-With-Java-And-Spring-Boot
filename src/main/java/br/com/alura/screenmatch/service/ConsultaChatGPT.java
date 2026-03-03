package br.com.alura.screenmatch.service;


import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

public class ConsultaChatGPT {

    public static String obterTraducao(String texto) {

        OpenAIClient client = OpenAIOkHttpClient.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();

        ChatCompletionCreateParams params =
                ChatCompletionCreateParams.builder()
                        .model("gpt-4o-mini")
                        .addUserMessage("Traduza para o português o texto: " + texto)
                        .maxTokens(1000)
                        .temperature(0.7)
                        .build();

        ChatCompletion completion = client.chat()
                .completions()
                .create(params);

        return completion.choices()
                .get(0)
                .message()
                .content()
                .get();

    }
}




//public class ConsultaChatGPT {
//    public static String obterTraducao(String texto) {
//        OpenAiService service = new OpenAiService("OPENAI_API_KEY");
//
//        CompletionRequest requisicao = CompletionRequest.builder()
//                .model("text-davinci-003")
//                .prompt("traduza para o português o texto: " + texto)
//                .maxTokens(1000)
//                .temperature(0.7)
//                .build();
//
//
//        var resposta = service.createCompletion(requisicao);
//        return resposta.getChoices().get(0).getText();
//    }
//}


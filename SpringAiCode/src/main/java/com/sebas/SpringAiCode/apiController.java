package com.sebas.SpringAiCode;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class apiController {

    private final EmbeddingModel embeddingModel;
    private OpenAiChatModel chatModel;

    private ChatClient chatClient;



    ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

    public apiController(OpenAiChatModel chatModel, EmbeddingModel embeddingModel) {
        this.chatModel = chatModel;
        this.chatClient = ChatClient.create(chatModel);
        this.embeddingModel = embeddingModel;
    }

//    public  OpenAIController(ChatClient.builder builder)
//    {
//        this.chatClient = builder
//    }

    @GetMapping("/api/{msg}")
    public String chat(@PathVariable String msg){

        String response = chatModel.call(msg);
        return "chat says" + response;
    }

    @GetMapping("/api2/{msg}")
    public ResponseEntity<String> chatStream(@PathVariable String msg){

        String response = chatClient
                .prompt(msg)
                .call()
                .content();

        return ResponseEntity.ok(response);
    }

    @PostMapping("api/recommend")
    public  String recomend(@RequestParam  String type, @RequestParam String lang, @RequestParam String year){

        String tempt = """
                    I want to waht a {type} movie tonight with good rating,
                    looging for movies arouds this {year}.
                    The language im looking forr is{lang}.
                    Suggest one specific movie and tell me thhe cast and length of the movie.
                """;

        PromptTemplate promptTemplate = new PromptTemplate("");

        Prompt prompt = promptTemplate.create(Map.of("type", type, "year", year, "lang", lang));


        String response = chatClient
                .prompt(prompt)
                .call()
                .content();

        return response;
    }


    @PostMapping("/api/embedding")
    public float[] embedding(@RequestParam String text){

        return  embeddingModel.embed(text);
    }
}

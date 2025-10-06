package com.sherlockgy.springai.quickstart;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;

@SpringBootTest
public class TestOllama {
    @Test
    public void testOllama(@Autowired OllamaChatModel ollamaChatModel) {
        //OllamaOptions.builder().build();
        String response = ollamaChatModel.call("你好，你是谁？");
        System.out.println(response);
    }

    /**
     * 多模态测试
     */
    @Test
    public void testOllamaMutiModality(@Autowired OllamaChatModel ollamaChatModel) {
        ClassPathResource imgResource = new ClassPathResource("files/img.png");

        // 指定模型为 gemma3:4b
        OllamaOptions ollamaChatOptions = OllamaOptions.builder().model("gemma3:4b").build();

        Media media = new Media(MimeTypeUtils.IMAGE_PNG, imgResource);

        ChatResponse response = ollamaChatModel.call(new Prompt(
                UserMessage.builder().media(media)
                                     .text("这是什么图片？").build(),
                ollamaChatOptions
        ));
        System.out.println(response.getResult().getOutput());
    }
}

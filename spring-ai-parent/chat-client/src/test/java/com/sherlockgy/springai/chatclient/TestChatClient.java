package com.sherlockgy.springai.chatclient;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestChatClient {
    @Test
    public void testChatClient(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder.build();
        String content = chatClient.prompt()
                .user("你好")
                .call()
                .content();
        System.out.println(content);
    }

    /**
     * 指定 DeepSeek 模型的 ChatClient 测试
     * 当存在多个模型时，可以通过这种方式指定使用哪个模型
     */
    @Test
    public void testChatClientWithDeepSeek(@Autowired DeepSeekChatModel deepSeekChatModel) {
        ChatClient chatClient = ChatClient.builder(deepSeekChatModel).build();
        String content = chatClient.prompt()
                .user("你好")
                .call()
                .content();
        System.out.println(content);
    }

    @Test
    public void testChatClientStream(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder.build();
        Flux<String> content = chatClient.prompt()
                .user("你好")
                .stream()
                .content();
        content.toIterable().forEach(s -> {
            if (s != null) {
                System.out.print(s);
            }
        });
    }
}

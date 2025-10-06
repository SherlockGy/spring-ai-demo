package com.sherlockgy.springai.chatclient;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
public class TestMemory {
    /**
     * 测试记忆功能
     */
    @Test
    public void testMemory(@Autowired OllamaChatModel ollamaChatModel) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        String conversationId = "test-memory-1";

        // 第一轮对话
        UserMessage userMessage1 = new UserMessage("我叫张三");
        chatMemory.add(conversationId, userMessage1);
        ChatResponse response1 = ollamaChatModel.call(new Prompt(chatMemory.get(conversationId)));
        chatMemory.add(conversationId, response1.getResult().getOutput());

        // 第二轮对话
        UserMessage userMessage2 = new UserMessage("我叫什么？");
        chatMemory.add(conversationId, userMessage2);
        ChatResponse response2 = ollamaChatModel.call(new Prompt(chatMemory.get(conversationId)));
        chatMemory.add(conversationId, response2.getResult().getOutput());

        System.out.println(response2.getResult().getOutput());
    }

    /**
     * 测试记忆功能，使用 Advisor 方式
     */
    @Test
    public void testMemoryAdvisor(@Autowired OllamaChatModel ollamaChatModel,
                                  @Autowired ChatMemory chatMemory) {
        OllamaOptions options = OllamaOptions.builder().model("deepseek-r1:8b").build();

        ChatClient chatClient = ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();

        String content = chatClient.prompt()
                .options(options)
                .user("你是谁？")
                .call()
                .content();
        System.out.println(content);
        System.out.println("----------------------------");

        content = chatClient.prompt()
                .user("我刚刚问你的问题是什么？")
                .call()
                .content();
        System.out.println(content);
    }

    @TestConfiguration
    static class Config {
        @Bean
        ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
            // 默认的唯一标识是 default
            return MessageWindowChatMemory
                    .builder()
                    .maxMessages(10) // 每个对话最多记忆 10 条消息
                    .chatMemoryRepository(chatMemoryRepository)
                    .build();
        }
    }

    @Test
    public void testChatOptions(@Autowired OllamaChatModel ollamaChatModel,
                                @Autowired ChatMemory chatMemory) {
        OllamaOptions options = OllamaOptions.builder().model("deepseek-r1:8b").build();

        ChatClient chatClient = ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();

        String content = chatClient.prompt()
                .options(options)
                .user("你是谁？")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1")) // 指定对话 ID 为 1
                .call()
                .content();
        System.out.println(content);
        System.out.println("----------------------------");

        content = chatClient.prompt()
                .user("你多大了？")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "2")) // 指定对话 ID 为 1
                .call()
                .content();

        content = chatClient.prompt()
                .user("我刚刚问你的问题是什么？")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1")) // 指定对话 ID 为 2
                .call()
                .content();
        System.out.println(content);
    }
}

package com.sherlockgy.springai.chatclient;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestAdvisor {
    @Test
    public void testChatClient(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                                 new SafeGuardAdvisor(List.of("敏感词"), "触发敏感词", 0)) // 添加默认的 advisor
                .build();
        String content = chatClient.prompt()
                .user("你好，敏感词")
                .call()
                .content();
        System.out.println(content);
    }

    /**
     * 测试 ReReadingAdvisor
     */
    @Test
    public void testReReadingAdvisor(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                                 new ReReadingAdvisor())
                .build();
        String content = chatClient.prompt()
                .user("你好")
                .call()
                .content();
        System.out.println(content);
    }
}

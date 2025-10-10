package com.sherlockgy.springai.chatclient;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
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

    /**
     * 指定 ollama 模型测试
     */
    @Test
    public void testOllamaOrderModel(@Autowired OllamaChatModel ollamaChatModel) {
        // 指定模型为 qwen2.5:0.5b
        OllamaOptions ollamaChatOptions = OllamaOptions.builder().model("qwen2.5:0.5b").build();
        String response = ChatClient.builder(ollamaChatModel)
                .build()
                .prompt("你好，你是谁？")
                .options(ollamaChatOptions)
                .call().content();
        System.out.println(response);
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

    /**
     * chatClient 设置默认的 system 提示，每次调用 prompt 时会自动带上
     * 可以在 prompt 里覆盖默认的 system 提示
     */
    @Test
    public void testSystemPrompt(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder
                .defaultSystem("""
                        # 角色说明
                        你是一名资深法律顾问
                        
                        # 回复格式
                        1. 问题分析
                        2. 法律依据
                        3. 梳理和建议
                        """)
                .build();
        String content = chatClient.prompt()
                //.system("") // 这里可以覆盖默认的 system 提示
                .user("你好")
                .call()
                .content();
        System.out.println(content);
    }

    /**
     * system 提示支持参数化
     */
    @Test
    public void testSystemPromptWithTemplate(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder
                .defaultSystem("""
                        # 角色说明
                        你是一名资深法律顾问，回答时需要结合用户的个人信息
                        
                        # 回复格式
                        1. 问题分析
                        2. 法律依据
                        3. 梳理和建议
                        
                        当前用户信息：姓名：{name}，年龄：{age}
                        """)
                .build();
        String content = chatClient.prompt()
                .system(p -> p.param("name", "张三").param("age", 30)) // 使用参数化的 system 提示
                .user("你好")
                .call()
                .content();
        System.out.println(content);
    }

    /**
     * 用户提示词参数化
     */
    @Test
    public void testUserPromptWithTemplate(@Autowired ChatClient.Builder chatClientBuilder) {
        ChatClient chatClient = chatClientBuilder.build();
        String content = chatClient.prompt()
                .user(p -> p.text("""
                        # 角色说明
                        你是一名资深法律顾问，回答时需要结合用户的个人信息
                        
                        # 回复格式
                        1. 问题分析
                        2. 法律依据
                        3. 梳理和建议
                        
                        当前用户信息：姓名：{name}，年龄：{age}
                        """).param("name", "张三").param("age", 30)) // 使用参数化的 user 提示
                .call()
                .content();
        System.out.println(content);
    }

    /**
     * 从配置文件加载默认的 system 提示
     */
    @Test
    public void testSystemPromptFromFile(@Autowired ChatClient.Builder chatClientBuilder,
                                         @Value("classpath:/prompts/prompt.st") Resource resource) {
        ChatClient chatClient = chatClientBuilder
                .defaultSystem(resource)
                .build();
        String content = chatClient.prompt()
                .system(p -> p.param("name", "张三").param("age", 30)) // 使用参数化的 system 提示
                .user("你好")
                .call()
                .content();
        System.out.println(content);
    }
}

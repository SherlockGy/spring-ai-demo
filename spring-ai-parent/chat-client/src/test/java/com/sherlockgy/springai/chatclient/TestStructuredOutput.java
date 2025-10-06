package com.sherlockgy.springai.chatclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestStructuredOutput {
    ChatClient chatClient;

    @BeforeEach
    public void init(@Autowired OllamaChatModel ollamaChatModel) {
        chatClient = ChatClient.builder(ollamaChatModel).build();
    }

    /**
     * 结构化输出测试
     */
    @Test
    public void testStructuredOutput() {
        OllamaOptions ollamaChatOptions = OllamaOptions.builder().model("qwen2.5:7b").build();

        Boolean isComplain = chatClient
                .prompt()
                .options(ollamaChatOptions)
                .system("""
                        请判断用户是否表达了投诉意图？
                        只能使用 true 或 false 来回答问题，不要输出多余内容。
                        """)
                .user("你们家快递迟迟不到，我要退货")
                .call()
                .entity(Boolean.class); // 这里不是 content 方法了

        if (Boolean.TRUE.equals(isComplain)) {
            System.out.println("用户表达了投诉意图");
        } else {
            System.out.println("用户没有表达投诉意图");
        }
    }

    record Address (
        String name,     // 收件人姓名
        String phone,    // 收件人电话
        String province, // 省份
        String city,     // 城市
        String district, // 区县
        String detail    // 详细地址
    ){}

    /**
     * 结构化输出测试，输出为实体类
     */
    @Test
    public void testEntityOut() {
        OllamaOptions ollamaChatOptions = OllamaOptions.builder().model("qwen2.5:7b").build();

        Address address = chatClient
                .prompt()
                .options(ollamaChatOptions)
                .system("""
                        请从下面这条文本中提取收货信息
                        """)
                .user("收货人：张三，电话：12322121212，地址：北京市朝阳区望京街道某某小区12号楼3单元502室")
                .call()
                .entity(Address.class);
        System.out.println(address);
    }
}

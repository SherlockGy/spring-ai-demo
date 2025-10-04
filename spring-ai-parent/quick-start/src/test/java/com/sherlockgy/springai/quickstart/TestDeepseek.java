package com.sherlockgy.springai.quickstart;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class TestDeepseek {
    @Test
    public void testDeepseek(@Autowired DeepSeekChatModel chatModel) {
        String content = chatModel.call("你好");
        System.out.println(content);
    }

    @Test
    public void testDeepseekStream(@Autowired DeepSeekChatModel chatModel) {
        Flux<String> stream = chatModel.stream("你好");
        stream.toIterable().forEach(chatResponse -> {
            if (chatResponse != null) {
                System.out.print(chatResponse);
            }
        });
    }

    @Test
    public void testDeepseekChatOptions(@Autowired DeepSeekChatModel chatModel) {
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model("deepseek-chat")
                //.temperature(0.7) // 设置温度参数
                //.topP(0.9)        // 设置top_p参数
                .maxTokens(150)   // 设置最大token数
                //.stop(List.of(new String[]{"\n"})) // 设置停止词，遇到停止词时生成将停止，例如：换行符、最后总结一下
                .build();
        ChatResponse res = chatModel.call(new Prompt("请写一句诗描述清晨。", options));
        System.out.println(res.getResult().getOutput().getText());
    }

    @Test
    public void testDeepseekR1(@Autowired DeepSeekChatModel chatModel) {
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model("deepseek-reasoner")
                .build();
        ChatResponse res = chatModel.call(new Prompt("你好，你是谁？", options));

        DeepSeekAssistantMessage message = (DeepSeekAssistantMessage) res.getResult().getOutput();
        System.out.println(message.getReasoningContent()); // 推理过程
        System.out.println("--------------------");
        System.out.println(message.getText()); // 最终回答
    }

    @Test
    public void testDeepseekR1Stream(@Autowired DeepSeekChatModel chatModel) {
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model("deepseek-reasoner")
                .build();
        Flux<ChatResponse> stream = chatModel.stream(new Prompt("你好，你是谁？", options));

        stream.toIterable().forEach(chatResponse -> {
            DeepSeekAssistantMessage message = (DeepSeekAssistantMessage) chatResponse.getResult().getOutput();
            if (message.getReasoningContent() != null) {
                System.out.print(message.getReasoningContent()); // 推理过程
            }
        });

        System.out.println();
        System.out.println("--------------------");

        stream.toIterable().forEach(chatResponse -> {
            DeepSeekAssistantMessage message = (DeepSeekAssistantMessage) chatResponse.getResult().getOutput();
            if (message.getText() != null) {
                System.out.print(message.getText()); // 最终回答
            }
        });
    }
}

package com.sherlockgy.springai.chatclient;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * 重新阅读 advisor
 */
public class ReReadingAdvisor implements BaseAdvisor {
    private static final String DEFAULT_USER_TEXT_ADVISOR = """
            {re2_input_query}
            请你重新阅读以下内容：{re2_input_query}
            """;

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        String contents = chatClientRequest.prompt().getContents();

        String re2InputQuery = PromptTemplate.builder()
                                             .template(DEFAULT_USER_TEXT_ADVISOR)
                                             .build()
                                             .render(Map.of("re2_input_query", contents));

        ChatClientRequest request = chatClientRequest.mutate()
                                                     .prompt(Prompt.builder()
                                                     .content(re2InputQuery)
                                                     .build())
                                                     .build();
        return request;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse ;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

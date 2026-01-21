package org.roni.ronitrouble.component.factory;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.model.OpenAIChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgentFactory {

    private final OpenAIChatModel openAIChatModel;
    private final String systemPrompt = "123";

    public ReActAgent buildReActAgent() {
        return ReActAgent.builder()
                .name("肉包包智能助手")
                .model(openAIChatModel)
                .sysPrompt(systemPrompt)
                .memory(new InMemoryMemory())
                .build();
    }

}

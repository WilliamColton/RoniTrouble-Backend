package org.roni.ronitrouble.component.factory;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.model.OpenAIChatModel;
import io.agentscope.core.tool.Toolkit;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.service.ToolService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgentFactory {

    private final OpenAIChatModel openAIChatModel;
    private final ToolService toolService;

    public ReActAgent buildReActAgent(String systemPrompt) {
        var toolKit = new Toolkit();
        toolKit.registerTool(toolService);
        return ReActAgent.builder()
                .name("肉包包智能助手")
                .model(openAIChatModel)
                .sysPrompt(systemPrompt)
                .memory(new InMemoryMemory())
                .toolkit(toolKit)
                .build();
    }

}

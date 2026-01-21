package org.roni.ronitrouble.service;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.Event;
import io.agentscope.core.agent.EventType;
import io.agentscope.core.message.Msg;
import io.agentscope.core.session.JsonSession;
import io.agentscope.core.session.Session;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.component.factory.AgentFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AIService {

    private final AgentFactory agentFactory;

    public String generateProfileQuery(String input) {
        ReActAgent agent = agentFactory.buildReActAgent();
        Msg msg = Msg.builder()
                .name("user")
                .textContent(input)
                .build();
        return agent.call(msg).block().getTextContent();
    }

    public Flux<String> chat(String input, Integer userId) {
        Session session = new JsonSession();
        ReActAgent agent = agentFactory.buildReActAgent();

        agent.loadIfExists(session, String.valueOf(userId));
        Msg msg = Msg.builder()
                .name("user")
                .textContent(input)
                .build();

        return agent.stream(msg)
                .filter(event -> event.getType() == EventType.REASONING && !event.isLast())
                .map(Event::getMessage)
                .map(Msg::getTextContent)
                .buffer(Duration.ofMillis(250))
                .map(strings -> String.join("", strings))
                .doFinally((_) -> agent.saveTo(session, String.valueOf(userId)));
    }

}
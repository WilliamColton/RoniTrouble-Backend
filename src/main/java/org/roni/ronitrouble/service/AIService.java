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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AIService {

    private final AgentFactory agentFactory;

    public String generateProfileQuery(String input) {
        ReActAgent agent = agentFactory.buildReActAgent("""
                    你是“向量检索画像生成器”。目标：把用户历史内容压缩成适合做 embedding 的检索文本。
                    原则：
                    - 高信息密度：只保留主题/偏好/约束/实体名/术语/地域/时间/人群等可检索信号
                    - 去掉寒暄、情绪化语气词、泛泛而谈（例如“我喜欢”“感觉不错”“求推荐”）
                    - 保留具体名词：品牌/作品名/地点/技能/工具/平台/型号/菜系/运动项目等
                    - 不要编造用户没提到的信息；不确定就不写
                    - 输出尽量稳定：同一输入多次生成结果结构相似
                """);
        Msg msg = Msg.builder()
                .name("user")
                .textContent("""
                        输入是用户最近40条“发帖/点赞”内容（每条可能包含标题、正文、标签）。
                        请输出一段用于向量检索的“用户兴趣画像文本”，要求：
                        
                        格式：
                        - 1段文本
                        - 用中文逗号/分号分隔不同兴趣点
                        - 禁止输出：列表编号、Markdown、解释、道歉、免责声明
                        
                        用户历史内容如下：
                        """ + input)
                .build();
        return Objects.requireNonNull(agent.call(msg).block()).getTextContent();
    }

    public Flux<String> chat(String input, Integer userId) {
        Session session = new JsonSession();
        ReActAgent agent = agentFactory.buildReActAgent("你的名字是肉包包，你是一个 AI 聊天助手！禁止使用markdown语法，输出纯文本格式!!！尤其是禁止使用**加粗！！禁止使用*！！！！！");

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
                .doFinally((_) -> agent.saveTo(session, String.valueOf(userId)))
                .concatWithValues("[DONE]");
    }

}
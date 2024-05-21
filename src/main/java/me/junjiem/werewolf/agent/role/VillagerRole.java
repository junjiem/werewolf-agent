package me.junjiem.werewolf.agent.role;

import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.junjiem.werewolf.agent.bean.SpeakResult;
import me.junjiem.werewolf.agent.bean.TestamentResult;
import me.junjiem.werewolf.agent.bean.VoteResult;
import me.junjiem.werewolf.agent.util.ChatLanguageModelUtil;

import java.util.List;

/**
 * 村民Role
 *
 * @Author JunjieM
 * @Date 2024/4/9
 */
@Slf4j
public class VillagerRole extends AbstractRole implements GoodRole {

    @NonNull
    private final VillagerAssistant assistant;

    public VillagerRole(@NonNull String service, @NonNull String apiKey, String modelName, Double temperature) {
        super(service, apiKey, modelName, temperature);
        this.assistant = AiServices.create(VillagerAssistant.class, chatLanguageModel);
    }

    public String speak(int id, int index, String gameInformation) {
        String answer = assistant.speak(id, index, gameInformation);
        log.info(answer);
        SpeakResult result = ChatLanguageModelUtil.jsonAnswer2Object(answer, SpeakResult.class);
        log.info("发言结果：{}", result);
        return result.getMySpeech();
    }

    public int vote(int id, String gameInformation, List<Integer> voteIds) {
        String answer = assistant.vote(id, gameInformation, voteIds);
        log.info(answer);
        VoteResult result = ChatLanguageModelUtil.jsonAnswer2Object(answer, VoteResult.class);
        log.info("投票结果：{}", result);
        return result.getVoteId()!=null?result.getVoteId():-1;
    }

    public String testament(int id, String gameInformation) {
        String answer = assistant.testament(id, gameInformation);
        log.info(answer);
        TestamentResult result = ChatLanguageModelUtil.jsonAnswer2Object(answer, TestamentResult.class);
        log.info("遗言结果：{}", result);
        return result.getLastWords();
    }

    /**
     * 村民Assistant
     */
    interface VillagerAssistant {
        /**
         * 发言
         *
         * @return
         */
        @SystemMessage(fromResource = "/prompt_template/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/prompt_template/villager-speak-user-prompt-template.txt")
        String speak(@V("id") int id, @V("index") int index, @V("gameInformation") String gameInformation);

        /**
         * 投票
         *
         * @return
         */
        @SystemMessage(fromResource = "/prompt_template/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/prompt_template/villager-vote-user-prompt-template.txt")
        String vote(@V("id") int id, @V("gameInformation") String gameInformation,
                    @V("voteIds") List<Integer> voteIds);

        /**
         * 遗言
         *
         * @return
         */
        @SystemMessage(fromResource = "/prompt_template/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/prompt_template/villager-testament-user-prompt-template.txt")
        String testament(@V("id") int id, @V("gameInformation") String gameInformation);
    }

}

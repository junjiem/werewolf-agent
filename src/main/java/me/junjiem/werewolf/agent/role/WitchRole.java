package me.junjiem.werewolf.agent.role;

import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.junjiem.werewolf.agent.bean.PoisonResult;
import me.junjiem.werewolf.agent.bean.SpeakResult;
import me.junjiem.werewolf.agent.bean.TestamentResult;
import me.junjiem.werewolf.agent.bean.VoteResult;
import me.junjiem.werewolf.agent.util.ChatLanguageModelUtil;

import java.util.List;

/**
 * 女巫Role
 *
 * @Author JunjieM
 * @Date 2024/4/9
 */
@Slf4j
public class WitchRole extends AbstractRole implements DeityRole {

    @NonNull
    private final WitchAssistant assistant;

    public WitchRole(@NonNull String service, @NonNull String apiKey, String modelName, Float temperature) {
        super(service, apiKey, modelName, temperature);
        this.assistant = AiServices.create(WitchAssistant.class, chatLanguageModel);
    }

    public String speak(int id, int index, String gameInformation, String skillInformation) {
        String answer = assistant.speak(id, index, gameInformation, skillInformation);
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

    public PoisonResult skill(int id, String gameInformation, int killId) {
        String answer = assistant.skill(id, gameInformation, killId);
        log.info(answer);
        PoisonResult result = ChatLanguageModelUtil.jsonAnswer2Object(answer, PoisonResult.class);
        log.info("使用毒药结果：{}", result);
        return result;
    }

    /**
     * 女巫Assistant
     */
    interface WitchAssistant {
        /**
         * 发言
         *
         * @return
         */
        @SystemMessage(fromResource = "/prompt_template/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/prompt_template/witch-speak-user-prompt-template.txt")
        String speak(@V("id") int id, @V("index") int index, @V("gameInformation") String gameInformation,
                     @V("skillInformation") String skillInformation);

        /**
         * 技能
         *
         * @return
         */
        @SystemMessage(fromResource = "/prompt_template/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/prompt_template/witch-skill-user-prompt-template.txt")
        String skill(@V("id") int id, @V("gameInformation") String gameInformation, @V("killId") int killId);

        /**
         * 投票
         *
         * @return
         */
        @SystemMessage(fromResource = "/prompt_template/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/prompt_template/witch-vote-user-prompt-template.txt")
        String vote(@V("id") int id, @V("gameInformation") String gameInformation,
                    @V("voteIds") List<Integer> voteIds);

        /**
         * 遗言
         *
         * @return
         */
        @SystemMessage(fromResource = "/prompt_template/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/prompt_template/witch-testament-user-prompt-template.txt")
        String testament(@V("id") int id, @V("gameInformation") String gameInformation);
    }
}

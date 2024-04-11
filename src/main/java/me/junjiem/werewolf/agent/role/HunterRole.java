package me.junjiem.werewolf.agent.role;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.junjiem.werewolf.agent.bean.ShotResult;
import me.junjiem.werewolf.agent.bean.SpeakResult;
import me.junjiem.werewolf.agent.bean.VoteResult;
import me.junjiem.werewolf.agent.util.ChatLanguageModelUtil;

import java.util.List;

/**
 * 猎人Role
 *
 * @Author JunjieM
 * @Date 2024/4/9
 */
@Slf4j
public class HunterRole implements DeityRole {

    @NonNull
    private final HunterAssistant assistant;

    @Builder
    public HunterRole(@NonNull String apiKey, String modelName, Float temperature) {
        ChatLanguageModel chatLanguageModel = ChatLanguageModelUtil.build(apiKey, modelName, temperature);
        this.assistant = AiServices.create(HunterAssistant.class, chatLanguageModel);
    }

    public String speak(int id, int index, String gameInformation) {
        String answer = assistant.speak(id, index, gameInformation);
        log.info(answer);
        SpeakResult result = ChatLanguageModelUtil.jsonAnswer2Object(answer, SpeakResult.class);
        log.info("发言结果：{}", result);
        return result.getMySpeech();
    }

    public Integer vote(int id, String gameInformation, List<Integer> voteIds) {
        String answer = assistant.vote(id, gameInformation, voteIds);
        log.info(answer);
        VoteResult result = ChatLanguageModelUtil.jsonAnswer2Object(answer, VoteResult.class);
        log.info("投票结果：{}", result);
        return result.getVoteId();
    }

    public ShotResult skill(int id, String gameInformation) {
        String answer = assistant.skill(id, gameInformation);
        log.info(answer);
        ShotResult result = ChatLanguageModelUtil.jsonAnswer2Object(answer, ShotResult.class);
        log.info("射击结果：{}", result);
        return result;
    }

    /**
     * 猎人Assistant
     */
    interface HunterAssistant {
        /**
         * 发言
         *
         * @return
         */
        @SystemMessage(fromResource = "/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/hunter-speak-user-prompt-template.txt")
        String speak(@V("id") int id, @V("index") int index,
                     @V("gameInformation") String gameInformation);

        /**
         * 技能
         *
         * @return
         */
        @SystemMessage(fromResource = "/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/hunter-skill-user-prompt-template.txt")
        String skill(@V("id") int id, @V("gameInformation") String gameInformation);

        /**
         * 投票
         *
         * @return
         */
        @SystemMessage(fromResource = "/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/hunter-vote-user-prompt-template.txt")
        String vote(@V("id") int id, @V("gameInformation") String gameInformation,
                    @V("voteIds") List<Integer> voteIds);
    }
}

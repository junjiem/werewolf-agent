package me.junjiem.werewolf.agent.role;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import lombok.Builder;
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
public class WitchRole implements DeityRole {

    private boolean elixirs = true; // 灵药是否还有
    private boolean poisons = true; // 毒药是否还有

    private int saveId = -1; // 昨晚救活的ID
    private int killId = -1; // 昨晚毒死的ID

    @NonNull
    private final WitchAssistant assistant;

    @Builder
    public WitchRole(@NonNull String apiKey, String modelName, Float temperature) {
        ChatLanguageModel chatLanguageModel = ChatLanguageModelUtil.build(apiKey, modelName, temperature);
        this.assistant = AiServices.create(WitchAssistant.class, chatLanguageModel);
    }

    public String speak(int id, int index, String gameInformation) {
        String skillInformation = "";
        if (saveId != -1) {
            skillInformation += "### 用药结果 ###\n昨晚救了" + saveId + "号。\n";
            this.saveId = -1;
        } else if (killId != -1) {
            skillInformation += "### 用药结果 ###\n昨晚毒死了" + killId + "号。\n";
            this.killId = -1;
        }
        String answer = assistant.speak(id, index, gameInformation, skillInformation);
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

    public String testament(int id, String gameInformation) {
        String answer = assistant.testament(id, gameInformation);
        log.info(answer);
        TestamentResult result = ChatLanguageModelUtil.jsonAnswer2Object(answer, TestamentResult.class);
        log.info("遗言结果：{}", result);
        return result.getLastWords();
    }

    public int skill(int id, String gameInformation) {
        if (!this.poisons) {
            return -1;
        }
        String answer = assistant.skill(id, gameInformation);
        log.info(answer);
        PoisonResult result = ChatLanguageModelUtil.jsonAnswer2Object(answer, PoisonResult.class);
        log.info("使用毒药结果：{}", result);
        if (result.isKill()) {
            this.killId = result.getKillId();
            this.poisons = false;
        }
        return result.isKill() ? result.getKillId() : -1;
    }

    public void save(int killId) {
        if (!this.elixirs) {
            return;
        }
        this.saveId = killId;
        this.elixirs = false;
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
        @SystemMessage(fromResource = "/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/witch-speak-user-prompt-template.txt")
        String speak(@V("id") int id, @V("index") int index, @V("gameInformation") String gameInformation,
                     @V("skillInformation") String skillInformation);

        /**
         * 技能
         *
         * @return
         */
        @SystemMessage(fromResource = "/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/witch-skill-user-prompt-template.txt")
        String skill(@V("id") int id, @V("gameInformation") String gameInformation);

        /**
         * 投票
         *
         * @return
         */
        @SystemMessage(fromResource = "/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/witch-vote-user-prompt-template.txt")
        String vote(@V("id") int id, @V("gameInformation") String gameInformation,
                    @V("voteIds") List<Integer> voteIds);

        /**
         * 遗言
         *
         * @return
         */
        @SystemMessage(fromResource = "/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/witch-testament-user-prompt-template.txt")
        String testament(@V("id") int id, @V("gameInformation") String gameInformation);
    }
}

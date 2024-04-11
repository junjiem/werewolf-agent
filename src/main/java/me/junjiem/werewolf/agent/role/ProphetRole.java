package me.junjiem.werewolf.agent.role;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.junjiem.werewolf.agent.player.Player;
import me.junjiem.werewolf.agent.bean.CheckResult;
import me.junjiem.werewolf.agent.bean.SpeakResult;
import me.junjiem.werewolf.agent.bean.TestamentResult;
import me.junjiem.werewolf.agent.bean.VoteResult;
import me.junjiem.werewolf.agent.util.ChatLanguageModelUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预言家Role
 *
 * @Author JunjieM
 * @Date 2024/4/9
 */
@Slf4j
public class ProphetRole implements DeityRole {

    private CheckResult checkResult;
    private boolean checkIsOk;

    @NonNull
    private final ProphetAssistant assistant;

    @Builder
    public ProphetRole(@NonNull String apiKey, String modelName, Float temperature) {
        ChatLanguageModel chatLanguageModel = ChatLanguageModelUtil.build(apiKey, modelName, temperature);
        this.assistant = AiServices.create(ProphetAssistant.class, chatLanguageModel);
    }

    public String speak(int id, int index, String gameInformation) {
        String answer = assistant.speak(id, index, gameInformation,
                checkResult.getCheckId(), checkIsOk ? "好人" : "狼人", checkResult.getCheckCause());
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

    public void skill(List<Player> players, int id, String gameInformation) {
        String answer = assistant.skill(id, gameInformation);
        log.info(answer);
        checkResult = ChatLanguageModelUtil.jsonAnswer2Object(answer, CheckResult.class);
        log.info("查验输出：{}", checkResult);
        int checkId = checkResult.getCheckId();
        Map<Integer, Player> playerMap = players.stream().collect(Collectors.toMap(Player::getId, player -> player));
        Role checkRole = playerMap.get(checkId).getRole();
        if (checkRole instanceof GoodRole) {
            checkIsOk = true;
            log.info("查验的" + checkId + "号是好人");
        } else {
            checkIsOk = false;
            log.info("查验的" + checkId + "号是狼人");
        }
    }

    /**
     * 预言家Assistant
     */
    interface ProphetAssistant {
        /**
         * 发言
         *
         * @return
         */
        @SystemMessage(fromResource = "/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/prophet-speak-user-prompt-template.txt")
        String speak(@V("id") int id, @V("index") int index, @V("gameInformation") String gameInformation,
                     @V("checkId") int checkId, @V("camp") String camp, @V("checkCause") String checkCause);

        /**
         * 技能
         *
         * @return
         */
        @SystemMessage(fromResource = "/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/prophet-skill-user-prompt-template.txt")
        String skill(@V("id") int id, @V("gameInformation") String gameInformation);

        /**
         * 投票
         *
         * @return
         */
        @SystemMessage(fromResource = "/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/prophet-vote-user-prompt-template.txt")
        String vote(@V("id") int id, @V("gameInformation") String gameInformation, @V("voteIds") List<Integer> voteIds);

        /**
         * 遗言
         *
         * @return
         */
        @SystemMessage(fromResource = "/player-system-prompt-template.txt")
        @UserMessage(fromResource = "/prophet-testament-user-prompt-template.txt")
        String testament(@V("id") int id, @V("gameInformation") String gameInformation);
    }
}

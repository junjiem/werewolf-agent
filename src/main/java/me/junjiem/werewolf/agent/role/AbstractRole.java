package me.junjiem.werewolf.agent.role;

import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.NonNull;
import me.junjiem.werewolf.agent.util.ChatLanguageModelUtil;

/**
 * @Author JunjieM
 * @Date 2024/4/11
 */
public abstract class AbstractRole {

    @NonNull
    protected final ChatLanguageModel chatLanguageModel;

    public AbstractRole(@NonNull String service, @NonNull String apiKey, String modelName, Float temperature) {
        this.chatLanguageModel = ChatLanguageModelUtil.build(service, apiKey, modelName, temperature);
    }

}

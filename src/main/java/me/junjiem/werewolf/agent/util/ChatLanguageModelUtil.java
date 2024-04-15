package me.junjiem.werewolf.agent.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.dashscope.QwenChatModel;
import dev.langchain4j.model.dashscope.QwenModelName;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.junjiem.werewolf.agent.bean.SpeakResult;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author JunjieM
 * @Date 2024/4/9
 */
@Slf4j
public class ChatLanguageModelUtil {

    private static final Pattern JSON_PATTERN = Pattern
            .compile("^\\s*```(.*?)\\n([\\s\\S]*?)\\n\\s*```", Pattern.MULTILINE);

    private ChatLanguageModelUtil() {
    }

    public static ChatLanguageModel build(@NonNull String apiKey) {
        return build(apiKey, null, null);
    }

    public static ChatLanguageModel build(@NonNull String apiKey, String modelName, Float temperature) {
        QwenChatModel.QwenChatModelBuilder builder = QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName(Optional.ofNullable(modelName).orElse(QwenModelName.QWEN_TURBO));
        Optional.ofNullable(temperature).ifPresent(builder::temperature);
        return builder.build();
    }

    public static <T> T jsonAnswer2Object(String answer, Class<T> classOfT) {
        Matcher matcher = JSON_PATTERN.matcher(answer);
        if (matcher.find()) {
            log.debug("Code Block Type: " + matcher.group(1));
            answer = matcher.group(2);
        }
        Gson gson = new Gson();
        answer = answer.replaceAll(",\\s*}", "}");
        log.info("JSON: " + answer);
        return gson.fromJson(answer, classOfT);
    }

}

package me.junjiem.werewolf.agent.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.dashscope.QwenChatModel;
import dev.langchain4j.model.dashscope.QwenModelName;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.model.zhipu.chat.ChatCompletionModel;
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

    public static ChatLanguageModel build(@NonNull String service, @NonNull String apiKey) {
        return build(service, apiKey, null, null);
    }

    public static ChatLanguageModel build(@NonNull String service, @NonNull String apiKey, String modelName, Double temperature) {
        if ("zhipuai".equalsIgnoreCase(service)) {
            ZhipuAiChatModel.ZhipuAiChatModelBuilder builder = ZhipuAiChatModel.builder()
                    .apiKey(apiKey)
                    .model(Optional.ofNullable(modelName).orElse(ChatCompletionModel.GLM_3_TURBO.toString()));
            Optional.ofNullable(temperature).ifPresent(builder::temperature);
            return builder.build();
        } else if ("dashscope".equalsIgnoreCase(service)) {
            QwenChatModel.QwenChatModelBuilder builder = QwenChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(Optional.ofNullable(modelName).orElse(QwenModelName.QWEN_TURBO));
            Optional.ofNullable(temperature).ifPresent(t -> builder.temperature(t.floatValue()));
            return builder.build();
        } else {
            OpenAiChatModel.OpenAiChatModelBuilder builder = OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(Optional.ofNullable(modelName).orElse(OpenAiChatModelName.GPT_3_5_TURBO.toString()));
            Optional.ofNullable(temperature).ifPresent(builder::temperature);
            return builder.build();
        }
    }

    public static <T> T jsonAnswer2Object(String answer, Class<T> classOfT) {
        Matcher matcher = JSON_PATTERN.matcher(answer);
        if (matcher.find()) {
            log.debug("Code Block Type: " + matcher.group(1));
            answer = matcher.group(2);
        }
        Gson gson = new Gson();
        answer = answer.replaceAll(",\\s*//.*?\\n", ", \n") // 去除逗号后面的单行注释
                .replaceAll(",(?=\\s*?[}\\]])", ""); // 去除尾随逗号
        log.info("JSON: " + answer);
        return gson.fromJson(answer, classOfT);
    }

}

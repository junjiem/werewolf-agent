package me.junjiem.werewolf.agent.bean;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * 遗言结果
 *
 * @Author JunjieM
 * @Date 2024/4/9
 */
@AllArgsConstructor
@Data
public class TestamentResult {
    @NonNull
    @SerializedName("reasoning_process")
    private String reasoningProcess;

    @NonNull
    @SerializedName("last_words")
    private String lastWords;
}

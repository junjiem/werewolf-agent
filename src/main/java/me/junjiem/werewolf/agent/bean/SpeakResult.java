package me.junjiem.werewolf.agent.bean;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * 发言结果
 *
 * @Author JunjieM
 * @Date 2024/4/8
 */
@AllArgsConstructor
@Data
public class SpeakResult {
    @NonNull
    @SerializedName("reasoning_process")
    private String reasoningProcess;

    @NonNull
    @SerializedName("my_speech")
    private String mySpeech;
}

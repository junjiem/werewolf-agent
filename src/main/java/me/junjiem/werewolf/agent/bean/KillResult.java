package me.junjiem.werewolf.agent.bean;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * 狼人猎杀结果
 *
 * @Author JunjieM
 * @Date 2024/4/8
 */
@AllArgsConstructor
@Data
public class KillResult {
    @SerializedName("kill_id")
    private Integer killId;

    @NonNull
    @SerializedName("my_strategy")
    private String myStrategy;
}

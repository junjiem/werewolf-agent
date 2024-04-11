package me.junjiem.werewolf.agent.bean;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * 女巫毒杀结果
 *
 * @Author JunjieM
 * @Date 2024/4/9
 */
@AllArgsConstructor
@Data
public class PoisonResult {
    @SerializedName("is_kill")
    private boolean isKill;

    @SerializedName("kill_id")
    private Integer killId;

    @NonNull
    @SerializedName("kill_cause")
    private String killCause;
}

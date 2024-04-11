package me.junjiem.werewolf.agent.bean;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

/**
 * 猎人射击结果
 *
 * @Author JunjieM
 * @Date 2024/4/8
 */
@AllArgsConstructor
@Data
public class ShotResult {
    @SerializedName("is_kill")
    private boolean isShot;

    @SerializedName("shot_id")
    private Integer shotId;

    @NonNull
    @SerializedName("shot_cause")
    private String shotCause;
}

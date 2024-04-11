package me.junjiem.werewolf.agent.bean;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * 预言家查验结果
 *
 * @Author JunjieM
 * @Date 2024/4/9
 */
@AllArgsConstructor
@Data
public class CheckResult {
    @NonNull
    @SerializedName("check_id")
    private Integer checkId;

    @NonNull
    @SerializedName("check_cause")
    private String checkCause;
}

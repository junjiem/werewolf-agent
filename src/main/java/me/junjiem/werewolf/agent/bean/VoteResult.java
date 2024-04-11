package me.junjiem.werewolf.agent.bean;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * 投票结果
 *
 * @Author JunjieM
 * @Date 2024/4/8
 */
@AllArgsConstructor
@Data
public class VoteResult {
    @SerializedName("vote_id")
    private Integer voteId;

    @NonNull
    @SerializedName("vote_cause")
    private String voteCause;
}

package me.junjiem.werewolf.agent.player;

import lombok.Builder;
import lombok.NonNull;
import me.junjiem.werewolf.agent.role.VillagerRole;

import java.util.List;

/**
 * 村民角色Player
 *
 * @Author JunjieM
 * @Date 2024/4/10
 */
public class VillagerPlayer {
    private int id; // ID
    private VillagerRole role; // 角色

    @Builder
    public VillagerPlayer(int id, @NonNull String apiKey, String modelName, Float temperature) {
        this.id = id;
        this.role = VillagerRole.builder().apiKey(apiKey).modelName(modelName).temperature(temperature).build();
    }

    public String speak(int index, String gameInformation) {
        return role.speak(id, index, gameInformation);
    }

    public int vote(String gameInformation, List<Integer> voteIds) {
        return role.vote(id, gameInformation, voteIds);
    }

    public String testament(String gameInformation) {
        return role.testament(id, gameInformation);
    }

}

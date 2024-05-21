package me.junjiem.werewolf.agent.player;

import lombok.NonNull;
import me.junjiem.werewolf.agent.role.VillagerRole;
import me.junjiem.werewolf.agent.util.GameData;

import java.util.List;

/**
 * 村民角色Player
 *
 * @Author JunjieM
 * @Date 2024/4/10
 */
public class VillagerPlayer extends AbstractPlayer {
    private final VillagerRole role;

    public VillagerPlayer(int id, String roleName, @NonNull String service, @NonNull String apiKey, String modelName, Float temperature) {
        super(id, roleName);
        this.role = new VillagerRole(service, apiKey, modelName, temperature);
    }

    @Override
    public boolean isGoodGuys() {
        return true;
    }

    @Override
    public String speak(int index) {
        return role.speak(id, index, GameData.getGameInformation());
    }

    @Override
    public int vote(List<Integer> voteIds) {
        return role.vote(id, GameData.getGameInformation(), voteIds);
    }

    @Override
    public String testament() {
        return role.testament(id, GameData.getGameInformation());
    }

}

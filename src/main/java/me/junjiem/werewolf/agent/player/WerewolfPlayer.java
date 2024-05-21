package me.junjiem.werewolf.agent.player;

import lombok.NonNull;
import me.junjiem.werewolf.agent.bean.KillResult;
import me.junjiem.werewolf.agent.role.WerewolfRole;
import me.junjiem.werewolf.agent.util.GameData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author JunjieM
 * @Date 2024/4/11
 */
public class WerewolfPlayer extends AbstractPlayer {
    private final WerewolfRole role;

    public WerewolfPlayer(int id, String roleName, @NonNull String service, @NonNull String apiKey, String modelName, Float temperature) {
        super(id, roleName);
        this.role = new WerewolfRole(service, apiKey, modelName, temperature);
    }

    @Override
    public boolean isGoodGuys() {
        return false;
    }

    @Override
    public String speak(int index) {
        List<String> werewolfTeams = GameData.getWerewolfPlayers().stream()
                .map(p -> p.getId() + "号玩家")
                .collect(Collectors.toList());
        return role.speak(id, index, GameData.getGameInformation(), werewolfTeams, GameData.werewolfKillId);
    }

    @Override
    public int vote(List<Integer> voteIds) {
        return role.vote(id, GameData.getGameInformation(), voteIds);
    }

    @Override
    public String testament() {
        return role.testament(id, GameData.getGameInformation());
    }

    public KillResult skill(String teamStrategies) {
        List<String> werewolfTeammates = GameData.getWerewolfPlayers().stream()
                .filter(p -> p.getId() != id)
                .map(p -> p.getId() + "号玩家")
                .collect(Collectors.toList());
        return role.skill(id, GameData.getGameInformation(), werewolfTeammates, teamStrategies);
    }
}

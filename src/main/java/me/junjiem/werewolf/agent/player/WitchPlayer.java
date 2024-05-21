package me.junjiem.werewolf.agent.player;

import lombok.NonNull;
import me.junjiem.werewolf.agent.bean.PoisonResult;
import me.junjiem.werewolf.agent.role.WitchRole;
import me.junjiem.werewolf.agent.util.GameData;

import java.util.List;

/**
 * @Author JunjieM
 * @Date 2024/4/11
 */
public class WitchPlayer extends AbstractPlayer {
    private final WitchRole role;

    private boolean elixirs = true; // 灵药是否还有
    private boolean poisons = true; // 毒药是否还有

    private int saveId = -1; // 昨晚救活的ID
    private int killId = -1; // 昨晚毒死的ID

    public WitchPlayer(int id, String roleName, @NonNull String service, @NonNull String apiKey, String modelName, Double temperature) {
        super(id, roleName);
        this.role = new WitchRole(service, apiKey, modelName, temperature);
    }

    @Override
    public boolean isGoodGuys() {
        return true;
    }

    @Override
    public String speak(int index) {
        String skillInformation = "";
        if (saveId != -1) {
            skillInformation += "### 用药结果 ###\n昨晚救了" + saveId + "号。\n";
            this.saveId = -1;
        } else if (killId != -1) {
            skillInformation += "### 用药结果 ###\n昨晚毒死了" + killId + "号。\n";
            this.killId = -1;
        }
        return role.speak(id, index, GameData.getGameInformation(), skillInformation);
    }

    @Override
    public int vote(List<Integer> voteIds) {
        return role.vote(id, GameData.getGameInformation(), voteIds);
    }

    @Override
    public String testament() {
        return role.testament(id, GameData.getGameInformation());
    }

    public int skill(int killId) {
        if (!this.poisons) {
            return -1;
        }
        PoisonResult result = role.skill(id, GameData.getGameInformation(), killId);
        if (result.isKill()) {
            this.killId = result.getKillId();
            this.poisons = false;
        }
        return result.isKill() ? result.getKillId() : -1;
    }

    public void save(int killId) {
        if (!this.elixirs) {
            return;
        }
        this.saveId = killId;
        this.elixirs = false;
    }
}

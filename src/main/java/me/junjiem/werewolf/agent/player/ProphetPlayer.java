package me.junjiem.werewolf.agent.player;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.junjiem.werewolf.agent.bean.CheckResult;
import me.junjiem.werewolf.agent.role.ProphetRole;
import me.junjiem.werewolf.agent.util.GameData;

import java.util.List;

/**
 * @Author JunjieM
 * @Date 2024/4/11
 */
@Slf4j
public class ProphetPlayer extends AbstractPlayer {
    private final ProphetRole role;

    private CheckResult checkResult;
    private boolean checkIsOk;

    public ProphetPlayer(int id, String roleName, @NonNull String service, @NonNull String apiKey, String modelName, Double temperature) {
        super(id, roleName);
        this.role = new ProphetRole(service, apiKey, modelName, temperature);
    }

    @Override
    public boolean isGoodGuys() {
        return true;
    }

    @Override
    public String speak(int index) {
        return role.speak(id, index, GameData.getGameInformation(),
                checkResult.getCheckId(), checkIsOk ? "好人" : "狼人", checkResult.getCheckCause());
    }

    @Override
    public int vote(List<Integer> voteIds) {
        return role.vote(id, GameData.getGameInformation(), voteIds);
    }

    @Override
    public String testament() {
        return role.testament(id, GameData.getGameInformation());
    }

    public void skill() {
        checkResult = role.skill(id, GameData.getGameInformation());
        int checkId = checkResult.getCheckId();
        if (GameData.getPlayer(checkId).isGoodGuys()) {
            checkIsOk = true;
            log.info("查验的" + checkId + "号是好人");
        } else {
            checkIsOk = false;
            log.info("查验的" + checkId + "号是狼人");
        }
    }

}

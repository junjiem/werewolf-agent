package me.junjiem.werewolf.agent.player;

import lombok.Getter;
import me.junjiem.werewolf.agent.util.GameData;

/**
 * @Author JunjieM
 * @Date 2024/4/11
 */
@Getter
public abstract class AbstractPlayer implements Player {

    /**
     * ID
     */
    protected final int id;

    protected final String roleName;

    public AbstractPlayer(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    @Override
    public boolean isAlive() {
        return !GameData.killIds.contains(id);
    }

}

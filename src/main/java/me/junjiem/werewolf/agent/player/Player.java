package me.junjiem.werewolf.agent.player;

import lombok.Data;
import me.junjiem.werewolf.agent.role.Role;

/**
 * 玩家
 *
 * @Author JunjieM
 * @Date 2024/4/8
 */
@Data
public class Player {
    private int id; // ID
    private Role role; // 角色
    private boolean isAlive = true; // 是否活着

    public Player(int id, Role role) {
        this.id = id;
        this.role = role;
    }
}
package me.junjiem.werewolf.agent.player;

import me.junjiem.werewolf.agent.GameOverException;

import java.util.List;

/**
 * @Author JunjieM
 * @Date 2024/4/11
 */
public interface Player {

    /**
     * 是否是好人阵营
     *
     * @return
     */
    boolean isGoodGuys();

    /**
     * 是否活着
     */
    boolean isAlive();

    /**
     * 发言
     *
     * @param index
     * @return
     */
    String speak(int index);

    /**
     * 投票
     *
     * @param votingIds
     * @return
     */
    int vote(List<Integer> votingIds);

    /**
     * 遗言
     *
     * @return
     */
    String testament() throws GameOverException;
}

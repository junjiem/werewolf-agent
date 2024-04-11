package me.junjiem.werewolf.agent.role;

import java.util.List;

/**
 * 角色
 *
 * @Author JunjieM
 * @Date 2024/4/9
 */
public interface Role {
    /**
     * 投票
     *
     * @return 为空表示弃票
     */
    Integer vote(int id, String gameInformation, List<Integer> voteIds);
}

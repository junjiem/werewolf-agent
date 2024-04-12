package me.junjiem.werewolf.agent.util;

import lombok.extern.slf4j.Slf4j;
import me.junjiem.werewolf.agent.GameOverException;
import me.junjiem.werewolf.agent.player.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author JunjieM
 * @Date 2024/4/11
 */
@Slf4j
public class GameData {
    private GameData() {
    }

    public static final List<AbstractPlayer> players = new ArrayList<>(); // 玩家列表

    public static final LinkedHashMap<Integer, String> gameInformations = new LinkedHashMap<>(); // 游戏对局信息

    public static final Set<Integer> killIds = new HashSet<>(); // 已死亡的玩家ID

    public static int werewolfKillId = -1; // 狼人昨晚猎杀的玩家ID

    public static boolean goodGuysWin = true; // 是否好人阵营获胜

    public static AbstractPlayer getPlayer(int id) {
        Map<Integer, AbstractPlayer> playerMap = players.stream()
                .collect(Collectors.toMap(AbstractPlayer::getId, p -> p));
        return playerMap.get(id);
    }

    public static List<AbstractPlayer> getAlivePlayers() {
        return players.stream()
                .filter(AbstractPlayer::isAlive)
                .collect(Collectors.toList());
    }

    public static List<AbstractPlayer> getWerewolfPlayers() {
        return players.stream()
                .filter(p -> p instanceof WerewolfPlayer)
                .collect(Collectors.toList());
    }

    public static List<AbstractPlayer> getAliveWerewolfPlayers() {
        return players.stream()
                .filter(p -> p instanceof WerewolfPlayer)
                .filter(AbstractPlayer::isAlive)
                .collect(Collectors.toList());
    }

    private static long aliveWerewolfCount() {
        return getAliveWerewolfPlayers().size();
    }

    public static List<AbstractPlayer> getAliveProphetPlayers() {
        return players.stream()
                .filter(p -> p instanceof ProphetPlayer)
                .filter(AbstractPlayer::isAlive)
                .collect(Collectors.toList());
    }

    public static List<AbstractPlayer> getAliveWitchPlayers() {
        return players.stream()
                .filter(p -> p instanceof WitchPlayer)
                .filter(AbstractPlayer::isAlive)
                .collect(Collectors.toList());
    }

    public static List<AbstractPlayer> getAliveHunterPlayers() {
        return players.stream()
                .filter(p -> p instanceof HunterPlayer)
                .filter(AbstractPlayer::isAlive)
                .collect(Collectors.toList());
    }

    private static long aliveDeityCount() {
        return players.stream()
                .filter(p -> p instanceof ProphetPlayer || p instanceof WitchPlayer || p instanceof HunterPlayer)
                .filter(AbstractPlayer::isAlive)
                .count();
    }

    private static long aliveVillagerCount() {
        return players.stream()
                .filter(p -> p instanceof VillagerPlayer)
                .filter(AbstractPlayer::isAlive)
                .count();
    }

    public static String getGameInformation() {
        Map<Integer, AbstractPlayer> playerMap = players.stream()
                .collect(Collectors.toMap(AbstractPlayer::getId, p -> p));
        String gameInformation = gameInformations.entrySet().stream()
                .map(e -> {
                    int id = playerMap.get(e.getKey()).getId();
                    String str = id + "号玩家";
                    if (killIds.contains(id)) {
                        str += "[已死亡]";
                    }
                    return str + (StringUtils.isNotBlank(e.getValue()) ? e.getValue() : "\n(第1天还未轮到发言)");
                })
                .collect(Collectors.joining("\n\n"));
        log.info("### 对局信息 ###\n" + gameInformation);
        return gameInformation;
    }

    public static void addPlayerInformation(int id, String information) {
        gameInformations.put(id, (StringUtils.isNotBlank(gameInformations.get(id)) ? gameInformations.get(id) : "")
                + "\n" + information);
    }

    public static void playerDead(int id) throws GameOverException {
        killIds.add(id);
        log.info("目前存活玩家：" + GameData.getAlivePlayers().stream()
                .map(p -> p.getId() + "号[" + p.getRoleName() + "]")
                .collect(Collectors.joining(", ")));
        if (aliveWerewolfCount() == 0) {
            throw new GameOverException();
        } else if (aliveDeityCount() == 0 || aliveVillagerCount() == 0) {
            goodGuysWin = false;
            throw new GameOverException();
        }
    }

}

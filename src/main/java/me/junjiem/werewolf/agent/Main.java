package me.junjiem.werewolf.agent;

import lombok.extern.slf4j.Slf4j;
import me.junjiem.werewolf.agent.bean.KillResult;
import me.junjiem.werewolf.agent.bean.ShotResult;
import me.junjiem.werewolf.agent.player.Player;
import me.junjiem.werewolf.agent.player.VillagerPlayer;
import me.junjiem.werewolf.agent.role.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author JunjieM
 * @Date 2024/4/8
 */
@Slf4j
public class Main {

    private static final String apiKey = "sk-XXX";

    private static final List<Player> players = new ArrayList<>();

    private static final Map<Integer, String> gameInformationMap = new LinkedHashMap<>();

    private static boolean goodWin = true;

    public static void main(String[] args) {
        System.out.println("-------------------初始化游戏-------------------");
        // 定义角色列表
        ArrayList<String> roles = new ArrayList<>();
        roles.add("预言家");
        roles.add("女巫");
        roles.add("猎人");
        for (int i = 0; i < 3; i++) {
            roles.add("村民");
        }
        for (int i = 0; i < 3; i++) {
            roles.add("狼人");
        }
        // 洗牌，打乱角色顺序
        Collections.shuffle(roles);
        // 分配角色给玩家
        for (int i = 0; i < 9; i++) {
            players.add(new Player(i + 1, createRole(roles.get(i))));
            gameInformationMap.put(i + 1, "");
        }

        System.out.println("-------------------开始游戏-------------------");
        try {
            for (int i = 1; true; i++) {
                List<Integer> killIds = new ArrayList<>();
                System.out.println("==========第" + i + "天==========");
                System.out.println("==========天黑请闭眼==========");
                System.out.println("++++++++狼人请睁眼++++++++");
                System.out.println(">>>>>>狼人请选择猎杀目标<<<<<<");
                List<Player> werewolfPlayers = players.stream()
                        .filter(player -> player.getRole() instanceof WerewolfRole)
                        .filter(Player::isAlive)
                        .collect(Collectors.toList());
                int killId = collectiveKill(werewolfPlayers, getGameInformation());
                System.out.println("++++++++狼人请闭眼++++++++");
                System.out.println("++++++++预言家请睁眼++++++++");
                System.out.println(">>>>>>预言家请选择查验目标<<<<<<");
                List<Player> prophetPlayers = players.stream()
                        .filter(player -> player.getRole() instanceof ProphetRole)
                        .filter(Player::isAlive)
                        .collect(Collectors.toList());
                if (!prophetPlayers.isEmpty()) {
                    Player prophetPlayer = prophetPlayers.get(0);
                    ProphetRole prophetRole = (ProphetRole) prophetPlayer.getRole();
                    prophetRole.skill(players, prophetPlayer.getId(), getGameInformation());
                }
                System.out.println("++++++++预言家请闭眼++++++++");
                System.out.println("++++++++女巫请睁眼++++++++");
                List<Player> witchPlayers = players.stream()
                        .filter(player -> player.getRole() instanceof WitchRole)
                        .filter(Player::isAlive)
                        .collect(Collectors.toList());
                if (!prophetPlayers.isEmpty()) {
                    Player witchPlayer = witchPlayers.get(0);
                    WitchRole witchRole = (WitchRole) witchPlayer.getRole();
                    System.out.println(">>>>>>" + killId + "号被刀<<<<<<");
                    if (i == 1) { // 第一天
                        System.out.println(">>>>>>由于是第一晚女巫救活了他<<<<<<");
                        witchRole.save(killId);
                        killId = -1;
                    } else {
                        int poisonId = witchRole.skill(witchPlayer.getId(), getGameInformation());
                        if (poisonId != -1) {
                            killIds.add(poisonId);
                        }
                    }
                }
                System.out.println("++++++++女巫请闭眼++++++++");
                if (killId != -1) {
                    killIds.add(killId);
                }
                System.out.println("==========天亮请睁眼==========");
                if (killIds.isEmpty()) {
                    System.out.println(">>>>>>昨晚是个平安夜<<<<<<");
                } else {
                    System.out.println(">>>>>>昨晚" + killIds + "号死亡<<<<<<");
                    checkGameStatus(killIds); // 检查游戏状态
                    int finalI = i;
                    killIds.forEach(id -> gameInformationMap.put(id,
                            gameInformationMap.get(id) + "\n第" + finalI + "天晚上死亡"));
                    if (i == 1) { // 第一天，发表遗言
                        for (int id : killIds) {
                            testaments(i, id);
                        }
                    }
                }
                List<Player> alivePlayers = players.stream().filter(Player::isAlive).collect(Collectors.toList());
                System.out.println("++++++++开始依次发言++++++++");
                for (int j = 0; j < alivePlayers.size(); j++) {
                    Player player = alivePlayers.get(j);
                    Role role = player.getRole();
                    String speak;
                    if (role instanceof GoodRole) {
                        speak = ((GoodRole) role).speak(player.getId(), j, getGameInformation());
                    } else {
                        List<String> werewolfTeams = players.stream()
                                .filter(p -> p.getRole() instanceof WerewolfRole)
                                .filter(Player::isAlive)
                                .map(p -> p.getId() + "号玩家")
                                .collect(Collectors.toList());
                        speak = ((WerewolfRole) role).speak(player.getId(), j, getGameInformation(), werewolfTeams, killId);
                    }
                    System.out.println(player.getId() + "号玩家" + " -> " + speakInformation(i, speak));
                    gameInformationMap.put(player.getId(), gameInformationMap.get(player.getId())
                            + "\n" + speakInformation(i, speak));
                }
                System.out.println("++++++++开始进行投票++++++++");
                int voteId = collectiveVote(alivePlayers, getGameInformation(),
                        alivePlayers.stream().map(Player::getId).collect(Collectors.toList()));
                players.stream().filter(p -> p.getId() == voteId).forEach(p -> p.setAlive(false));
                checkGameStatus(Collections.singletonList(voteId)); // 检查游戏状态
                System.out.println("++++++++开始发表遗言++++++++");
                System.out.println(">>>>>>" + voteId + "号被投票出局，请发表遗言<<<<<<");
                testaments(i, voteId); // 发表遗言
            }
        } catch (GameOverException e) {
            System.out.println("-------------------结束游戏-------------------");
            System.out.println("---------------------------------------------");
            System.out.println("|              获胜阵营：" + (goodWin ? "好人" : "坏人") + "阵营               |");
            System.out.println("---------------------------------------------");
        }
    }

    private static void checkGameStatus(List<Integer> killIds) throws GameOverException {
        players.stream().filter(p -> killIds.contains(p.getId())).forEach(p -> p.setAlive(false));
        List<Player> alivePlayers = players.stream().filter(Player::isAlive).collect(Collectors.toList());
        if (alivePlayers.stream().noneMatch(p -> p.getRole() instanceof BadRole)) {
            throw new GameOverException();
        } else if (alivePlayers.stream().noneMatch(p -> p.getRole() instanceof DeityRole)
                || alivePlayers.stream().noneMatch(p -> p.getRole() instanceof VillagerRole)) {
            goodWin = false;
            throw new GameOverException();
        }
    }

    private static void testaments(int day, int killId) throws GameOverException {
        Player player = players.stream().filter(p -> p.getId() == killId).collect(Collectors.toList()).get(0);
        Role role = player.getRole();
        String gameInformation = getGameInformation();
        String testament = "";
        if (role instanceof VillagerRole) {
            testament = ((VillagerRole) role).testament(player.getId(), gameInformation);
        } else if (role instanceof WerewolfRole) {
            testament = ((WerewolfRole) role).testament(player.getId(), gameInformation);
        } else if (role instanceof ProphetRole) {
            testament = ((ProphetRole) role).testament(player.getId(), gameInformation);
        } else if (role instanceof WitchRole) {
            testament = ((WitchRole) role).testament(player.getId(), gameInformation);
        } else if (role instanceof HunterRole) {
            ShotResult result = ((HunterRole) role).skill(player.getId(), gameInformation);
            testament = result.getShotCause();
            if (result.isShot()) {
                int shotId = result.getShotId();
                System.out.println(">>>>>>" + shotId + "号被开枪带走<<<<<<");
                checkGameStatus(Collections.singletonList(shotId)); // 检查游戏状态
            }
        }
        System.out.println(player.getId() + "号玩家" + " -> " + testamentInformation(day, testament));
        gameInformationMap.put(player.getId(), gameInformationMap.get(player.getId())
                + "\n" + testamentInformation(day, testament));
    }

    private static String getGameInformation() {
        Map<Integer, Player> playerMap = players.stream().collect(Collectors.toMap(Player::getId, p -> p));
        String gameInformation = gameInformationMap.entrySet().stream()
                .map(e -> {
                    Player player = playerMap.get(e.getKey());
                    String str = player.getId() + "号玩家";
                    if (!player.isAlive()) {
                        str += "[已死亡]";
                    }
                    return str + e.getValue();
                })
                .collect(Collectors.joining("\n\n"));
        log.info("### 对局信息 ###\n" + gameInformation);
        return gameInformation;
    }

    private static String speakInformation(int day, String speak) {
        return "第" + day + "天发言: " + speak;
    }

    private static String testamentInformation(int day, String testament) {
        return "第" + day + "天被投票出局，发表遗言: " + testament;
    }

    private static Role createRole(String roleName) {
        switch (roleName) {
            case "村民":
                return VillagerRole.builder().apiKey(apiKey).build();
            case "狼人":
                return WerewolfRole.builder().apiKey(apiKey).build();
            case "预言家":
                return ProphetRole.builder().apiKey(apiKey).build();
            case "女巫":
                return WitchRole.builder().apiKey(apiKey).build();
            case "猎人":
                return HunterRole.builder().apiKey(apiKey).build();
            default:
                throw new IllegalArgumentException("不支持的角色: " + roleName);
        }
    }

    /**
     * 集体决定投票目标
     *
     * @return
     */
    public static int collectiveVote(List<Player> players, String gameInformation, List<Integer> voteIds) {
        Map<Integer, Integer> map = new HashMap<>();
        for (Player player : players) {
            Role role = player.getRole();
            Integer voteId = role.vote(player.getId(), gameInformation, voteIds);
            if (voteId != null) {
                map.put(player.getId(), voteId);
            }
        }
        System.out.println("投票结果：" + map.entrySet().stream()
                .map(e -> e.getKey() + "->" + e.getValue())
                .collect(Collectors.joining(", "))
        );
        Map<Integer, Long> frequencyMap = map.values().stream()
                .collect(Collectors.groupingBy(
                        Function.identity(), // 分组依据，这里表示元素本身
                        Collectors.counting()  // 计算每个元素的数量
                ));
        long maxFrequency = frequencyMap.values().stream().max(Long::compareTo).orElse(0L);
        List<Integer> maxFrequencyVoteIds = frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxFrequency))
                .map(Map.Entry::getKey).collect(Collectors.toList());
        if (maxFrequencyVoteIds.size() == 1) { // 只有一个出现次数最多的ID
            return maxFrequencyVoteIds.get(0);
        } else { // 有多个出现次数最多的ID
            System.out.println(maxFrequencyVoteIds.stream().map(id -> id + "")
                    .collect(Collectors.joining(", ")) + "都为" + maxFrequency + "票，请重新投票");
            List<Player> newPlayers = players.stream()
                    .filter(p -> !maxFrequencyVoteIds.contains(p.getId()))
                    .collect(Collectors.toList());
            List<Integer> newVoteIds = players.stream()
                    .map(Player::getId)
                    .filter(maxFrequencyVoteIds::contains)
                    .collect(Collectors.toList());
            return collectiveVote(newPlayers, gameInformation, newVoteIds);
        }
    }

    /**
     * 集体决定猎杀目标
     *
     * @param werewolfPlayers
     * @return
     */
    public static int collectiveKill(List<Player> werewolfPlayers, String gameInformation) {
        Map<Player, KillResult> map = new HashMap<>();
        for (Player player : werewolfPlayers) {
            List<String> werewolfTeammates = werewolfPlayers.stream()
                    .filter(p -> player.getId() != p.getId())
                    .map(p -> p.getId() + "号玩家")
                    .collect(Collectors.toList());
            WerewolfRole role = (WerewolfRole) player.getRole();
            List<String> teamStrategies = new ArrayList<>();
            int index = 1;
            for (Map.Entry<Player, KillResult> e : map.entrySet()) {
                teamStrategies.add("team_strategy_" + (index++) + ":" + e.getKey().getId() + "号玩家想杀死的玩家是"
                        + e.getValue().getKillId() + "号，他的游戏策略如下：\n" + e.getValue().getMyStrategy());
            }
            KillResult result = role.skill(player.getId(), werewolfTeammates, gameInformation, String.join("\n", teamStrategies));
            map.put(player, result);
        }
        Map<Integer, Long> frequencyMap = map.values().stream()
                .map(KillResult::getKillId)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Function.identity(), // 分组依据，这里表示元素本身
                        Collectors.counting()  // 计算每个元素的数量
                ));
        long maxFrequency = frequencyMap.values().stream().max(Long::compareTo).orElse(0L);
        List<Integer> maxFrequencyKillIds = frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxFrequency))
                .map(Map.Entry::getKey).collect(Collectors.toList());
        if (maxFrequencyKillIds.size() == 1) { // 只有一个出现次数最多的ID
            return maxFrequencyKillIds.get(0);
        } else { // 有多个出现次数最多的ID
            return collectiveKill(werewolfPlayers, gameInformation);
        }
    }

}

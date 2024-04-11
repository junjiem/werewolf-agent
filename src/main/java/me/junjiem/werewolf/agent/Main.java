package me.junjiem.werewolf.agent;

import lombok.extern.slf4j.Slf4j;
import me.junjiem.werewolf.agent.bean.KillResult;
import me.junjiem.werewolf.agent.player.*;
import me.junjiem.werewolf.agent.util.GameData;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author JunjieM
 * @Date 2024/4/8
 */
@Slf4j
public class Main {

    private final static Map<String, Object> config;

    static {
        try (InputStream in = Main.class.getClassLoader().getResourceAsStream("config.yaml")) {
            Yaml yaml = new Yaml(new Constructor(Map.class));
            config = yaml.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Load config.yaml failed.", e);
        }
    }

    private static final String apiKey = (String) ((Map) config.get("llm")).get("api_key");
    private static final String modelName = (String) ((Map) config.get("llm")).get("model_name");
    private static final Double temperature = (Double) ((Map) config.get("llm")).get("temperature");

    private static boolean goodWin = true;

    private static void init() {
        // 定义角色列表
        List<String> roles = new ArrayList<>();
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
        for (int i = 0; i < roles.size(); i++) {
            int id = i + 1;
            String role = roles.get(i);
            log.info(id + "号玩家角色: " + role);
            GameData.players.add(createPlayer(id, role));
            GameData.gameInformations.put(id, null);
        }
    }

    public static void main(String[] args) {
        System.out.println("-------------------初始化游戏-------------------");
        init();
        System.out.println("-------------------开始游戏-------------------");
        try {
            for (int i = 1; true; i++) {
                Set<Integer> killIds = new HashSet<>();
                System.out.println("==================== 第" + i + "天 ====================");

                System.out.println("========== 天黑请闭眼 ==========");
                System.out.println("++++++++狼人请睁眼++++++++");
                System.out.println(">>>>>>狼人请选择猎杀目标<<<<<<");
                int killId = collectiveKill(); // 狼人集体决定猎杀目标
                System.out.println("++++++++狼人请闭眼++++++++");
                System.out.println("++++++++预言家请睁眼++++++++");
                System.out.println(">>>>>>预言家请选择查验目标<<<<<<");
                List<AbstractPlayer> prophetPlayers = GameData.getAliveProphetPlayers();
                if (!prophetPlayers.isEmpty()) {
                    ((ProphetPlayer) prophetPlayers.get(0)).skill();
                }
                System.out.println("++++++++预言家请闭眼++++++++");
                System.out.println("++++++++女巫请睁眼++++++++");
                List<AbstractPlayer> witchPlayers = GameData.getAliveWitchPlayers();
                if (!witchPlayers.isEmpty()) {
                    WitchPlayer witchPlayer = (WitchPlayer) witchPlayers.get(0);
                    System.out.println(">>>>>>" + killId + "号玩家被刀<<<<<<");
                    if (i == 1) { // 第一天
                        System.out.println(">>>>>>由于是第一晚女巫救活了他<<<<<<");
                        witchPlayer.save(killId);
                        killId = -1;
                    } else {
                        int poisonId = witchPlayer.skill(killId);
                        if (poisonId != -1) {
                            killIds.add(poisonId);
                        }
                    }
                }
                System.out.println("++++++++女巫请闭眼++++++++");
                if (killId != -1) {
                    killIds.add(killId);
                }

                System.out.println("========== 天亮请睁眼 ==========");
                if (killIds.isEmpty()) {
                    System.out.println(">>>>>>昨晚是个平安夜<<<<<<");
                } else {
                    System.out.println(">>>>>>昨晚" + killIds + "号玩家死亡<<<<<<");
                    for (int id : killIds) {
                        GameData.playerDead(id);
                        GameData.addPlayerInformation(id, "第" + i + "天晚上死亡");
                    }
                }
                List<AbstractPlayer> alivePlayers = GameData.getAlivePlayers();
                System.out.println("++++++++开始依次发言++++++++");
                for (int j = 0; j < alivePlayers.size(); j++) {
                    AbstractPlayer player = alivePlayers.get(j);
                    String speak = player.speak(j);
                    System.out.println(player.getId() + "号玩家" + " -> " + speakInformation(i, speak));
                    GameData.addPlayerInformation(player.getId(), speakInformation(i, speak));
                }
                System.out.println("++++++++开始进行投票++++++++");
                int voteId = collectiveVote(i, 1, alivePlayers,
                        alivePlayers.stream().map(AbstractPlayer::getId).collect(Collectors.toList()));
                System.out.println(">>>>>>" + voteId + "号玩家被投票出局<<<<<<");
                GameData.playerDead(voteId);
                System.out.println(">>>>>>" + voteId + "号玩家请发表遗言<<<<<<");
                testaments(i, voteId);
            }
        } catch (GameOverException e) {
            System.out.println("-------------------结束游戏-------------------");
            System.out.println("---------------------------------------------");
            System.out.println("|              获胜阵营：" + (goodWin ? "好人" : "坏人") + "阵营               |");
            System.out.println("---------------------------------------------");
        }
    }

    private static void testaments(int day, int killId) throws GameOverException {
        String testament = GameData.getPlayer(killId).testament();
        System.out.println(killId + "号玩家 -> " + testamentInformation(day, testament));
        GameData.addPlayerInformation(killId, testamentInformation(day, testament));
    }

    private static String speakInformation(int day, String speak) {
        return "第" + day + "天发言: " + speak;
    }

    private static String testamentInformation(int day, String testament) {
        return "第" + day + "天被投票出局，发表遗言: " + testament;
    }

    private static AbstractPlayer createPlayer(int id, String roleName) {
        switch (roleName) {
            case "村民":
                return new VillagerPlayer(id, roleName, apiKey, modelName, temperature.floatValue());
            case "狼人":
                return new WerewolfPlayer(id, roleName, apiKey, modelName, temperature.floatValue());
            case "预言家":
                return new ProphetPlayer(id, roleName, apiKey, modelName, temperature.floatValue());
            case "女巫":
                return new WitchPlayer(id, roleName, apiKey, modelName, temperature.floatValue());
            case "猎人":
                return new HunterPlayer(id, roleName, apiKey, modelName, temperature.floatValue());
            default:
                throw new IllegalArgumentException("不支持的角色: " + roleName);
        }
    }

    /**
     * 集体决定投票目标
     *
     * @return
     */
    public static int collectiveVote(int day, int times, List<AbstractPlayer> players, List<Integer> voteIds) {
        Map<Integer, Integer> map = new HashMap<>();
        for (AbstractPlayer player : players) {
            int voteId = player.vote(voteIds);
            String voteInfo = "第" + day + "天第" + times + "次投票: 弃票";
            if (voteId != -1) {
                voteInfo = "第" + day + "天第" + times + "次投票: 投给了" + voteId + "号玩家";
                map.put(player.getId(), voteId);
            }
            GameData.addPlayerInformation(player.getId(), voteInfo);
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
            List<AbstractPlayer> newPlayers = players.stream()
                    .filter(p -> !maxFrequencyVoteIds.contains(p.getId()))
                    .collect(Collectors.toList());
            List<Integer> newVoteIds = players.stream()
                    .map(AbstractPlayer::getId)
                    .filter(maxFrequencyVoteIds::contains)
                    .collect(Collectors.toList());
            return collectiveVote(day, times + 1, newPlayers, newVoteIds);
        }
    }

    /**
     * 狼人集体决定猎杀目标
     *
     * @return
     */
    public static int collectiveKill() {
        Map<AbstractPlayer, KillResult> map = new HashMap<>();
        for (AbstractPlayer player : GameData.getAliveWerewolfPlayers()) {
            List<String> teamStrategies = new ArrayList<>();
            int index = 1;
            for (Map.Entry<AbstractPlayer, KillResult> e : map.entrySet()) {
                teamStrategies.add("team_strategy_" + (index++) + ":" + e.getKey().getId() + "号玩家想杀死的玩家是"
                        + e.getValue().getKillId() + "号，他的游戏策略如下：\n" + e.getValue().getMyStrategy());
            }
            KillResult result = ((WerewolfPlayer) player).skill(String.join("\n", teamStrategies));
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
            return collectiveKill();
        }
    }

}

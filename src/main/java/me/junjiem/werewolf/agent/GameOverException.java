package me.junjiem.werewolf.agent;

/**
 * @Author JunjieM
 * @Date 2024/4/10
 */
public class GameOverException extends Exception {
    public GameOverException() {
        super("游戏结束！");
    }

    public GameOverException(String message) {
        super(message);
    }
}

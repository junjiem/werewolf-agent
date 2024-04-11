package me.junjiem.werewolf.agent;

import com.google.gson.Gson;
import me.junjiem.werewolf.agent.bean.SpeakResult;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author JunjieM
 * @Date 2024/4/9
 */
public class Test1 {
    @Test
    public void test1() {
        String json = "{\n" +
                "  \"reasoning_process\": \"作为预言家，我先查验了9号，因为他是最后一个发言的村民，容易留下破绽。8号被猎杀，表明有狼人存在。1号对跳预言家，且查杀8号，可能是狼人策略。2号和4号的发言过于谨慎，可能在隐藏身份。3号、5号和6号的怀疑有一定合理性，但没有直接指向狼人。7号的查杀7号看似矛盾，可能是在混淆视听。综合分析，我认为9号的查验结果是狼人，因为他的发言逻辑不连贯，而且符合首晚被查杀的常见狼人策略。我会以此为契机，号召大家投票9号，揭露狼人身份。\",\n" +
                "  \"my_speech\": \"各位，我是预言家，昨晚查验了9号，结果是狼人。9号的发言存在逻辑漏洞，可能是他急于掩饰身份。1号的对跳和查杀8号，可能是个圈套。2号和4号的发言过于保守，让人起疑。3号、5号和6号的分析都有道理，但还没有确凿证据。7号查杀自己，可能是想转移视线。鉴于此，我建议大家信任我的查验，9号是狼人，让我们一起投票驱逐他，保护好我们的阵营。\"" +
                "}";
        Gson gson = new Gson();
        SpeakResult result = gson.fromJson(json, SpeakResult.class);
        System.out.println(result);
    }
}

package cc.w0rm.ghost.service;

import cc.w0rm.ghost.api.MsgConsumer;
import cc.w0rm.ghost.dto.BaozouResponseDTO;
import cc.w0rm.ghost.dto.TklConvertDTO;
import cc.w0rm.ghost.dto.TklInfoDTO;
import cc.w0rm.ghost.rpc.baozou.BaoZouService;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.bot.BotInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author : xuyang
 * @date : 2020/10/15 4:10 下午
 */

/**
 * service 必须指定名称， 名称为对应的消息组名称
 */

@Slf4j
@Service("q1")
public class MsgConsumerImpl implements MsgConsumer {

    @Resource
    private BaoZouService baoZouService;

//    @PostConstruct 可以debug查看请求结果
    public void test(){
        BaozouResponseDTO<TklInfoDTO> responseDTO = baoZouService.tklDecrypt("/SPJlcjYgikM//");
        TklInfoDTO tklInfoDTO = responseDTO.getData();
        TklConvertDTO tklConvertDTO = TklConvertDTO.builder()
                .goodsId(tklInfoDTO.getGoodsId())
                .action("tkl")
                .title(tklInfoDTO.getTitle())
                .activityId(tklInfoDTO.getActivityId())
                .picUrl(tklInfoDTO.getPirUrl())
                .pid("mm_1414520133_2052450010_110872200005")
                .build();

        BaozouResponseDTO<?> baozouResponseDTO = baoZouService.convertMiddle(tklConvertDTO);
        if (0 == baozouResponseDTO.getCode()) {
            log.info("口令转换成功={}", baozouResponseDTO.getErrmsg());
        }else {
            log.error("转换口令失败, response={}", baozouResponseDTO);
        }
    }


    /**
     * @param botInfo 账号信息
     * @param group   qq群号
     * @param msgGet  消息
     */
    @Override
    public void consume(BotInfo botInfo, String group, MsgGet msgGet) {
        log.debug("[q1] 消费者：[{}] , 接收到消息：[{}] ==> 群qq：[{}]",
                botInfo.getBotCode(),
                msgGet.getId(),
                group);


        botInfo.getSender().SENDER.sendGroupMsg(group, msgGet.getMsg());
    }

}

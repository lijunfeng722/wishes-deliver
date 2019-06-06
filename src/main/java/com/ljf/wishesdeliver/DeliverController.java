package com.ljf.wishesdeliver;

import com.ljf.wishesdeliver.domain.*;
import com.ljf.wishesdeliver.service.DeliverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * @author JUNFENG
 * @date 2019/6/5
 */
@RestController("/")
public class DeliverController {
    @Autowired
    DeliverService deliverService;
    private static final String dataFileName = "data.txt";


    @GetMapping("/")
    public DeliveryReport deliverWish(/*List<Stock<Gift>> stocks, List<Candidate> candidates todo */) {

        List<Candidate> candidates = new ArrayList<>();
        List<Stock<Gift>> stocks = new ArrayList<>();
        loadDateFromFile(candidates, stocks, dataFileName);
        DeliveryReport deliveryReport = deliverService.deliverWish(stocks, candidates);
        saveReport2File(deliveryReport);

        return deliveryReport;
    }

    private void saveReport2File(DeliveryReport deliveryReport) {
        StringBuffer sb = new StringBuffer("分配结果：\n");
        List<DeliveryResult> deliveryResult = deliveryReport.getDeliveryResults();
        deliveryResult.forEach(result ->
                sb.append(result.getCandidate().getName())
                  .append(",")
                  .append(result.getGift().getName())
                  .append(",")
                  .append(result.getOrder())
                  .append("\n")
        );

        sb.append("未分配到礼物的同学：\n");
        List<Candidate> manWithOutGift = deliveryReport.getManWithOutGift();
        manWithOutGift.forEach(candidate -> {
                    sb.append(candidate.getName())
                      .append(",");
                    candidate.getWishes()
                             .stream()
                             .map(wish -> wish.getGift().getName())
                             .forEach(gitfName -> sb.append(gitfName).append(","));
                    sb.append("\n");
                }
        );

        sb.append("剩余的库存：\n");
        List<Stock> restGift = deliveryReport.getRestGift();
        restGift.forEach(stock -> sb.append(stock.getItem()).append(",").append(stock.getCount()).append(","));

        String s = sb.toString();

        System.out.println(s);

    }

    private void loadDateFromFile(List<Candidate> candidates, List<Stock<Gift>> stocks, String dataFileName) {
        try (BufferedReader inputStream = new BufferedReader(new FileReader(dataFileName))) {
            // 读取礼物列表
            String line = inputStream.readLine();
            List<String> giftName = Arrays.asList(line.split(","));
            List<Gift> gifts = giftName.stream().map(Gift::new).collect(toList());
            // 读取礼物库存
            line = inputStream.readLine();
            List<String> stockStr = Arrays.asList(line.split(","));
            List<Stock<Gift>> stocks1 = IntStream.range(0, gifts.size())
                                                 .mapToObj(i -> new Stock<Gift>(gifts.get(i), Integer.parseInt(stockStr.get(i))))
                                                 .collect(toList());
            stocks.addAll(stocks1);

            //获取candidates以及志愿
            while ((line = inputStream.readLine()) != null) {
                List<String> strings = Arrays.asList(line.split(","));
                String candidateName = strings.get(0);
                // 是否填写了志愿
                if (strings.size() > 1) {
                    List<Gift> wishes = strings.subList(1, strings.size()).stream().map(giftNameStr -> {
                        Gift gift = new Gift(giftNameStr);
                        if (!gifts.contains(gift)) {
                            throw new RuntimeException(candidateName + " 的愿望：" + gift.getName() + " 未出现在先前输入的愿望清单中" + giftName + "。");
                        }
                        return gift;
                    }).collect(toList());
                    candidates.add(new Candidate(candidateName, wishes));
                } else {
                    candidates.add(new Candidate(candidateName, true));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

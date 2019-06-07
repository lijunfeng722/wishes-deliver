package com.ljf.wishesdeliver.util;

import com.ljf.wishesdeliver.domain.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
/**
 * @author JUNFENG
 * @date 2019/6/7
 */
public class DataAccessUtil {
    public static void saveReport2File(DeliveryReport deliveryReport, String fileName) {
        String resultStr = createResultString(deliveryReport);

        File file = new File(fileName);
        FileWriter fileWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file.getName());
            fileWriter.write(resultStr);
            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static String createResultString(DeliveryReport deliveryReport) {
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

        sb.append("\n未分配到礼物的同学：\n");
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

        sb.append("\n剩余的库存：\n");
        List<Stock> restGift = deliveryReport.getRestGift();
        restGift.forEach(stock -> sb.append(stock.getItem()).append(",").append(stock.getCount()).append(","));

        return sb.append("\n").toString();
    }

    public static void loadDateFromFile(List<Candidate> candidates, List<Stock<Gift>> stocks, String dataFileName) {
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

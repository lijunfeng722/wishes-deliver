package com.ljf.wishesdeliver.service;

import com.ljf.wishesdeliver.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeliverServiceTest {

    static List<Stock<Gift>> stocks = new ArrayList<>();
    static List<Candidate> candidates = new ArrayList<>();

    static {
        Gift bag2 = new Gift("双肩包");
        Gift bag1 = new Gift("单肩包");

        stocks.add(new Stock<>(bag2, 7));
        stocks.add(new Stock<>(bag1, 8));


        candidates.add(new Candidate("周俊彦", bag2, bag1));
        candidates.add(new Candidate("熊永春", bag2, bag1));
        candidates.add(new Candidate("王雪晨", bag2, bag1));
        candidates.add(new Candidate("王淦", bag2, bag1));
        candidates.add(new Candidate("徐月华", bag2, bag1));
        candidates.add(new Candidate("陈科有", bag2, bag1));
        candidates.add(new Candidate("王志尚", bag2, bag1));
        candidates.add(new Candidate("陆先柱", bag2, bag1));

        candidates.add(new Candidate("罗勇闯", bag1, bag2));
        candidates.add(new Candidate("张娟", bag1, bag2));
        candidates.add(new Candidate("林帅", bag1, bag2));
        candidates.add(new Candidate("李素华", bag1, bag2));
        candidates.add(new Candidate("李俊锋", bag1, bag2));

        candidates.add(new Candidate("李俊锋1", bag1, bag2));
        candidates.add(new Candidate("李俊锋2", bag1, bag2));
        candidates.add(new Candidate("李俊锋3", true)); //fixme 在礼物数小于人数时，giveUp永远得不到礼物

    }

    @Autowired
    DeliverService deliverService;

    @Test
    public void deliverWish() {

        DeliveryReport deliveryReport = deliverService.deliverWish(stocks, candidates);
        System.out.println(deliveryReport);
    }

    @Test
    public void deliverWish_GET_INPUT_FROM_FILE() {
        List<Candidate> candidates1 = new ArrayList<>();
        try (BufferedReader inputStream = new BufferedReader(new FileReader("data.txt"))) {
            // 读取礼物列表
            String line = inputStream.readLine();
            List<String> giftName = Arrays.asList(line.split(","));
            List<Gift> gifts = giftName.stream().map(Gift::new).collect(toList());
            // 读取礼物库存
            line = inputStream.readLine();
            List<String> stockStr = Arrays.asList(line.split(","));
            List<Stock<Gift>> stocks = IntStream.range(0, gifts.size())
                                                .mapToObj(i -> new Stock<Gift>(gifts.get(i), Integer.parseInt(stockStr.get(i))))
                                                .collect(toList());

            //获取candidates以及志愿
            while ((line = inputStream.readLine()) != null) {
                List<String> strings = Arrays.asList(line.split(","));
                String candidateName = strings.get(0);
                // 是否填写了志愿
                if (strings.size() > 1) {
                    List<Gift> wishes = strings.subList(1, strings.size()).stream().map(giftNameStr -> {
                        Gift gift = new Gift(giftNameStr);
                        if (!gifts.contains(gift))
                            throw new RuntimeException(candidateName + " 的愿望：" + gift.getName() + " 未出现在先前输入的愿望清单中" + giftName + "。");
                        return gift;
                    }).collect(toList());
                    candidates1.add(new Candidate(candidateName, wishes));
                } else {
                    candidates1.add(new Candidate(candidateName, true));
                }

            }
            DeliveryReport deliveryReport = deliverService.deliverWish(stocks, candidates1);
            System.out.println(deliveryReport);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
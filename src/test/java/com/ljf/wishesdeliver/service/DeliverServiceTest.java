package com.ljf.wishesdeliver.service;

import com.ljf.wishesdeliver.DeliverController;
import com.ljf.wishesdeliver.domain.*;
import com.ljf.wishesdeliver.util.DataAccessUtil;
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
    @Autowired
    DeliverController deliverController;

    private static final String dataFileName = "data.txt";
    private static final String resultFileName = "result.txt";

    @Test
    public void deliverWish_DataFromFile() {
        List<Candidate> candidates = new ArrayList<>();
        List<Stock<Gift>> stocks = new ArrayList<>();
        DataAccessUtil.loadDateFromFile(candidates, stocks, dataFileName);
        DeliveryReport deliveryReport = deliverService.deliverWish(stocks, candidates);
        DataAccessUtil.saveReport2File(deliveryReport,resultFileName);
    }

    @Test
    public void deliverWish_dataFromCode() {
        DeliveryReport deliveryReport = deliverService.deliverWish(stocks, candidates);
        System.out.println(deliveryReport);
    }

}
package com.ljf.wishesdeliver.service;

import com.ljf.wishesdeliver.domain.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

import static com.ljf.wishesdeliver.domain.DeliveryResult.NO_GIFT;
import static com.ljf.wishesdeliver.domain.DeliveryResult.OUT_OF_WILLING;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * @author JUNFENG
 * @date 2019/6/5
 */
@Service
public class DeliverService {

    public DeliveryReport deliverWish(List<Stock<Gift>> stocks, List<Candidate> candidates) {
        List<DeliveryResult> deliveryResults = doDeliverWish(stocks, candidates);

        List<Candidate> manWithOutGift = deliveryResults.stream()
                                                        .filter(DeliveryResult::noGift)
                                                        .map(DeliveryResult::getCandidate)
                                                        .collect(toList());

        List<Stock<Gift>> restGift = stocks.stream().filter(giftStock -> giftStock.getCount() > 0).collect(toList());

        return new DeliveryReport(deliveryResults, manWithOutGift, restGift);

    }

    private List<DeliveryResult> doDeliverWish(List<Stock<Gift>> stocks, List<Candidate> candidates) {
        List<Wish> wishes = candidates.stream().flatMap(candidate -> candidate.getWishes().stream()).collect(toList());

        TreeMap<Integer, List<Wish>> wishesGroupByOrder =
                wishes.stream().collect(groupingBy(Wish::getOrder, TreeMap::new, toList()));

        List<DeliveryResult> results = new ArrayList<>();

        //根据志愿分配礼物
        wishesGroupByOrder.entrySet().forEach(entry -> {
            Integer order = entry.getKey();
            List<Wish> wishesInCurrentOrder = entry.getValue();
            Collections.shuffle(wishesInCurrentOrder);
            //按志愿顺序分配礼物,先分配一志愿，然后二志愿...
            wishesInCurrentOrder.stream()
                                .filter(wish -> stockNotEmpty(stocks, wish))// 想要的gift还没有被分配完
                                .filter(wish -> !candidateAlreadyHasGift(results, wish))
                                .forEach(wish -> {
                                    Gift wishGift = wish.getGift();
                                    results.add(new DeliveryResult(wish.getCandidate(), wishGift, order));
                                    Stock<Gift> giftStock = stocks.stream()
                                                                  .filter(stock -> stock.getItem().equals(wishGift))
                                                                  .findAny()
                                                                  .get();
                                    giftStock.countDown();
                                });
        });

        //给仍未分配到的人分配剩余的礼物
        List<Candidate> pityMan = someDoesNotHasGift(candidates, results);
        if (pityMan.size() > 0) {
            List<Stock<Gift>> restStock = stocks.stream().filter(stock -> stock.getCount() > 0).collect(toList());

            List<DeliveryResult> restDiliveryResult = pityMan.stream().map(candidate -> {
                Gift gift = NO_GIFT;
                if (restStock.size() > 0) {
                    Stock<Gift> stock = restStock.get(0);
                    stock.countDown();
                    if (stock.getCount() <= 0) {
                        restStock.remove(stock);
                    }
                    gift = stock.getItem();
                }
                return new DeliveryResult(candidate, gift, OUT_OF_WILLING);
            }).collect(toList());

            results.addAll(restDiliveryResult);
        }


        return results;


    }



    private List<Candidate> someDoesNotHasGift(List<Candidate> candidates, List<DeliveryResult> results) {
        return candidates.stream()
                         .filter(candidate -> results.stream()
                                                     .noneMatch(deliveryResult -> deliveryResult.getCandidate()
                                                                                                .equals(candidate)))
                         .collect(toList());
    }


    private boolean stockNotEmpty(List<Stock<Gift>> stocks, Wish wish) {
        Stock<Gift> giftStock = stocks.stream().filter(stock -> stock.getItem().equals(wish.getGift())).findAny().get();
        return giftStock.getCount() > 0;
    }

    private boolean candidateAlreadyHasGift(List<DeliveryResult> results, Wish wish) {
        return results.stream().anyMatch(result -> result.getCandidate().equals(wish.getCandidate()));
    }
}


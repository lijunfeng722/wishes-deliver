package com.ljf.wishesdeliver.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * 投递人
 *
 * @author JUNFENG
 * @date 2019/6/5
 */
@Data
public class Candidate {
    private String name;
    private List<Wish> wishes = new ArrayList<>();

    /**
     * 没有倾向，分配到什么都行
     */
    private boolean giveUp = false;


    public Candidate(String name, Gift... gifts) {
        this.name = name;
        this.wishes = IntStream.range(0, wishes.size())
                              .mapToObj(i -> new Wish(gifts[i], i + 1, this))
                              .collect(toList());
    }


    public Candidate(String name, boolean giveUp) {
        this.name = name;
        this.giveUp = giveUp;
    }

    public Candidate(String name, List<Gift> wishes) {
        this.name = name;
        this.wishes = IntStream.range(0, wishes.size())
                               .mapToObj(i -> new Wish(wishes.get(i), i + 1, this))
                               .collect(toList());
    }
}

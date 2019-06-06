package com.ljf.wishesdeliver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 * @author JUNFENG
 * @date 2019/6/5
 */
@Data
public class Wish {
    private Gift gift;
    /** 第几志愿 */
    private int order;
    @JsonIgnore
    private Candidate candidate;

    public Wish(Gift gift, int order,Candidate candidate) {
        this.gift = gift;
        this.order = order;
        this.candidate = candidate;
    }

    @Override
    public String toString() {
        return "Wish{" +
                "gift=" + gift +
                ", order=" + order +
                ", candidate=" + candidate.getName() +
                '}';
    }
}

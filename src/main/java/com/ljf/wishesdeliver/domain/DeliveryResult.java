package com.ljf.wishesdeliver.domain;

import lombok.Data;

/**
 * @author JUNFENG
 * @date 2019/6/5
 */
@Data
public class DeliveryResult {
    private final Candidate candidate;
    /**
     * 最终分发到的志愿
     */
    private final Gift gift;
    /**
     * 最终以几志愿中签
     */
    private final int order;
    /**
     * order = OUT_OF_WILLING 时代表不是以志愿中签的
     */
    public static final int OUT_OF_WILLING = Integer.MAX_VALUE;
    public static final Gift NO_GIFT = new Gift("抱抱\\(￣︶￣*\\))");

    @Override
    public String toString() {
        return "candidate=" + candidate.getName() +
                ", gift=" + gift +
                ", order=" + order ;
    }

    public boolean noGift() {
        return this.getGift() == NO_GIFT;
    }
}

package com.ljf.wishesdeliver.domain;

import lombok.Data;

import java.util.List;

/**
 * @author JUNFENG
 * @date 2019/6/5
 */
@Data
public class DeliveryReport<T> {
    private final List<DeliveryResult> deliveryResults;
    private final List<Candidate> manWithOutGift;
    /** 剩余的礼物 */
    private final List<Stock<T>> restGift;

}

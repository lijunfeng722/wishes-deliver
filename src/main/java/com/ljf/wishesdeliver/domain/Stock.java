package com.ljf.wishesdeliver.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 库存
 *
 * @author JUNFENG
 * @date 2019/6/5
 */
@Data
@AllArgsConstructor
public class Stock<T> {
    private final T item;
    private int count;

    public void countDown() {
        if (count <= 0) {
            throw new RuntimeException("配额已等于零！");
        }
        count--;
    }
}

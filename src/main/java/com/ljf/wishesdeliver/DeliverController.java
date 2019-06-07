package com.ljf.wishesdeliver;

import com.ljf.wishesdeliver.domain.*;
import com.ljf.wishesdeliver.service.DeliverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author JUNFENG
 * @date 2019/6/5
 */
@RestController("/")
public class DeliverController {
    @Autowired
    DeliverService deliverService;

    @GetMapping("/")
    public DeliveryReport deliverWish(List<Stock<Gift>> stocks, List<Candidate> candidates) {

        DeliveryReport deliveryReport = deliverService.deliverWish(stocks, candidates);

        return deliveryReport;
    }

}

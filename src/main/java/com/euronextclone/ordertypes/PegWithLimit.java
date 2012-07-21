package com.euronextclone.ordertypes;

import com.euronextclone.Order;
import com.euronextclone.OrderType;
import com.euronextclone.OrderTypeLimit;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 7/20/12
 * Time: 2:36 PM
 */
public class PegWithLimit extends OrderTypeLimit {

    private final double limit;

    public PegWithLimit(final double limit) {
        super(OrderType.Peg, limit);
        this.limit = limit;
    }

    @Override
    public boolean providesLimit() {
        return false;
    }

    @Override
    public boolean canPegLimit() {
        return true;
    }

    @Override
    public Double price(Order.OrderSide side, Double bestLimit) {
        if (side == Order.OrderSide.Buy && bestLimit <= limit) {
            return bestLimit;
        } else if (side == Order.OrderSide.Sell && bestLimit >= limit) {
            return bestLimit;
        } else {
            return limit;
        }
    }
}

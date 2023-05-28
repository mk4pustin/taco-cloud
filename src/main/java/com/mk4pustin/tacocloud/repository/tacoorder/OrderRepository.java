package com.mk4pustin.tacocloud.repository.tacoorder;

import com.mk4pustin.tacocloud.data.order.TacoOrder;

public interface OrderRepository {
    TacoOrder save(TacoOrder order);
}

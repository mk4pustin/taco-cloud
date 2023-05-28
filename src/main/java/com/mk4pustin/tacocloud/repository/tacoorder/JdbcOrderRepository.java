package com.mk4pustin.tacocloud.repository.tacoorder;

import com.mk4pustin.tacocloud.data.IngredientRef;
import com.mk4pustin.tacocloud.data.order.TacoOrder;
import com.mk4pustin.tacocloud.data.taco.Taco;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
public class JdbcOrderRepository implements OrderRepository {
    private JdbcOperations jdbcOperations;

    public JdbcOrderRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }


    @Override
    @Transactional
    public TacoOrder save(TacoOrder order) {
        final var pscf = new PreparedStatementCreatorFactory(
                "insert into Taco_Order "
                        + "(delivery_name, delivery_street, delivery_city, "
                        + "delivery_region, delivery_zip, cc_number, "
                        + "cc_expiration, cc_cvv, placed_at) "
                        + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP
        );
        pscf.setReturnGeneratedKeys(true);

        order.setPlacedAt(new Date());
        final var psc = pscf.newPreparedStatementCreator(
                Arrays.asList(
                        order.getDeliveryName(),
                        order.getDeliveryStreet(),
                        order.getDeliveryCity(),
                        order.getDeliveryRegion(),
                        order.getDeliveryZip(),
                        order.getCcNumber(),
                        order.getCcExpiration(),
                        order.getCcCVV(),
                        order.getPlacedAt())
        );

        final var keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(psc, keyHolder);

        final var orderId = keyHolder.getKey().longValue();
        order.setId(orderId);

        final var tacos = order.getTacos();
        var i = 0;
        for (var taco : tacos) saveTaco(orderId, i++, taco);

        return order;
    }

    private long saveTaco(Long orderId, int orderKey, Taco taco) {
        taco.setCreatedAt(new Date());
        final var pscf = new PreparedStatementCreatorFactory(
                "insert into Taco "
                        + "(name, created_at, taco_order, taco_order_key) "
                        + "values (?, ?, ?, ?)",
                Types.VARCHAR, Types.TIMESTAMP, Types.BIGINT, Types.BIGINT
        );

        pscf.setReturnGeneratedKeys(true);

        final var psc = pscf.newPreparedStatementCreator(
                Arrays.asList(
                        taco.getName(),
                        taco.getCreatedAt(),
                        orderId,
                        orderKey)
        );

        final var keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(psc, keyHolder);

        var tacoId = keyHolder.getKey().longValue();
        taco.setId(tacoId);

        saveIngredientRefs(tacoId, taco.getIngredients());

        return tacoId;
    }

    private void saveIngredientRefs(
            long tacoId, List<IngredientRef> ingredientRefs) {
        var key = 0;
        for (var ingRef : ingredientRefs) {
            jdbcOperations.update(
                    "insert into Ingredient_Ref (ingredient, taco, taco_key) "
                    + "values (?, ?, ?)",
                    ingRef.getIngredient(), tacoId, key++);
        }
    }
}

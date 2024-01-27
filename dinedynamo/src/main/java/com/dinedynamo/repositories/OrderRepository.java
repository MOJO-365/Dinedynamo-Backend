package com.dinedynamo.repositories;

import com.dinedynamo.collections.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {


    List<Order> findByRestaurantId(String restaurantId);

    Optional<Order> findByTableId(String tableId);


}







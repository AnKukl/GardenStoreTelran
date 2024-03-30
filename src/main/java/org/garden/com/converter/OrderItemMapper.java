package org.garden.com.converter;


import org.garden.com.dto.OrderItemDto;
import org.garden.com.entity.Order;
import org.garden.com.entity.OrderItem;
import org.garden.com.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "priceAtPurchase", target = "priceAtPurchase")
    OrderItemDto orderItemToOrderItemDto(OrderItem orderItem);

    @Mapping(source = "orderId", target = "order")
    @Mapping(source = "productId", target = "product")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "priceAtPurchase", target = "priceAtPurchase")
    OrderItem orderItemDtoToOrderItem(OrderItemDto orderItemDto);

    default Order map(Long orderId) {
        if (orderId == null) {
            return null;
        }
        Order order = new Order();
        order.setId(orderId);
        return order;
    }

    @Mapping(source = "product.id", target = "productId")
    OrderItemDto orderToOrderItemDto(OrderItem orderItem);

    default Product mapProductIdToProduct(Long productId) {
        if (productId == null) {
            return null;
        }
        Product product = new Product();
        product.setId(productId);
        return product;
    }

    default Long map(Product product) {
        return product.getId();
    }
}

package com.fawry.orderservice.service;

import com.fawry.orderservice.dto.*;
import com.fawry.orderservice.entity.Order;
import com.fawry.orderservice.entity.OrderItem;
import com.fawry.orderservice.mapper.OrderMapper;
import com.fawry.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final WebClientService webClientService;
  private final OrderRepository orderRepository;

  @Autowired private OrderMapper orderMapper;

  @Value("${system_bank_number}")
  private String systemBankNumber;

  @Value("${system_bank_cvv}")
  private String bankAccountCvv;

  @Override
  public OrderDto createOrder(OrderRequestModel orderRequestModel) {
    validateProductAvailability(orderRequestModel.getItems());

    List<ProductResponseDTO> products = fetchProducts(orderRequestModel);

    double totalAmount = calculateTotalAmount(orderRequestModel.getItems(), products);

    double totalAmountAfterDiscount = applyDiscountIfAvailable(orderRequestModel, totalAmount);

    processPayment(orderRequestModel.getCardNumber(), totalAmountAfterDiscount);

    Order createdOrder = saveOrder(orderRequestModel, products, totalAmountAfterDiscount);

    updateStock(orderRequestModel.getItems());

    consumeCoupon(orderRequestModel, createdOrder.getId());

    sendOrderNotification(orderRequestModel.getCustomerEmail(), totalAmountAfterDiscount);
    return null;
  }

  @Override
  public List<OrderDto> findOrdersByGuestEmail(String customerEmail) {
    List<Order> orders = orderRepository.findOrdersByCustomerEmail(customerEmail);
    return orders.stream().map(order -> orderMapper.toOrderDto(order)).toList();
  }

  @Override
  public List<OrderDto> findOrdersByCreatedAtBetween(Date from, Date to) {
    List<Order> orders = orderRepository.findOrdersByCreatedAtBetween(from, to);
    return orders.stream().map(order -> orderMapper.toOrderDto(order)).toList();
  }

  private void validateProductAvailability(List<ItemRequestModel> items) {
    webClientService.validateProductOutOfStock(items);
  }

  private List<ProductResponseDTO> fetchProducts(OrderRequestModel orderRequestModel) {
    List<Integer> productIds =
        orderRequestModel.getItems().stream()
            .map(ItemRequestModel::getProductId)
            .collect(Collectors.toList());
    return webClientService.getProducts(productIds);
  }

  private double calculateTotalAmount(
      List<ItemRequestModel> items, List<ProductResponseDTO> products) {
    Map<Integer, ProductResponseDTO> productMap =
        products.stream().collect(Collectors.toMap(ProductResponseDTO::getId, p -> p));

    return items.stream()
        .mapToDouble(
            item -> {
              ProductResponseDTO product = productMap.get(item.getProductId());
              if (product == null) {
                throw new RuntimeException("Product not found");
              }
              return product.getPrice() * item.getQuantity();
            })
        .sum();
  }

  private double applyDiscountIfAvailable(OrderRequestModel orderRequestModel, double totalAmount) {
    if (orderRequestModel.getCouponCode() == null) {
      return totalAmount;
    }

    webClientService.validateCoupon(
        orderRequestModel.getCouponCode(), orderRequestModel.getCustomerEmail());
    return webClientService.calculateDiscount(
        orderRequestModel.getCouponCode(), orderRequestModel.getCustomerEmail(), totalAmount);
  }

  private void processPayment(String cardNumber, double amount) {
    TransactionModel customerTransaction = createTransactionModel(cardNumber, amount, "Draw");
    webClientService.withdrawInvoiceAmountFromGuestBankAccount(customerTransaction);

    TransactionModel merchantTransaction = createTransactionModel(systemBankNumber, amount, "Add");
    webClientService.depositInvoiceAmountIntoMerchantBankAccount(merchantTransaction);
  }

  private TransactionModel createTransactionModel(String cardNumber, double amount, String method) {
    return TransactionModel.builder().CardNumber(cardNumber).amount(amount).method(method).build();
  }

  private Order saveOrder(
      OrderRequestModel orderRequestModel,
      List<ProductResponseDTO> products,
      double totalAmountAfterDiscount) {
    List<OrderItem> orderItems = createOrderItems(orderRequestModel.getItems(), products);

    Order order =
        Order.builder()
            .customerEmail(orderRequestModel.getCustomerEmail())
            .couponCode(orderRequestModel.getCouponCode())
            .amount(totalAmountAfterDiscount)
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .updatedAt(new Timestamp(System.currentTimeMillis()))
            .orderItems(orderItems)
            .build();

    orderItems.forEach(item -> item.setOrder(order));

    return orderRepository.save(order);
  }

  private List<OrderItem> createOrderItems(
      List<ItemRequestModel> items, List<ProductResponseDTO> products) {
    Map<Integer, ProductResponseDTO> productMap =
        products.stream().collect(Collectors.toMap(ProductResponseDTO::getId, p -> p));

    return items.stream()
        .map(
            item -> {
              ProductResponseDTO product = productMap.get(item.getProductId());
              if (product == null) {
                throw new RuntimeException("Product not found");
              }
              return OrderItem.builder()
                  .productId(item.getProductId())
                  .price(product.getPrice())
                  .quantity(item.getQuantity())
                  .build();
            })
        .collect(Collectors.toList());
  }

  private void updateStock(List<ItemRequestModel> items) {
    webClientService.consumeStock(items);
  }

  private void consumeCoupon(OrderRequestModel orderRequestModel, Integer orderId) {

    if (orderRequestModel.getCouponCode() == null) {
      return;
    }
    CouponRequsetModel couponRequest =
        CouponRequsetModel.builder()
            .couponCode(orderRequestModel.getCouponCode())
            .orderId(orderId)
            .userEmail(orderRequestModel.getCustomerEmail())
            .build();

    webClientService.consumeCoupon(couponRequest);
  }

  private void sendOrderNotification(String customerEmail, double totalAmount) {
    String message = "Order completed with total price " + totalAmount;

    NotificationDto notificationDto =
        NotificationDto.builder().message(message).customerEmail(customerEmail).build();

    webClientService.sendOrderDetailsToNotificationsAPI(notificationDto);
  }
}

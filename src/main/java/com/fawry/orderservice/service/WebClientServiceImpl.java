package com.fawry.orderservice.service;

import com.fawry.orderservice.dto.*;
import com.fawry.orderservice.error.GlobalError;
import com.fawry.orderservice.error.IdsRequestError;
import com.fawry.orderservice.error.ProductErrorModel;
import com.fawry.orderservice.exception.ClientException;
import com.fawry.orderservice.exception.ProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.View;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebClientServiceImpl implements WebClientService {

  private final WebClient.Builder webClient;
  private final View error;

  @Override
  public void validateCoupon(String couponCode, String customerEmail) {

    webClient
        .build()
        .get()
        .uri(
            "http://localhost:7070/consumptions/validate",
            uriBuilder ->
                uriBuilder
                    .queryParam("couponCode", couponCode)
                    .queryParam("userEmail", customerEmail)
                    .build())
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            clientResponse ->
                clientResponse
                    .bodyToMono(GlobalError.class)
                    .flatMap(
                        globalError -> Mono.error(new ClientException(globalError.getMessage()))))
        .bodyToMono(String.class)
        .block();
  }

  @Override
  public void validateProductOutOfStock(List<ItemRequestModel> itemRequestModels) {

    webClient
        .build()
        .post()
        .uri("http://localhost:8080/stocks/check")
        .bodyValue(itemRequestModels)
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            clientResponse ->
                clientResponse
                    .bodyToMono(ProductErrorModel.class)
                    .flatMap(error -> Mono.error(new ProductException(error.getDetails()))))
        .bodyToMono(String.class)
        .block();
  }

  @Override
  public List<ProductResponseDTO> getProducts(List<Integer> ids) {

    return webClient
        .build()
        .post()
        .uri("http://localhost:5050/products/find-by-ids")
        .bodyValue(ids)
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            clientResponse ->
                clientResponse
                    .bodyToMono(IdsRequestError.class)
                    .flatMap(error -> Mono.error(new ClientException(error.getMessage()))))
        .bodyToFlux(ProductResponseDTO.class)
        .collectList()
        .block();
  }

  @Override
  public double calculateDiscount(String couponCode, String customerEmail, double invoiceAmount) {

    return webClient
        .build()
        .get()
        .uri(
            "http://localhost:7070/consumptions/calculate-amount",
            uriBuilder ->
                uriBuilder
                    .queryParam("couponCode", couponCode)
                    .queryParam("amount", invoiceAmount)
                    .build())
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            clientResponse ->
                clientResponse
                    .bodyToMono(ClientException.class)
                    .flatMap(error -> Mono.error(new ClientException(error.getMessage()))))
        .bodyToMono(Double.class)
        .block()
        .doubleValue();
  }

  @Override
  public void withdrawInvoiceAmountFromGuestBankAccount(TransactionModel transactional) {
    webClient
        .build()
        .put()
        .uri("http://localhost:6060/account/transaction")
        .bodyValue(transactional)
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            clientResponse ->
                clientResponse
                    .bodyToMono(ClientException.class)
                    .flatMap(error -> Mono.error(new ClientException(error.getMessage()))))
        .bodyToMono(void.class)
        .block();
  }

  @Override
  public void depositInvoiceAmountIntoMerchantBankAccount(TransactionModel transactional) {
    webClient
        .build()
        .put()
        .uri("http://localhost:6060/account/transaction")
        .bodyValue(transactional)
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            clientResponse ->
                clientResponse
                    .bodyToMono(ClientException.class)
                    .flatMap(error -> Mono.error(new ClientException(error.getMessage()))))
        .bodyToMono(void.class)
        .block();
  }

  @Override
  public void consumeStock(List<ItemRequestModel> itemRequests) {

    webClient
        .build()
        .post()
        .uri("http://localhost:8080/stocks/consume")
        .bodyValue(itemRequests)
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            clientResponse ->
                clientResponse
                    .bodyToMono(ClientException.class)
                    .flatMap(error -> Mono.error(new ClientException(error.getMessage()))))
        .bodyToMono(String.class)
        .block();
  }

  @Override
  public void consumeCoupon(CouponRequsetModel couponRequest) {

    webClient
        .build()
        .post()
        .uri("http://localhost:7070/consumptions/create")
        .bodyValue(couponRequest)
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            clientResponse ->
                clientResponse
                    .bodyToMono(ClientException.class)
                    .flatMap(error -> Mono.error(new ClientException(error.getMessage()))))
        .bodyToMono(void.class)
        .block();
  }

  @Override
  public void sendOrderDetailsToNotificationsAPI(NotificationDto notificationDto) {

    webClient
        .build()
        .post()
        .uri("http://localhost:8083/sendOrderNotification")
        .bodyValue(notificationDto)
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            clientResponse ->
                clientResponse
                    .bodyToMono(ClientException.class)
                    .flatMap(error -> Mono.error(new ClientException(error.getMessage()))))
        .bodyToMono(String.class)
        .block();
  }

  @Override
  public StoreResponseDto getStoreById(long storeId) {
    return webClient
        .build()
        .get()
        .uri("http://localhost:8080/stores")
        .attribute("id", storeId)
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            clientResponse ->
                clientResponse
                    .bodyToMono(ClientException.class)
                    .flatMap(error -> Mono.error(new ClientException(error.getMessage()))))
        .bodyToMono(StoreResponseDto.class)
        .block();
  }
}

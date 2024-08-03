package com.fawry.orderservice.service;

import com.fawry.orderservice.dto.CouponRequsetModel;
import com.fawry.orderservice.dto.ItemRequestModel;
import com.fawry.orderservice.dto.ProductResponseModel;
import com.fawry.orderservice.dto.TransactionModel;
import com.fawry.orderservice.error.GlobalError;
import com.fawry.orderservice.error.IdsRequestError;
import com.fawry.orderservice.exception.ClientException;
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

  private final View error;
  private final WebClient.Builder webClient;

  @Override
  public void validateCoupon(String couponCode, String customerEmail) {

    webClient
        .build()
        .get()
        .uri(
            "http://localhost:8080/consumptions/validate",
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
        .uri("")
        .bodyValue(itemRequestModels)
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            clientResponse ->
                clientResponse
                    .bodyToMono(IdsRequestError.class)
                    .flatMap(error -> Mono.error(new ClientException(error.getMessage()))))
        .bodyToMono(void.class)
        .block();
  }

  @Override
  public List<ProductResponseModel> getProducts(List<Integer> productIds) {

    return webClient
        .build()
        .get()
        .uri("", uriBuilder -> uriBuilder.queryParam("ids", productIds).build())
        .retrieve()
        .onStatus(
            HttpStatusCode::is4xxClientError,
            clientResponse ->
                clientResponse
                    .bodyToMono(IdsRequestError.class)
                    .flatMap(error -> Mono.error(new ClientException(error.getMessage()))))
        .bodyToFlux(ProductResponseModel.class)
        .collectList()
        .block();
  }

  @Override
  public double calculateDiscount(String couponCode, String customerEmail, double invoiceAmount) {

    return webClient
        .build()
        .get()
        .uri(
            "http://localhost:8080/consumptions/calculate-amount",
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
  public void withdrawInvoiceAmountFromGuestBankAccount(TransactionModel withdrawRequestModel) {
    webClient
        .build()
        .post()
        .uri("")
        .bodyValue(withdrawRequestModel)
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
  public void depositInvoiceAmountIntoMerchantBankAccount(TransactionModel depositRequestModel) {
    webClient
        .build()
        .post()
        .uri("")
        .bodyValue(depositRequestModel)
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
        .uri("")
        .bodyValue(itemRequests)
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
  public void consumeCoupon(CouponRequsetModel couponRequest) {

    webClient
        .build()
        .post()
        .uri("http://localhost:8080/consumptions/create")
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
}

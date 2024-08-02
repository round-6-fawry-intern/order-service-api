package com.fawry.orderservice.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdsRequestError {

    private String message;

//    private Timestamp occurredOn;

}

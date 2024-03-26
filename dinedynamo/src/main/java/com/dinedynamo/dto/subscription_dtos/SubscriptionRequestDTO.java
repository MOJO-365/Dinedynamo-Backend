package com.dinedynamo.dto.subscription_dtos;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubscriptionRequestDTO
{
    String subscriptionPlanId;

    boolean isPaymentDone;

    String restaurantId;

    LocalDate startDate;

    LocalDate endDate;


}

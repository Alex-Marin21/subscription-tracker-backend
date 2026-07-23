package com.subtracker.subscriptiontracker.controller;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for managing Stripe payment integrations.
 * Facilitates the creation of secure checkout sessions for users.
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    /**
     * Initializes the PaymentController and configures the Stripe API key from environment variables.
     */
    public PaymentController() {
        Stripe.apiKey = System.getenv("STRIPE_SECRET_KEY");
    }

    /**
     * Creates a new Stripe Checkout Session for the Premium Lifetime Access product.
     * Generates a payment URL to which the client should be redirected.
     *
     * @return a ResponseEntity containing the generated Stripe Checkout URL
     */
    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession() {
        try {
            String frontendUrl = "https://subscription-tracker-app-bsfh.onrender.com";
            
            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "?success=true")
                .setCancelUrl(frontendUrl + "?canceled=true")
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmount(1000L)
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Premium Lifetime Access")
                                        .setDescription("Unlock unlimited subscription tracking")
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build();

            Session session = Session.create(params);
            
            Map<String, String> responseData = new HashMap<>();
            responseData.put("url", session.getUrl());
            
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}

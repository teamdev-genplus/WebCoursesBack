package com.aecode.webcoursesback.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.aecode.webcoursesback.dtos.PayPalOrderDTO;
import com.aecode.webcoursesback.services.PayPalService;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

@RestController
@RequestMapping("/payment")
public class PayPalController {
    @Autowired
    private PayPalService PayPalService;

    @PostMapping("/pay")
    public String pay(@RequestBody PayPalOrderDTO order) {
        System.out.println("Received order: " + order);

        try {
            Payment payment = PayPalService.createPayment(order);
            for (com.paypal.api.payments.Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return link.getHref();
                }
            }
        } catch (PayPalRESTException e) {

            e.printStackTrace();
        }
        return "Error creating PayPal payment";
    }

    @GetMapping("/success")
    public String success(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = PayPalService.executePayment(paymentId, payerId);
            System.out.println(payment.toJSON());
            return "Payment successful";
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return "Payment failed";
    }
}

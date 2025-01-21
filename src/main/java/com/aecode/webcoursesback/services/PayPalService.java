package com.aecode.webcoursesback.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aecode.webcoursesback.dtos.PayPalOrderDTO;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

@Service
public class PayPalService {

    @Autowired
    private APIContext apiContext;

    public Payment createPayment(PayPalOrderDTO orderDTO) throws PayPalRESTException {
        // Depurando la información recibida
        System.out.println("Received orderDTO: " + orderDTO);

        Amount amount = new Amount();
        amount.setCurrency(orderDTO.getCurrency());
        amount.setTotal(String.format("%.2f", orderDTO.getTotal()));

        // Depurando el monto
        System.out.println("Amount set to: " + amount.getTotal() + " " + amount.getCurrency());

        Transaction transaction = new Transaction();
        transaction.setDescription(orderDTO.getDescription());
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(orderDTO.getMethod());

        // Depurando el método de pago
        System.out.println("Payment method set to: " + payer.getPaymentMethod());

        Payment payment = new Payment();
        payment.setIntent(orderDTO.getIntent());
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(orderDTO.getCancelUrl());
        redirectUrls.setReturnUrl(orderDTO.getSuccessUrl());

        // Depurando las URLs de redirección
        System.out.println("Cancel URL 1: " + redirectUrls.getCancelUrl());
        System.out.println("Success URL 1: " + redirectUrls.getReturnUrl());

        payment.setRedirectUrls(redirectUrls);

        // Depurando el pago
        System.out.println("Payment created 1: " + payment.toJSON());

        System.out.println("API context: " + apiContext);

        // Realizando la solicitud a PayPal
        Payment createdPayment = payment.create(apiContext);

        // Depurando el resultado del pago
        System.out.println("Payment created 2: " + createdPayment.toJSON());

        return createdPayment;
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        // Depurando los parámetros recibidos
        System.out.println("Executing payment with paymentId: " + paymentId + " and payerId: " + payerId);

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        // Realizando la ejecución del pago
        Payment executedPayment = payment.execute(apiContext, paymentExecution);

        // Depurando el resultado de la ejecución del pago
        System.out.println("Payment executed: " + executedPayment.toJSON());

        return executedPayment;
    }
}

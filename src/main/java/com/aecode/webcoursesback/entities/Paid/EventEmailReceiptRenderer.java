package com.aecode.webcoursesback.entities.Paid;
import com.aecode.webcoursesback.entities.UserProfile;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EventEmailReceiptRenderer {

    private static String safe(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;")
                .replace(">","&gt;").replace("\"","&quot;");
    }

    private static String fmt(BigDecimal amount, String currency) {
        double v = amount == null ? 0.0 : amount.doubleValue();
        return String.format(Locale.US, "%.2f %s", v, currency);
    }

    private static String methodLabel(PaymentReceipt.PaymentMethod m) {
        if (m == null) return "Pago";
        return switch (m) {
            case PAYPAL -> "PayPal";
            case YAPE   -> "Yape";
            case PLIN   -> "Plin";
            case TRANSFER -> "Transferencia";
        };
    }

    /** HTML para usuario (idéntico al de Izipay EVENT, con método dinámico). */
    public static String renderUserHtml(UserProfile user,
                                        String eventTitle,
                                        String planTitle,
                                        int quantity,
                                        String currency,
                                        BigDecimal unitPriceShown,
                                        BigDecimal subtotal,
                                        BigDecimal discount,
                                        BigDecimal commission,
                                        BigDecimal total,
                                        String purchaseNumber,
                                        OffsetDateTime purchasedAt,
                                        PaymentReceipt.PaymentMethod method) {

        if (purchasedAt == null) purchasedAt = OffsetDateTime.now();
        if (currency == null || currency.isBlank()) currency = "PEN";
        if (eventTitle == null || eventTitle.isBlank()) eventTitle = "Evento";
        if (planTitle == null || planTitle.isBlank()) planTitle = "Plan";
        if (unitPriceShown == null) unitPriceShown = BigDecimal.ZERO;
        if (subtotal == null) subtotal = BigDecimal.ZERO;
        if (discount == null) discount = BigDecimal.ZERO;
        if (commission == null) commission = BigDecimal.ZERO;
        if (total == null) total = BigDecimal.ZERO;

        String numeroCompra = (purchaseNumber == null || purchaseNumber.isBlank())
                ? DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(purchasedAt)
                : purchaseNumber;

        java.util.Locale locale = new java.util.Locale("es", "ES");
        String fechaCompra = purchasedAt.toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", locale));

        StringBuilder eventoHtmlBuilder = new StringBuilder();
        eventoHtmlBuilder.append(
                "<tr>" +
                        "<td align=\"center\" style=\"padding:5px 40px; font-family:verdana, geneva, sans-serif; font-size:14px; color:#333333;\">" +
                        "<strong>" + safe(eventTitle) + " — " + safe(planTitle) + "</strong> &times; " + quantity +
                        "</td>" +
                        "<td align=\"center\" style=\"padding:5px 40px; font-family:verdana, geneva, sans-serif; font-size:14px; color:#333333; width:85px;\">" +
                        fmt(unitPriceShown, currency) +
                        "</td>" +
                        "</tr>"
        );

        // HTML (idéntico al de Izipay EVENT; solo cambia el texto del método)
        String userHtml =
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                        "<html dir=\"ltr\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" lang=\"es\">\n" +
                        " <head>\n" +
                        "  <meta charset=\"UTF-8\">\n" +
                        "  <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">\n" +
                        "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
                        "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                        "  <meta content=\"telephone=no\" name=\"format-detection\">\n" +
                        "  <title>¡Confirmación de Compra! AECODE Training 📚🎉</title>\n" +
                        "  <style type=\"text/css\"></style>\n" +
                        " </head>\n" +
                        " <body class=\"body\" style=\"width:100%;height:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0;background-color:#FAFAFA\">\n" +
                        "  <div dir=\"ltr\" class=\"es-wrapper-color\" lang=\"es\" style=\"background-color:#FAFAFA\">\n" +
                        "   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" class=\"es-wrapper\" role=\"none\" style=\"border-collapse:collapse;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;background-color:#FAFAFA\">\n" +
                        "     <tr>\n" +
                        "      <td valign=\"top\" style=\"padding:0;Margin:0\">\n" +
                        "       <table cellpadding=\"0\" cellspacing=\"0\" align=\"center\" class=\"es-header\" role=\"none\" style=\"border-collapse:collapse;width:100%;table-layout:fixed !important;background-color:transparent;background-repeat:repeat;background-position:center top\">\n" +
                        "         <tr>\n" +
                        "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                        "           <table bgcolor=\"#ffffff\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" class=\"es-header-body\" role=\"none\" style=\"border-collapse:collapse;background-color:transparent;width:600px\">\n" +
                        "             <tr><td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
                        "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"none\" style=\"border-collapse:collapse\">\n" +
                        "                 <tr><td valign=\"top\" align=\"center\" class=\"es-m-p0r\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"border-collapse:collapse\">\n" +
                        "                     <tr><td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\">" +
                        "                       <img src=\"https://euqtuhd.stripocdn.email/content/guids/CABINET_d1422edc264bd643c8af51440e8995acef2448ffb48805c1983bece0ea0a568e/images/channels4_banner.jpg\" alt=\"Logo\" width=\"560\" title=\"Logo\" class=\"adapt-img\" style=\"display:block;border:0;outline:none;text-decoration:none;border-radius:0\">" +
                        "                     </td></tr>\n" +
                        "                   </table></td></tr>\n" +
                        "               </table></td></tr>\n" +
                        "           </table></td></tr>\n" +
                        "       </table>\n" +
                        "       <table cellpadding=\"0\" cellspacing=\"0\" align=\"center\" class=\"es-content\" role=\"none\" style=\"border-collapse:collapse;width:100%;table-layout:fixed !important\">\n" +
                        "         <tr><td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                        "           <table bgcolor=\"#ffffff\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" class=\"es-content-body\" role=\"none\" style=\"border-collapse:collapse;background-color:#FFFFFF;width:600px\">\n" +
                        "             <tr><td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px;padding-right:20px;padding-left:20px\">\n" +
                        "               <table cellpadding=\"0\" cellspacing=\"0\" align=\"left\" class=\"es-left\" role=\"none\" style=\"border-collapse:collapse;float:left\">\n" +
                        "                 <tr><td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"border-collapse:collapse\">\n" +
                        "                     <tr><td align=\"center\" class=\"es-text-4557\" style=\"padding:0;Margin:0;padding-bottom:10px\"><p class=\"es-m-txt-c es-text-mobile-size-36\" style=\"Margin:0;line-height:36px;color:#333333;font-size:36px\"><strong>Confirmación de Compra</strong></p></td></tr>\n" +
                        "                     <tr><td align=\"center\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;line-height:21px;color:#333333;font-size:14px\">¡Felicidades! Tu compra se procesó correctamente</p></td></tr>\n" +
                        "                     <tr><td align=\"center\" style=\"padding:10px 0 10px 0; font-size:20px; color:#5C68E2; font-weight:bold;\">\n" +
                        "                        Nro. Compra <a target=\"_blank\" style=\"text-decoration:underline;color:#5C68E2;\" href=\"#\">#" + safe(numeroCompra) + "</a>\n" +
                        "                     </td></tr>\n" +
                        "                     <tr><td align=\"center\" style=\"padding:0;Margin:0; font-size:14px; color:#333333;\">" +
                        "                        " + safe(fechaCompra) + "\n" +
                        "                     </td></tr>\n" +
                        "                     <tr><td align=\"center\" style=\"padding:10px 0 15px 0; font-size:14px; color:#333333;\">\n" +
                        "                        Gracias por tu compra en <strong>AECODE Training.</strong>\n" +
                        "                     </td></tr>\n" +
                        "                   </table></td></tr>\n" +
                        "               </table></td></tr>\n" +
                        "             <tr><td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
                        "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"none\" style=\"border-collapse:collapse\">\n" +
                        "                 <tr><td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"border-collapse:collapse\">\n" +
                        "                     <tbody>" + eventoHtmlBuilder + "</tbody>\n" +
                        "                   </table>\n" +
                        "                 </td></tr>\n" +
                        "                 <tr><td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-collapse:collapse;border-top:2px solid #efefef;border-bottom:2px solid #efefef\" role=\"presentation\">\n" +
                        "                     <tr><td align=\"right\" style=\"padding:0;Margin:0;padding-top:10px;padding-bottom:20px\">\n" +
                        "                       <p style=\"Margin:0;line-height:21px;color:#333333;font-size:14px\">" +
                        "Subtotal: <strong>" + fmt(subtotal, currency) + "</strong><br>" +
                        "Descuentos: <strong>" + fmt(discount, currency) + "</strong><br>" +
                        "Comisión: <strong>" + fmt(commission, currency) + "</strong><br>" +
                        "Total: <strong>" + fmt(total, currency) + "</strong>" +
                        "</p>\n" +
                        "                     </td></tr>\n" +
                        "                   </table>\n" +
                        "                 </td></tr>\n" +
                        "                 <tr><td align=\"left\" style=\"Margin:0;padding-right:20px;padding-left:20px;padding-bottom:10px;padding-top:20px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" align=\"left\" class=\"es-left\" role=\"none\" style=\"float:left\">\n" +
                        "                     <tr><td align=\"center\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                       <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"border-collapse:collapse\">\n" +
                        "                         <tr><td align=\"left\" style=\"padding:0;Margin:0;font-size:12px;color:#333333;line-height:18px\">\n" +
                        "                           <p>Cliente: <strong>" + safe(user.getEmail()) + "</strong></p>\n" +
                        "                           <p>Número de Compra: <strong>#" + safe(numeroCompra) + "</strong></p>\n" +
                        "                           <p>Fecha: <strong>" + safe(fechaCompra) + "</strong></p>\n" +
                        "                           <p>Método de Pago: <strong>" + safe(methodLabel(method)) + "</strong></p>\n" +
                        "                           <p>Moneda: <strong>" + safe(currency) + "</strong></p>\n" +
                        "                         </td></tr>\n" +
                        "                       </table>\n" +
                        "                     </td></tr>\n" +
                        "                   </table>\n" +
                        "                 </td></tr>\n" +
                        "                 <tr><td align=\"left\" style=\"Margin:0;padding-top:15px;padding-right:20px;padding-left:20px;padding-bottom:10px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"none\" style=\"border-collapse:collapse\">\n" +
                        "                     <tr><td align=\"left\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                       <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"border-collapse:collapse\">\n" +
                        "                         <tr><td align=\"center\" style=\"padding:0;Margin:0;padding-bottom:10px;padding-top:10px;font-size:12px;color:#333333;line-height:18px\">\n" +
                        "                           <p>Si tiene dudas o consultas, enviar un correo a <a target=\"_blank\" href=\"mailto:contacto@aecode.ai\" style=\"text-decoration:underline;color:#5C68E2;font-size:12px\">contacto@aecode.ai</a></p>\n" +
                        "                         </td></tr>\n" +
                        "                       </table>\n" +
                        "                     </td></tr>\n" +
                        "                   </table>\n" +
                        "                 </td></tr>\n" +
                        "               </table>\n" +
                        "             </td></tr>\n" +
                        "           </table>\n" +
                        "         </td></tr>\n" +
                        "       </table>\n" +
                        "      </td>\n" +
                        "     </tr>\n" +
                        "   </table>\n" +
                        "  </div>\n" +
                        " </body>\n" +
                        "</html>";

        return userHtml;
    }

    /** Texto plano para empresa. */
    public static String renderCompanyPlain(UserProfile user,
                                            String eventTitle,
                                            String planTitle,
                                            int quantity,
                                            String currency,
                                            BigDecimal unitPriceShown,
                                            BigDecimal subtotal,
                                            BigDecimal discount,
                                            BigDecimal commission,
                                            BigDecimal total,
                                            OffsetDateTime purchasedAt,
                                            String purchaseNumber,
                                            PaymentReceipt.PaymentMethod method) {

        String methodText = methodLabel(method);
        return String.format(Locale.US,
                "El usuario %s (%s) ha comprado entradas para el evento:%n" +
                        "- %s — %s x %d (unit: %s %s)%n%n" +
                        "Subtotal: %s%nDescuentos: %s%nComisión: %s%nTotal pagado: %s%n" +
                        "Fecha: %s%nNº Compra: #%s%nMétodo: %s",
                (user.getFullname() != null && !user.getFullname().isBlank()) ? user.getFullname() : user.getEmail(),
                user.getEmail(),
                eventTitle, planTitle, quantity,
                unitPriceShown == null ? "0.00" : String.format(Locale.US,"%.2f", unitPriceShown.doubleValue()),
                currency,
                fmt(subtotal, currency),
                fmt(discount, currency),
                fmt(commission, currency),
                fmt(total, currency),
                purchasedAt == null ? "" : purchasedAt.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                purchaseNumber == null ? "" : purchaseNumber,
                methodText
        );
    }
}
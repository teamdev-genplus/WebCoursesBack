package com.aecode.webcoursesback.servicesimplement.Izipay;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.services.EmailSenderService;
import com.aecode.webcoursesback.services.Izipay.EmailReceiptService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmailReceiptServiceImpl implements EmailReceiptService {
    private final EmailSenderService emailSenderService;

    private static double nvl(Double d, double def) { return d == null ? def : d; }
    private static String safe(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;")
                .replace(">","&gt;").replace("\"","&quot;");
    }
    private static String nullSafe(String primary, String fallback) {
        return (primary == null || primary.isBlank()) ? fallback : primary;
    }
    private static String fmt(double amount, String currency) {
        // 105.18 PEN  /  105.18 USD
        return String.format(Locale.US, "%.2f %s", amount, currency);
    }

    @Override
    public void sendIzipayReceipt(UserProfile user,
                                  List<Module> modules,
                                  String purchaseNumber,
                                  OffsetDateTime purchasedAt,
                                  String currency,
                                  double amountPaid) {

        if (purchasedAt == null) purchasedAt = OffsetDateTime.now();
        if (currency == null || currency.isBlank()) currency = "PEN";

        // ===== 1) Calcular importes (comisión = 0; desc = subtotal - pagado si aplica)
        double subtotal = 0.0;
        StringBuilder cursosHtmlBuilder = new StringBuilder();
        StringBuilder companyCoursesBuilder = new StringBuilder();

        for (Module m : modules) {
            String itemTitle = (m.getCourse() != null && m.getCourse().getTitle() != null)
                    ? m.getCourse().getTitle() + " — " + safe(m.getProgramTitle())
                    : safe(m.getProgramTitle());

            // Tomamos el precio "regular" como referencia para subtotal (ajusta si usas otro campo)
            double price = nvl(m.getPriceRegular(), 0.0);
            subtotal += price;

            // fila para HTML (usuario)
            cursosHtmlBuilder.append(
                    "<tr>" +
                            "<td align=\"center\" style=\"padding:5px 40px; font-family:verdana, geneva, sans-serif; font-size:14px; color:#333333;\">" +
                            "<strong>" + itemTitle + "</strong></td>" +
                            "<td align=\"center\" style=\"padding:5px 40px; font-family:verdana, geneva, sans-serif; font-size:14px; color:#333333; width:85px;\">" +
                            fmt(price, currency) + "</td>" +
                            "</tr>"
            );

            // texto para email empresa
            companyCoursesBuilder.append(String.format(Locale.US, "- %s (%s)%n", itemTitle, fmt(price, currency)));
        }

        // comisión Izipay (para el email) = 0
        double comision = 0.0;

        // descuento: si el total pagado es menor al subtotal calculado => asumimos descuento aplicado
        double descuentoTotal = subtotal - amountPaid;
        if (descuentoTotal < 0) descuentoTotal = 0.0;

        double total = amountPaid;

        // ===== 2) Fechas para el email
        // número de compra: usa el orderId de Izipay que enviamos desde PaymentEntitlementService
        String numeroCompra = (purchaseNumber == null || purchaseNumber.isBlank())
                ? DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(purchasedAt)
                : purchaseNumber;

        java.util.Locale locale = new java.util.Locale("es", "ES");
        String fechaCompra = purchasedAt.toLocalDate().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", locale));

        // ===== 3) HTML (idéntico al de PayPal; sólo cambia "Método de Pago" y los importes formateados)
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
                        "   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" class=\"es-wrapper\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;background-color:#FAFAFA\">\n" +
                        "     <tr>\n" +
                        "      <td valign=\"top\" style=\"padding:0;Margin:0\">\n" +
                        "       <table cellpadding=\"0\" cellspacing=\"0\" align=\"center\" class=\"es-header\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:100%;table-layout:fixed !important;background-color:transparent;background-repeat:repeat;background-position:center top\">\n" +
                        "         <tr>\n" +
                        "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                        "           <table bgcolor=\"#ffffff\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" class=\"es-header-body\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\">\n" +
                        "             <tr>\n" +
                        "              <td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
                        "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                        "                 <tr>\n" +
                        "                  <td valign=\"top\" align=\"center\" class=\"es-m-p0r\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\">" +
                        "                       <img src=\"https://euqtuhd.stripocdn.email/content/guids/CABINET_d1422edc264bd643c8af51440e8995acef2448ffb48805c1983bece0ea0a568e/images/channels4_banner.jpg\" alt=\"Logo\" width=\"560\" title=\"Logo\" class=\"adapt-img\" style=\"display:block;font-size:12px;border:0;outline:none;text-decoration:none;border-radius:0\">" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                   </table></td>\n" +
                        "                 </tr>\n" +
                        "               </table></td>\n" +
                        "             </tr>\n" +
                        "           </table></td>\n" +
                        "         </tr>\n" +
                        "       </table>\n" +
                        "       <table cellpadding=\"0\" cellspacing=\"0\" align=\"center\" class=\"es-content\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:100%;table-layout:fixed !important\">\n" +
                        "         <tr>\n" +
                        "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                        "           <table bgcolor=\"#ffffff\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" class=\"es-content-body\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px\">\n" +
                        "             <tr>\n" +
                        "              <td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px;padding-right:20px;padding-left:20px\">\n" +
                        "               <table cellpadding=\"0\" cellspacing=\"0\" align=\"left\" class=\"es-left\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\">\n" +
                        "                 <tr>\n" +
                        "                  <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" class=\"es-text-4557\" style=\"padding:0;Margin:0;padding-bottom:10px\"><p class=\"es-m-txt-c es-text-mobile-size-36\" style=\"Margin:0;mso-line-height-rule:exactly;font-family:verdana, geneva, sans-serif;line-height:36px;letter-spacing:0;color:#333333;font-size:36px\"><strong>Confirmación de Compra</strong></p></td>\n" +
                        "                     </tr>\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;letter-spacing:0;color:#333333;font-size:14px\">¡Felicidades! Tu compra se procesó correctamente</p></td>\n" +
                        "                     </tr>\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" style=\"padding:10px 0 10px 0; font-family:verdana, geneva, sans-serif; font-size:20px; color:#5C68E2; font-weight:bold;\">\n" +
                        "                        Nro. Compra <a target=\"_blank\" style=\"text-decoration:underline;color:#5C68E2;\" href=\"#\">#" + safe(numeroCompra) + "</a>\n" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" style=\"padding:0;Margin:0; font-family:verdana, geneva, sans-serif; font-size:14px; color:#333333;\">" +
                        "                        " + safe(fechaCompra) + "\n" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" style=\"padding:10px 0 15px 0; font-family:arial, helvetica, sans-serif; font-size:14px; color:#333333;\">\n" +
                        "                        Gracias por tu compra en <strong>AECODE Training.</strong>\n" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                        "                        <span style=\"border-style:solid;border-color:#2CB543;background:#1f1748;border-width:0px;display:inline-block;border-radius:5px;width:auto\">\n" +
                        "                          <a href=\"https://aecode.ai/training\" target=\"_blank\" style=\"text-decoration:none;color:#FFFFFF;font-size:20px;padding:10px 30px;display:inline-block;background:#1f1748;border-radius:5px;font-family:arial, helvetica, sans-serif;font-weight:bold;line-height:24px;text-align:center;letter-spacing:0;\">Visualiza tu compra aquí.</a>\n" +
                        "                        </span>\n" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                   </table></td>\n" +
                        "                 </tr>\n" +
                        "               </table></td>\n" +
                        "             </tr>\n" +
                        "             <tr>\n" +
                        "              <td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
                        "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"none\" style=\"border-collapse:collapse;border-spacing:0px\">\n" +
                        "                 <tr>\n" +
                        "                  <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"border-collapse:collapse;border-spacing:0px\">\n" +
                        "                     <tbody>" +
                        cursosHtmlBuilder +
                        "                     </tbody>\n" +
                        "                   </table>\n" +
                        "                  </td>\n" +
                        "                 </tr>\n" +
                        "                 <tr>\n" +
                        "                  <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-collapse:collapse;border-spacing:0px;border-top:2px solid #efefef;border-bottom:2px solid #efefef\" role=\"presentation\">\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"right\" style=\"padding:0;Margin:0;padding-top:10px;padding-bottom:20px\">\n" +
                        "                       <p style=\"Margin:0;font-family:verdana, geneva, sans-serif;line-height:21px;letter-spacing:0;color:#333333;font-size:14px\">" +
                        "Subtotal: <strong>" + fmt(subtotal, currency) + "</strong><br>" +
                        "Descuentos: <strong>" + fmt(descuentoTotal, currency) + "</strong><br>" +
                        "Comisión: <strong>" + fmt(0.0, currency) + "</strong><br>" +
                        "Total: <strong>" + fmt(total, currency) + "</strong>" +
                        "</p>\n" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                   </table>\n" +
                        "                  </td>\n" +
                        "                 </tr>\n" +
                        "                 <tr>\n" +
                        "                  <td align=\"left\" style=\"Margin:0;padding-right:20px;padding-left:20px;padding-bottom:10px;padding-top:20px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" align=\"left\" class=\"es-left\" role=\"none\" style=\"float:left\">\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                       <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"border-collapse:collapse;border-spacing:0px\">\n" +
                        "                         <tr>\n" +
                        "                          <td align=\"left\" style=\"padding:0;Margin:0;font-family:verdana, geneva, sans-serif;font-size:12px;color:#333333;line-height:18px;letter-spacing:0\">\n" +
                        "                           <p>Cliente: <strong>" + safe(user.getEmail()) + "</strong></p>\n" +
                        "                           <p>Número de Compra: <strong>#" + safe(numeroCompra) + "</strong></p>\n" +
                        "                           <p>Fecha: <strong>" + safe(fechaCompra) + "</strong></p>\n" +
                        "                           <p>Método de Pago: <strong>Izipay</strong></p>\n" +
                        "                           <p>Moneda: <strong>" + safe(currency) + "</strong></p>\n" +
                        "                          </td>\n" +
                        "                         </tr>\n" +
                        "                       </table>\n" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                   </table>\n" +
                        "                  </td>\n" +
                        "                 </tr>\n" +
                        "                 <tr>\n" +
                        "                  <td align=\"left\" style=\"Margin:0;padding-top:15px;padding-right:20px;padding-left:20px;padding-bottom:10px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"none\" style=\"border-collapse:collapse;border-spacing:0px\">\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"left\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                       <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"border-collapse:collapse;border-spacing:0px\">\n" +
                        "                         <tr>\n" +
                        "                          <td align=\"center\" style=\"padding:0;Margin:0;padding-bottom:10px;padding-top:10px;font-family:verdana, geneva, sans-serif;font-size:12px;color:#333333;line-height:18px;letter-spacing:0\">\n" +
                        "                           <p>Si tiene dudas o consultas, enviar un correo a <a target=\"_blank\" href=\"mailto:contacto@aecode.ai\" style=\"text-decoration:underline;color:#5C68E2;font-size:12px;font-family:verdana, geneva, sans-serif\">contacto@aecode.ai</a></p>\n" +
                        "                          </td>\n" +
                        "                         </tr>\n" +
                        "                       </table>\n" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                   </table>\n" +
                        "                  </td>\n" +
                        "                 </tr>\n" +
                        "               </table>\n" +
                        "              </td>\n" +
                        "             </tr>\n" +
                        "           </table>\n" +
                        "          </td>\n" +
                        "         </tr>\n" +
                        "       </table>\n" +
                        "      </td>\n" +
                        "     </tr>\n" +
                        "   </table>\n" +
                        "  </div>\n" +
                        " </body>\n" +
                        "</html>";

        // ===== 4) Envíos
        try {
            // Usuario
            emailSenderService.sendHtmlEmail(user.getEmail(), "Confirmación de compra", userHtml);

            // Empresa (texto plano)
            String companyBody = String.format(
                    Locale.US,
                    "El usuario %s (%s) ha comprado los siguientes módulos:%n%s%n" +
                            "Subtotal: %s%nDescuentos: %s%nComisión: %s%nTotal pagado: %s%nFecha: %s",
                    nullSafe(user.getFullname(), user.getEmail()),
                    user.getEmail(),
                    companyCoursesBuilder.toString(),
                    fmt(subtotal, currency),
                    fmt(descuentoTotal, currency),
                    fmt(0.0, currency),
                    fmt(total, currency),
                    purchasedAt.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );
            emailSenderService.sendEmail("contacto@aecode.ai", "Nueva compra de módulos (Izipay)", companyBody);

        } catch (MessagingException e) {
            // No bloqueamos el flujo si falla el envío
            System.err.println("Fallo enviando email de Izipay: " + e.getMessage());
        }
    }

    @Override
    public void sendIzipayEventReceipt(UserProfile user,
                                       String eventTitle,
                                       String planTitle,
                                       int quantity,
                                       String currency,
                                       double unitPriceShown,
                                       double subtotal,
                                       double discountTotal,
                                       double total,
                                       String purchaseNumber,
                                       OffsetDateTime purchasedAt) {
        if (purchasedAt == null) purchasedAt = OffsetDateTime.now();
        if (currency == null || currency.isBlank()) currency = "PEN";
        if (eventTitle == null || eventTitle.isBlank()) eventTitle = "Evento";
        if (planTitle == null || planTitle.isBlank()) planTitle = "Plan";

        String numeroCompra = (purchaseNumber == null || purchaseNumber.isBlank())
                ? DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(purchasedAt)
                : purchaseNumber;

        java.util.Locale locale = new java.util.Locale("es", "ES");
        String fechaCompra = purchasedAt.toLocalDate().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", locale));

        // === 1) Fila única: "Evento — Plan x Cantidad" + precio unitario mostrado
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

        // === 2) HTML base (MISMO diseño que módulos) — se elimina el botón
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
                        "   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" class=\"es-wrapper\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;background-color:#FAFAFA\">\n" +
                        "     <tr>\n" +
                        "      <td valign=\"top\" style=\"padding:0;Margin:0\">\n" +
                        "       <table cellpadding=\"0\" cellspacing=\"0\" align=\"center\" class=\"es-header\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:100%;table-layout:fixed !important;background-color:transparent;background-repeat:repeat;background-position:center top\">\n" +
                        "         <tr>\n" +
                        "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                        "           <table bgcolor=\"#ffffff\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" class=\"es-header-body\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\">\n" +
                        "             <tr>\n" +
                        "              <td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
                        "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                        "                 <tr>\n" +
                        "                  <td valign=\"top\" align=\"center\" class=\"es-m-p0r\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\">" +
                        "                       <img src=\"https://euqtuhd.stripocdn.email/content/guids/CABINET_d1422edc264bd643c8af51440e8995acef2448ffb48805c1983bece0ea0a568e/images/channels4_banner.jpg\" alt=\"Logo\" width=\"560\" title=\"Logo\" class=\"adapt-img\" style=\"display:block;font-size:12px;border:0;outline:none;text-decoration:none;border-radius:0\">" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                   </table></td>\n" +
                        "                 </tr>\n" +
                        "               </table></td>\n" +
                        "             </tr>\n" +
                        "           </table></td>\n" +
                        "         </tr>\n" +
                        "       </table>\n" +
                        "       <table cellpadding=\"0\" cellspacing=\"0\" align=\"center\" class=\"es-content\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:100%;table-layout:fixed !important\">\n" +
                        "         <tr>\n" +
                        "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                        "           <table bgcolor=\"#ffffff\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" class=\"es-content-body\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px\">\n" +
                        "             <tr>\n" +
                        "              <td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px;padding-right:20px;padding-left:20px\">\n" +
                        "               <table cellpadding=\"0\" cellspacing=\"0\" align=\"left\" class=\"es-left\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\">\n" +
                        "                 <tr>\n" +
                        "                  <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" class=\"es-text-4557\" style=\"padding:0;Margin:0;padding-bottom:10px\"><p class=\"es-m-txt-c es-text-mobile-size-36\" style=\"Margin:0;mso-line-height-rule:exactly;font-family:verdana, geneva, sans-serif;line-height:36px;letter-spacing:0;color:#333333;font-size:36px\"><strong>Confirmación de Compra</strong></p></td>\n" +
                        "                     </tr>\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;letter-spacing:0;color:#333333;font-size:14px\">¡Felicidades! Tu compra se procesó correctamente</p></td>\n" +
                        "                     </tr>\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" style=\"padding:10px 0 10px 0; font-family:verdana, geneva, sans-serif; font-size:20px; color:#5C68E2; font-weight:bold;\">\n" +
                        "                        Nro. Compra <a target=\"_blank\" style=\"text-decoration:underline;color:#5C68E2;\" href=\"#\">#" + safe(numeroCompra) + "</a>\n" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" style=\"padding:0;Margin:0; font-family:verdana, geneva, sans-serif; font-size:14px; color:#333333;\">" +
                        "                        " + safe(fechaCompra) + "\n" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" style=\"padding:10px 0 15px 0; font-family:arial, helvetica, sans-serif; font-size:14px; color:#333333;\">\n" +
                        "                        Gracias por tu compra en <strong>AECODE Training.</strong>\n" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                   </table></td>\n" +
                        "                 </tr>\n" +
                        "               </table></td>\n" +
                        "             </tr>\n" +
                        "             <tr>\n" +
                        "              <td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
                        "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"none\" style=\"border-collapse:collapse;border-spacing:0px\">\n" +
                        "                 <tr>\n" +
                        "                  <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"border-collapse:collapse;border-spacing:0px\">\n" +
                        "                     <tbody>" +
                        eventoHtmlBuilder +
                        "                     </tbody>\n" +
                        "                   </table>\n" +
                        "                  </td>\n" +
                        "                 </tr>\n" +
                        "                 <tr>\n" +
                        "                  <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-collapse:collapse;border-spacing:0px;border-top:2px solid #efefef;border-bottom:2px solid #efefef\" role=\"presentation\">\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"right\" style=\"padding:0;Margin:0;padding-top:10px;padding-bottom:20px\">\n" +
                        "                       <p style=\"Margin:0;font-family:verdana, geneva, sans-serif;line-height:21px;letter-spacing:0;color:#333333;font-size:14px\">" +
                        "Subtotal: <strong>" + fmt(subtotal, currency) + "</strong><br>" +
                        "Descuentos: <strong>" + fmt(discountTotal, currency) + "</strong><br>" +
                        "Comisión: <strong>" + fmt(0.0, currency) + "</strong><br>" +   // se mantiene 0 como en el diseño
                        "Total: <strong>" + fmt(total, currency) + "</strong>" +
                        "</p>\n" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                   </table>\n" +
                        "                  </td>\n" +
                        "                 </tr>\n" +
                        "                 <tr>\n" +
                        "                  <td align=\"left\" style=\"Margin:0;padding-right:20px;padding-left:20px;padding-bottom:10px;padding-top:20px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" align=\"left\" class=\"es-left\" role=\"none\" style=\"float:left\">\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"center\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                       <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"border-collapse:collapse;border-spacing:0px\">\n" +
                        "                         <tr>\n" +
                        "                          <td align=\"left\" style=\"padding:0;Margin:0;font-family:verdana, geneva, sans-serif;font-size:12px;color:#333333;line-height:18px;letter-spacing:0\">\n" +
                        "                           <p>Cliente: <strong>" + safe(user.getEmail()) + "</strong></p>\n" +
                        "                           <p>Número de Compra: <strong>#" + safe(numeroCompra) + "</strong></p>\n" +
                        "                           <p>Fecha: <strong>" + safe(fechaCompra) + "</strong></p>\n" +
                        "                           <p>Método de Pago: <strong>Izipay</strong></p>\n" +
                        "                           <p>Moneda: <strong>" + safe(currency) + "</strong></p>\n" +
                        "                          </td>\n" +
                        "                         </tr>\n" +
                        "                       </table>\n" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                   </table>\n" +
                        "                  </td>\n" +
                        "                 </tr>\n" +
                        "                 <tr>\n" +
                        "                  <td align=\"left\" style=\"Margin:0;padding-top:15px;padding-right:20px;padding-left:20px;padding-bottom:10px\">\n" +
                        "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"none\" style=\"border-collapse:collapse;border-spacing:0px\">\n" +
                        "                     <tr>\n" +
                        "                      <td align=\"left\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                       <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"border-collapse:collapse;border-spacing:0px\">\n" +
                        "                         <tr>\n" +
                        "                          <td align=\"center\" style=\"padding:0;Margin:0;padding-bottom:10px;padding-top:10px;font-family:verdana, geneva, sans-serif;font-size:12px;color:#333333;line-height:18px;letter-spacing:0\">\n" +
                        "                           <p>Si tiene dudas o consultas, enviar un correo a <a target=\"_blank\" href=\"mailto:contacto@aecode.ai\" style=\"text-decoration:underline;color:#5C68E2;font-size:12px;font-family:verdana, geneva, sans-serif\">contacto@aecode.ai</a></p>\n" +
                        "                          </td>\n" +
                        "                         </tr>\n" +
                        "                       </table>\n" +
                        "                      </td>\n" +
                        "                     </tr>\n" +
                        "                   </table>\n" +
                        "                  </td>\n" +
                        "                 </tr>\n" +
                        "               </table>\n" +
                        "              </td>\n" +
                        "             </tr>\n" +
                        "           </table>\n" +
                        "          </td>\n" +
                        "         </tr>\n" +
                        "       </table>\n" +
                        "      </td>\n" +
                        "     </tr>\n" +
                        "   </table>\n" +
                        "  </div>\n" +
                        " </body>\n" +
                        "</html>";

        // === 3) Envío
        try {
            emailSenderService.sendHtmlEmail(user.getEmail(), "Confirmación de compra", userHtml);

            // Empresa (texto plano, adaptado a EVENT)
            String companyBody = String.format(
                    Locale.US,
                    "El usuario %s (%s) ha comprado entradas para el evento:%n- %s — %s x %d (unit: %s)%n%n" +
                            "Subtotal: %s%nDescuentos: %s%nComisión: %s%nTotal pagado: %s%nFecha: %s%nNº Compra: #%s",
                    nullSafe(user.getFullname(), user.getEmail()),
                    user.getEmail(),
                    eventTitle, planTitle, quantity, fmt(unitPriceShown, currency),
                    fmt(subtotal, currency),
                    fmt(discountTotal, currency),
                    fmt(0.0, currency),
                    fmt(total, currency),
                    purchasedAt.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                    numeroCompra
            );
            emailSenderService.sendEmail("contacto@aecode.ai", "Nueva compra de evento (Izipay)", companyBody);

        } catch (MessagingException e) {
            System.err.println("Fallo enviando email de Izipay EVENT: " + e.getMessage());
        }
    }


}

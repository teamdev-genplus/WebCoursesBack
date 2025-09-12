package com.aecode.webcoursesback.entities.Paid;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.Paid.PaymentReceipt;
import com.aecode.webcoursesback.entities.UserProfile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
public class EmailReceiptRenderer {

    private static String escape(String s) {
        return s == null ? "" : s.replace("&","&amp;")
                .replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }

    private static BigDecimal unitPrice(Module m) {
        boolean onSale = Boolean.TRUE.equals(m.getIsOnSale());
        Double prompt = m.getPromptPaymentPrice();
        Double regular = m.getPriceRegular();
        double chosen = onSale && prompt != null && prompt > 0 ? prompt
                : regular != null ? regular : 0.0;
        return BigDecimal.valueOf(chosen).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal subtotalFromModules(List<Module> modules) {
        return modules.stream().map(EmailReceiptRenderer::unitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    private static String currencySuffix(PaymentReceipt.CurrencyCode code) {
        return switch (code) { case USD -> "$"; case PEN -> "S/"; default -> code.name(); };
    }

    // ===== nuevos helpers de totales (para consistencia con el service) =====
    private static BigDecimal effectiveSubtotal(List<Module> modules, PaymentReceipt r) {
        return Optional.ofNullable(r.getSubtotal()).orElseGet(() -> subtotalFromModules(modules))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal effectiveCommission(PaymentReceipt r) {
        return Optional.ofNullable(r.getCommission()).orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal effectiveDiscount(List<Module> modules, PaymentReceipt r) {
        if (r.getDiscount() != null) return r.getDiscount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = effectiveSubtotal(modules, r);
        BigDecimal commission = effectiveCommission(r);
        BigDecimal total = Optional.ofNullable(r.getTotal()).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        BigDecimal d = subtotal.add(commission).subtract(total);
        if (d.compareTo(BigDecimal.ZERO) < 0) d = BigDecimal.ZERO;
        return d.setScale(2, RoundingMode.HALF_UP);
    }

    public static String renderLegacyExact(UserProfile user, List<Module> modules, PaymentReceipt r) {
        // ======= Valores a inyectar
        String numeroCompra = escape(r.getPurchaseNumber());

        String fechaCompra = (r.getPurchaseDateLabel()!=null && !r.getPurchaseDateLabel().isBlank())
                ? escape(r.getPurchaseDateLabel())
                : DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", new Locale("es","ES"))
                .format(r.getPurchaseAt().toLocalDate());

        String metodoPago = switch (r.getMethod()) {
            case PAYPAL -> "PayPal"; case YAPE -> "Yape"; case PLIN -> "Plin";
            default -> r.getMethod().name();
        };
        String moneda = r.getCurrency().name();
        String curSuf = currencySuffix(r.getCurrency());

        // Totales efectivos (respetando la misma regla que en el service)
        BigDecimal subtotal  = effectiveSubtotal(modules, r);
        BigDecimal comision  = effectiveCommission(r);
        BigDecimal descuento = effectiveDiscount(modules, r);
        BigDecimal total     = Optional.ofNullable(r.getTotal()).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        // ======= Filas de módulos
        StringBuilder cursosHtmlBuilder = new StringBuilder();
        for (Module m : modules) {
            String itemTitle = (m.getCourse()!=null && m.getCourse().getTitle()!=null)
                    ? escape(m.getCourse().getTitle()) + " — " + escape(m.getProgramTitle())
                    : escape(m.getProgramTitle());
            String priceStr = String.format(Locale.US, "%.2f %s", unitPrice(m), curSuf);

            cursosHtmlBuilder.append(
                    "<tr>" +
                            "<td align=\"center\" style=\"padding:5px 40px; font-family:verdana, geneva, sans-serif; font-size:14px; color:#333333;\">" +
                            "<strong>" + itemTitle + "</strong></td>" +
                            "<td align=\"center\" style=\"padding:5px 40px; font-family:verdana, geneva, sans-serif; font-size:14px; color:#333333; width:85px;\">" +
                            priceStr + "</td>" +
                            "</tr>"
            );
        }

        // ======= Totales con mismo formato del HTML original
        String subtotalStr  = String.format(Locale.US, "%.2f %s", subtotal,  curSuf);
        String descuentoStr = String.format(Locale.US, "%.2f %s", descuento, curSuf);
        String comisionStr  = String.format(Locale.US, "%.2f %s", comision,  curSuf);
        String totalStr     = String.format(Locale.US, "%.2f %s", total,     curSuf);

        // ======= HTML EXACTO (copiado de tu endpoint original)
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html dir=\"ltr\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" lang=\"es\">\n" +
                " <head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">\n" +
                "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "  <meta content=\"telephone=no\" name=\"format-detection\">\n" +
                "  <title>¡Confirmación de Compra! AECODE Training 📚🎉</title>\n" +
                "  <!--[if (mso 16)]>\n" +
                "    <style type=\"text/css\">\n" +
                "    a {text-decoration: none;}\n" +
                "    </style>\n" +
                "  <![endif]-->\n" +
                "  <!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]-->\n" +
                "  <!--[if gte mso 9]>\n" +
                "  <noscript>\n" +
                "         <xml>\n" +
                "           <o:OfficeDocumentSettings>\n" +
                "           <o:AllowPNG></o:AllowPNG>\n" +
                "           <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
                "           </o:OfficeDocumentSettings>\n" +
                "         </xml>\n" +
                "      </noscript>\n" +
                "  <![endif]-->\n" +
                "  <!--[if mso]><xml>\n" +
                "    <w:WordDocument xmlns:w=\"urn:schemas-microsoft-com:office:word\">\n" +
                "      <w:DontUseAdvancedTypographyReadingMail/>\n" +
                "    </w:WordDocument>\n" +
                "    </xml><![endif]-->\n" +
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
                "                        Nro. Compra <a target=\"_blank\" style=\"text-decoration:underline;color:#5C68E2;\" href=\"#\">#" + numeroCompra + "</a>\n" +
                "                      </td>\n" +
                "                     </tr>\n" +
                "                     <tr>\n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0; font-family:verdana, geneva, sans-serif; font-size:14px; color:#333333;\">" +
                "                        " + fechaCompra + "\n" +
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
                "Subtotal: <strong>" + subtotalStr + "</strong><br>" +
                "Descuentos: <strong>" + descuentoStr + "</strong><br>" +
                "Comisión: <strong>" + comisionStr + "</strong><br>" +
                "Total: <strong>" + totalStr + "</strong>" +
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
                "                           <p>Cliente: <strong>" + escape(user.getEmail()) + "</strong></p>\n" +
                "                           <p>Número de Compra: <strong>#"+ numeroCompra +"</strong></p>\n" +
                "                           <p>Fecha: <strong>" + fechaCompra + "</strong></p>\n" +
                "                           <p>Método de Pago: <strong>" + escape(metodoPago) + "</strong></p>\n" +
                "                           <p>Moneda: <strong>" + escape(moneda) + "</strong></p>\n" +
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
    }

    public static String renderCompanyPlain(UserProfile user, List<Module> modules, PaymentReceipt r) {
        String suffix = currencySuffix(r.getCurrency());

        String modulesText = modules.stream()
                .map(m -> {
                    String title = (m.getCourse()!=null ? m.getCourse().getTitle()+" — " : "") +
                            Optional.ofNullable(m.getProgramTitle()).orElse("Módulo");
                    BigDecimal price = unitPrice(m);
                    return "- " + title + " (" + price + " " + suffix + ")";
                })
                .collect(Collectors.joining("\n"));

        BigDecimal subtotal   = effectiveSubtotal(modules, r);
        BigDecimal commission = effectiveCommission(r);
        BigDecimal discount   = effectiveDiscount(modules, r);
        BigDecimal total      = Optional.ofNullable(r.getTotal()).orElse(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

        return """
Nueva compra (front-asserted)
Usuario: %s <%s>
Compra #: %s
Fecha: %s
Método: %s
Moneda: %s

Módulos:
%s

Subtotal: %s %s
Descuento: %s %s
Comisión: %s %s
Total: %s %s
""".formatted(
                Optional.ofNullable(user.getFullname()).orElse(user.getEmail()),
                user.getEmail(),
                r.getPurchaseNumber(),
                Optional.ofNullable(r.getPurchaseDateLabel()).orElse(
                        Optional.ofNullable(r.getPurchaseAt())
                                .map(dt -> dt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                                .orElse("")),
                r.getMethod().name(),
                r.getCurrency().name(),
                modulesText,
                subtotal,  suffix,
                discount,  suffix,
                commission,suffix,
                total,     suffix
        );
    }
}
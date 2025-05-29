package com.aecode.webcoursesback.controllers;
import com.aecode.webcoursesback.dtos.UserSecCourseDTO;
import com.aecode.webcoursesback.dtos.UserSecCoursePurchaseDTO;
import com.aecode.webcoursesback.entities.SecondaryCourses;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.entities.UserSecCourseAccess;
import com.aecode.webcoursesback.services.EmailSenderService;
import com.aecode.webcoursesback.services.ISecondCourseService;
import com.aecode.webcoursesback.services.IUserProfileService;
import com.aecode.webcoursesback.services.IUserSecCourseService;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/userseccourse")
public class UserSecCourseController {


    @Autowired
    IUserSecCourseService uscS;
    @Autowired
    private IUserProfileService pS;
    @Autowired
    private ISecondCourseService scS;

    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping("/purchase")
    public ResponseEntity<String> purchaseCourses(@RequestBody UserSecCoursePurchaseDTO dto) throws MessagingException {
        UserProfile user = pS.listId(dto.getUserId());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
        }

        double subtotal = 0.0;
        double descuentoTotal = 0.0;
        double comision = 0.0;
        double total = 0.0;

        StringBuilder cursosHtmlBuilder = new StringBuilder();
        StringBuilder companyCoursesBuilder = new StringBuilder();

        for (Long courseId : dto.getSeccourseIds()) {
            SecondaryCourses curso = scS.listId(courseId);
            if (curso == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Curso con id " + courseId + " no encontrado");
            }

            boolean exists = uscS.existsByUserProfileUserIdAndSeccourseSeccourseId(user.getUserId(), courseId);
            if (!exists) {
                UserSecCourseAccess access = new UserSecCourseAccess();
                access.setUserProfile(user);
                access.setSeccourse(curso);
                access.setCompleted(false);
                uscS.insert(access);
            }

            // Obtener precio y descuento del curso
            double price = curso.getPriceRegular();
            if(curso.getIsOnSale()==true){
                double discount = price - curso.getPromptPaymentPrice();
                descuentoTotal += discount;
            }

            subtotal += price;

            // Construir filas para email usuario
            cursosHtmlBuilder.append(
                    "<tr>" +
                            "<td align=\"center\" style=\"padding:5px 40px; font-family:verdana, geneva, sans-serif; font-size:14px; color:#333333;\">" +
                            "<strong>" + curso.getTitle() + "</strong></td>" +
                            "<td align=\"center\" style=\"padding:5px 40px; font-family:verdana, geneva, sans-serif; font-size:14px; color:#333333; width:85px;\">" +
                            String.format("%.2f $", price) + "</td>" +
                            "</tr>"
            );

            // Construir texto para email empresa
            companyCoursesBuilder.append(String.format("- %s (%.2f $)\n", curso.getTitle(), price));
        }


        comision = (((subtotal-descuentoTotal) +0.3)*100)/(100-5.4);
        comision = Math.round(comision * 100.0) / 100.0;
        comision =comision-(subtotal-descuentoTotal);
        total = subtotal - descuentoTotal + comision;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String numeroCompra = now.format(formatter);

        Locale locale = new Locale("es", "ES");
        String fechaCompra = LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", locale));

        // HTML completo para email usuario con diseño completo y dinámico
        String userHtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
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
                "  <style type=\"text/css\">\n" +
                "    /* Aquí puedes poner tu CSS embebido si quieres */\n" +
                "  </style>\n" +
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
                "                      <td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><img src=\"https://euqtuhd.stripocdn.email/content/guids/CABINET_d1422edc264bd643c8af51440e8995acef2448ffb48805c1983bece0ea0a568e/images/channels4_banner.jpg\" alt=\"Logo\" width=\"560\" title=\"Logo\" class=\"adapt-img\" style=\"display:block;font-size:12px;border:0;outline:none;text-decoration:none;border-radius:0\"></td>\n" +
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
                "                      <td align=\"center\" style=\"padding:0;Margin:0; font-family:verdana, geneva, sans-serif; font-size:14px; color:#333333;\">\n" +
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
                cursosHtmlBuilder.toString() +
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
                "Subtotal: <strong>" + String.format("%.2f $", subtotal) + "</strong><br>" +
                "Descuentos: <strong>" + String.format("%.2f $", descuentoTotal) + "</strong><br>" +
                "Comisión: <strong>" + String.format("%.2f $", comision) + "</strong><br>" +
                "Total: <strong>" + String.format("%.2f $", total) + "</strong>" +
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
                "                           <p>Cliente: <strong>" + user.getEmail() + "</strong></p>\n" +
                "                           <p>Número de Compra: <strong>#" + numeroCompra + "</strong></p>\n" +
                "                           <p>Fecha: <strong>" + fechaCompra + "</strong></p>\n" +
                "                           <p>Método de Pago: <strong>PayPal</strong></p>\n" +
                "                           <p>Moneda: <strong>USD</strong></p>\n" +
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

        // Enviar email HTML al usuario
        emailSenderService.sendHtmlEmail(user.getEmail(), "Confirmación de compra", userHtml);

        // email texto para correo de la empresa con lista de cursos y total
        String companyBody = String.format(
                "El usuario %s (%s) ha comprado los siguientes cursos:\n%s\nSubtotal: %.2f $\nDescuentos: %.2f $\nComisión: %.2f $\nTotal pagado: %.2f $\nFecha: %s",
                user.getFullname(),
                user.getEmail(),
                companyCoursesBuilder.toString(),
                subtotal,
                descuentoTotal,
                comision,
                total,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );

        // Enviar email a empresa
        emailSenderService.sendEmail("contacto@aecode.ai", "Nueva compra de cursos", companyBody);

        return ResponseEntity.ok("Compra procesada y emails enviados correctamente");
    }

    @GetMapping
    public List<UserSecCourseDTO> list() {
        return uscS.list().stream().map(x -> {
            ModelMapper m = new ModelMapper();
            return m.map(x, UserSecCourseDTO.class);
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        uscS.delete(id);
    }

    @GetMapping("/{id}")
    public UserSecCourseDTO listId(@PathVariable("id") Integer id) {
        ModelMapper m = new ModelMapper();
        UserSecCourseDTO dto = m.map(uscS.listId(id), UserSecCourseDTO.class);
        return dto;
    }

    @PutMapping
    public void update(@RequestBody UserSecCourseDTO dto) {
        ModelMapper m = new ModelMapper();
        UserSecCourseAccess usca = m.map(dto, UserSecCourseAccess.class);
        // Asegurarse de cargar los objetos UserProfile y SecondaryCourses
        UserProfile user = pS.listId(dto.getUserId());
        SecondaryCourses seccourse = scS.listId(dto.getSeccourseId());

        usca.setUserProfile(user);
        usca.setSeccourse(seccourse);

        uscS.insert(usca);
    }

    // Nuevo endpoint PATCH para marcar completado
    @PatchMapping("/{id}/complete")
    public ResponseEntity<String> markCourseCompleted(@PathVariable("id") int id, @RequestParam boolean completed) {
        try {
            uscS.markCompleted(id, completed);
            return ResponseEntity.ok("Estado de completado actualizado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}

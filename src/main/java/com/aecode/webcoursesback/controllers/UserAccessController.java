package com.aecode.webcoursesback.controllers;

import com.aecode.webcoursesback.dtos.*;
import com.aecode.webcoursesback.dtos.Paid.AccessPurchaseRequestDTO;
import com.aecode.webcoursesback.dtos.Paid.AccessPurchaseResponseDTO;
import com.aecode.webcoursesback.dtos.Profile.ModuleProfileDTO;
import com.aecode.webcoursesback.entities.Module;
import com.aecode.webcoursesback.entities.UserProfile;
import com.aecode.webcoursesback.entities.VClassroom.MarkVideoCompletedRequest;
import com.aecode.webcoursesback.repositories.IModuleRepo;
import com.aecode.webcoursesback.repositories.IUserProfileRepository;
import com.aecode.webcoursesback.services.EmailSenderService;
import com.aecode.webcoursesback.services.IUserAccessService;
import com.aecode.webcoursesback.services.Paid.PurchaseAccessService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
@RestController
@RequestMapping("/user-access")
public class UserAccessController {

    @Autowired
    private IUserAccessService userAccessService;
    @Autowired
    private IUserProfileRepository userProfileRepo;
    @Autowired
    private IModuleRepo moduleRepo;
    @Autowired
    private EmailSenderService emailSenderService;

    // NUEVO: servicio que ya creaste en servicesimplement.Paid.PurchaseAccessServiceImpl
    @Autowired private PurchaseAccessService purchaseAccessService;

    /**
     * NUEVO: Otorga acceso por compra "front-asserted" (PayPal / Yape / Plin),
     * envía email HTML con los datos recibidos y persiste un recibo unificado.
     * Izipay se maneja aparte (IPN/validate).
     *
     * IMPORTANTE: Esto también guarda accesos en usermoduleaccess, porque
     * internamente llama a userAccessService.grantMultipleModuleAccess(...).
     */

    @PostMapping("/modules/access/purchase")
    public ResponseEntity<AccessPurchaseResponseDTO> grantModulesWithFrontAssertedPurchase(
            @Valid @RequestBody AccessPurchaseRequestDTO request
    ) {
        // Si ya usas el filtro de consistencia, aquí no necesitas más validación del clerkId.
        AccessPurchaseResponseDTO res = purchaseAccessService.processFrontAssertedPurchase(request);
        return ResponseEntity.ok(res);
    }


    // ======================
    // ACCESO DEL USUARIO
    // ======================

    /**
     * Obtener cards de los cursos a los que el usuario tiene acceso (completo o parcial).
     */
    @GetMapping("/courses/{clerkId}")
    public ResponseEntity<List<CourseCardProgressDTO>> getUserCourses(@PathVariable String clerkId) {
        return ResponseEntity.ok(userAccessService.getAccessibleCoursesForUser(clerkId));
    }

    /**
     * Obtener el primer módulo disponible de un curso al que el usuario tenga acceso.
     */
    @GetMapping("/courses/id/{courseId}/first-module")
    public ResponseEntity<ModuleProfileDTO> getFirstAccessibleModule(
            @RequestParam String clerkId,
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(userAccessService.getFirstAccessibleModuleForUser(clerkId, courseId));
    }

    @GetMapping("/courses/{urlnamecourse}/first-module")
    public ResponseEntity<ModuleProfileDTO> getFirstAccessibleModuleByurlName(
            @RequestParam String clerkId,
            @PathVariable String urlnamecourse
    ) {
        return ResponseEntity.ok(
                userAccessService.getFirstAccessibleModuleForUserBySlug(clerkId, urlnamecourse)
        );
    }


    /**
     * Obtener información de un módulo si el usuario tiene acceso a él.
     */
    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<?> getModuleIfHasAccess(
            @RequestParam String clerkId,
            @PathVariable Long moduleId
    ) {
        return ResponseEntity.ok(userAccessService.getModuleById(moduleId, clerkId));
    }

    /**
     * Obtener los módulos a los que el usuario tiene acceso.
     */
    @GetMapping("/modules")
    public ResponseEntity<List<UserModuleDTO>> getUserModules(@RequestParam String clerkId) {
        return ResponseEntity.ok(userAccessService.getUserModulesByClerkId(clerkId));
    }

    // ======================
    // ACCESO ADMIN / BACKOFFICE
    // ======================

    /**
     * Obtener todos los cursos con acceso registrado.
     */
    @GetMapping("/admin/courses")
    public ResponseEntity<List<UserCourseDTO>> getAllCoursesAccess() {
        return ResponseEntity.ok(userAccessService.getAllCourses());
    }

    /**
     * Obtener todos los módulos con acceso registrado.
     */
    @GetMapping("/admin/modules")
    public ResponseEntity<List<UserModuleDTO>> getAllModulesAccess() {
        return ResponseEntity.ok(userAccessService.getAllModules());
    }

    // ======================
    // GESTIÓN DE ACCESO (COMPRAS / TRACKING)
    // ======================

    /**
     * Otorgar acceso completo a un curso al usuario (incluye todos los módulos).
     */
    @PostMapping("/courses/access")
    public ResponseEntity<UserCourseDTO> grantCourseAccess(
            @RequestParam String clerkId,
            @RequestParam Long courseId
    ) {
        UserCourseDTO result = userAccessService.grantCourseAccess(clerkId, courseId);
        return ResponseEntity.ok(result);
    }

    /**
     * Otorgar acceso individual a un módulo al usuario.
     */
    @PostMapping("/modules/access")
    public ResponseEntity<UserModuleDTO> grantModuleAccess(
            @RequestParam String clerkId,
            @RequestParam Long moduleId
    ) {
        UserModuleDTO result = userAccessService.grantModuleAccess(clerkId, moduleId);
        return ResponseEntity.ok(result);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //ADAPTAR ESTE ENVIO DE CORREOS DE UN ANTERIOR CONTROLLER Y ENDPOINT A grantMultipleModules
    @PostMapping("/modules/access/multiple")
    public ResponseEntity<List<UserModuleDTO>> grantMultipleModules(
            @RequestParam String clerkId,
            @RequestBody List<Long> moduleIds
    ) {
        if (moduleIds == null || moduleIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        // --- 1) Traer usuario (email / nombre) ---
        UserProfile user = userProfileRepo.findByClerkId(clerkId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con Clerk ID no encontrado: " + clerkId));

        // --- 2) Traer módulos (para precios/títulos) ---
        List<Module> modules = moduleRepo.findAllById(moduleIds);
        if (modules.size() != moduleIds.size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.emptyList()); // alguno no existe
        }

        // --- 3) Calcular importes y construir contenido para emails ---
        double subtotal = 0.0;
        double descuentoTotal = 0.0;
        double comision = 0.0;
        double total = 0.0;

        StringBuilder cursosHtmlBuilder = new StringBuilder();
        StringBuilder companyCoursesBuilder = new StringBuilder();

        for (Module m : modules) {
            // Título mostrado: "Curso - Nombre del Módulo" (ajusta si prefieres solo el módulo)
            String itemTitle = (m.getCourse() != null && m.getCourse().getTitle() != null)
                    ? m.getCourse().getTitle() + " — " + safe(m.getProgramTitle())
                    : safe(m.getProgramTitle());

            // === PRECIOS ===
            // Ajusta estos getters si tus nombres de campo difieren
            double price = nvl(m.getPriceRegular(), 0.0);
            Boolean onSale = m.getIsOnSale() != null ? m.getIsOnSale() : Boolean.FALSE;
            Double prompt = m.getPromptPaymentPrice();

            if (onSale && prompt != null && prompt > 0) {
                double discount = price - prompt;
                if (discount > 0) {
                    descuentoTotal += discount;
                }
            }

            subtotal += price;

            // Fila para HTML usuario
            cursosHtmlBuilder.append(
                    "<tr>" +
                            "<td align=\"center\" style=\"padding:5px 40px; font-family:verdana, geneva, sans-serif; font-size:14px; color:#333333;\">" +
                            "<strong>" + itemTitle + "</strong></td>" +
                            "<td align=\"center\" style=\"padding:5px 40px; font-family:verdana, geneva, sans-serif; font-size:14px; color:#333333; width:85px;\">" +
                            String.format(java.util.Locale.US, "%.2f $", price) + "</td>" +
                            "</tr>"
            );

            // Texto para email empresa
            companyCoursesBuilder.append(String.format(java.util.Locale.US, "- %s (%.2f $)%n", itemTitle, price));
        }

        // Comisión PayPal (5.4% + 0.30) como en tu endpoint anterior
        comision = (((subtotal - descuentoTotal) + 0.30) * 100) / (100 - 5.4);
        comision = Math.round(comision * 100.0) / 100.0;
        comision = comision - (subtotal - descuentoTotal);

        total = subtotal - descuentoTotal + comision;

        // --- 4) Nro/Fecha de compra ---
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String numeroCompra = now.format(formatter);

        java.util.Locale locale = new java.util.Locale("es", "ES");
        String fechaCompra = LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'del' yyyy", locale));

        // --- 5) Ejecutar la lógica de otorgar accesos ---
        List<UserModuleDTO> saved = userAccessService.grantMultipleModuleAccess(clerkId, moduleIds);

        // --- 6) Armar HTML (MISMO DISEÑO) y enviar emails ---
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
                        "Subtotal: <strong>" + String.format(java.util.Locale.US, "%.2f $", subtotal) + "</strong><br>" +
                        "Descuentos: <strong>" + String.format(java.util.Locale.US, "%.2f $", descuentoTotal) + "</strong><br>" +
                        "Comisión: <strong>" + String.format(java.util.Locale.US, "%.2f $", comision) + "</strong><br>" +
                        "Total: <strong>" + String.format(java.util.Locale.US, "%.2f $", total) + "</strong>" +
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

        try {
            // Email HTML al usuario
            emailSenderService.sendHtmlEmail(user.getEmail(), "Confirmación de compra", userHtml);

            // Email texto a la empresa
            String companyBody = String.format(
                    java.util.Locale.US,
                    "El usuario %s (%s) ha comprado los siguientes módulos:%n%s%n" +
                            "Subtotal: %.2f $%nDescuentos: %.2f $%nComisión: %.2f $%nTotal pagado: %.2f $%nFecha: %s",
                    nullSafe(user.getFullname(), user.getEmail()),
                    user.getEmail(),
                    companyCoursesBuilder.toString(),
                    subtotal,
                    descuentoTotal,
                    comision,
                    total,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );
            emailSenderService.sendEmail("contacto@aecode.ai", "Nueva compra de módulos", companyBody);

        } catch (MessagingException e) {
            // Si falla el email, igual devolvemos los accesos otorgados (puedes cambiar el comportamiento si quieres)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(saved);
        }

        return ResponseEntity.ok(saved);
    }

    // ======================
    // TRACKING
    // ======================
    /**
     * Marcar un módulo como completado por el usuario.
     */
    @PutMapping("/modules/{moduleId}/complete")
    public ResponseEntity<?> markModuleAsCompleted(
            @RequestParam String clerkId,
            @PathVariable Long moduleId
    ) {
        boolean updated = userAccessService.markModuleAsCompleted(clerkId, moduleId);
        return updated
                ? ResponseEntity.ok("Módulo marcado como completado")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Acceso al módulo no encontrado");
    }


    // ===== VER CONTENIDO DEL MÓDULO =====
// GET /user-access/modules/{moduleId}/content?clerkId=abc&videoId=123 (videoId opcional)
    @GetMapping("/modules/{moduleId}/content")
    public ResponseEntity<?> getModuleContent(
            @RequestParam String clerkId,
            @PathVariable Long moduleId,
            @RequestParam(required = false) Long videoId
    ) {
        return ResponseEntity.ok(userAccessService.getModuleContent(clerkId, moduleId, videoId));
    }

    // ===== MARCAR VIDEO COMO COMPLETADO =====
    // PUT /user-access/videos/{videoId}/complete?clerkId=abc   (body opcional: { "completed": true })
    @PutMapping("/videos/{videoId}/complete")
    public ResponseEntity<?> markVideoCompleted(
            @RequestParam String clerkId,
            @PathVariable Long videoId,
            @RequestBody(required = false) MarkVideoCompletedRequest body
    ) {
        Boolean completed = (body == null) ? Boolean.TRUE : body.getCompleted();
        return ResponseEntity.ok(userAccessService.markVideoCompleted(clerkId, videoId, completed));
    }



    // ======================
    // Helpers privados
    // ======================

    private static double nvl(Double d, double def) {
        return d == null ? def : d;
    }

    private static String safe(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;")
                .replace(">","&gt;").replace("\"","&quot;");
    }

    private static String nullSafe(String primary, String fallback) {
        return (primary == null || primary.isBlank()) ? fallback : primary;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}

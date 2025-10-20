package com.aecode.webcoursesback.servicesimplement.Business;

import com.aecode.webcoursesback.dtos.Business.BusinessDemoRequestDTO;
import com.aecode.webcoursesback.dtos.Business.SubmitBusinessDemoRequestDTO;
import com.aecode.webcoursesback.entities.Business.BusinessDemoRequest;
import com.aecode.webcoursesback.repositories.Business.BusinessDemoRequestRepository;
import com.aecode.webcoursesback.services.Business.BusinessDemoService;
import com.aecode.webcoursesback.services.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessDemoServiceImpl implements BusinessDemoService {

    private final BusinessDemoRequestRepository repo;
    private final EmailSenderService emailSender;

    private static String safe(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;");
    }

    @Override
    public BusinessDemoRequestDTO submit(SubmitBusinessDemoRequestDTO dto) {
        // persist
        BusinessDemoRequest e = BusinessDemoRequest.builder()
                .companyName(dto.getCompanyName().trim())
                .fullName(dto.getFullName().trim())
                .email(dto.getEmail().trim())
                .interestLine(dto.getInterestLine().trim())
                .countryCode(dto.getCountryCode().trim())
                .phone(dto.getPhone().trim())
                .message(dto.getMessage() != null ? dto.getMessage().trim() : null)
                .clerkId(dto.getClerkId())
                .status(BusinessDemoRequest.Status.PENDING)
                .build();

        e = repo.save(e);

        // emails
        sendUserConfirmEmail(e);
        sendCompanyNotifyEmail(e);

        // map
        return BusinessDemoRequestDTO.builder()
                .id(e.getId())
                .companyName(e.getCompanyName())
                .fullName(e.getFullName())
                .email(e.getEmail())
                .interestLine(e.getInterestLine())
                .countryCode(e.getCountryCode())
                .phone(e.getPhone())
                .message(e.getMessage())
                .status(e.getStatus().name())
                .createdAt(e.getCreatedAt() != null ? e.getCreatedAt().toString() : null)
                .build();
    }

    /* ================= Emails ================= */

    private void sendUserConfirmEmail(BusinessDemoRequest r) {
        String subject = "¡Solicitud de demo recibida! — AECODE";
        String html = buildUserHtml(r);
        try {
            emailSender.sendHtmlEmail(r.getEmail(), subject, html);
        } catch (Exception ex) {
            System.err.println("Fallo enviando email al usuario (demo): " + ex.getMessage());
        }
    }

    private void sendCompanyNotifyEmail(BusinessDemoRequest r) {
        String to = "contacto@aecode.ai"; // ajusta si deseas otro buzón
        String subject = "Nueva solicitud de DEMO (Business)";
        String body = String.format(java.util.Locale.US,
                "Nueva solicitud de DEMO (Business)\n\n" +
                        "Empresa: %s\n" +
                        "Nombre: %s\n" +
                        "Email: %s\n" +
                        "Interés: %s\n" +
                        "Teléfono: %s %s\n\n" +
                        "Mensaje:\n%s\n\n" +
                        "Estado: %s\n" +
                        "Creado: %s\n",
                r.getCompanyName(), r.getFullName(), r.getEmail(), r.getInterestLine(),
                r.getCountryCode(), r.getPhone(),
                r.getMessage() != null ? r.getMessage() : "(sin mensaje)",
                r.getStatus().name(),
                r.getCreatedAt() != null ? r.getCreatedAt().toString() : "(ahora)"
        );
        try {
            emailSender.sendEmail(to, subject, body);
        } catch (Exception ex) {
            System.err.println("Fallo enviando email a la empresa (demo): " + ex.getMessage());
        }
    }

    private String buildUserHtml(BusinessDemoRequest r) {
        String niceDate = java.time.OffsetDateTime.now()
                .toLocalDate()
                .format(java.time.format.DateTimeFormatter.ofPattern(
                        "dd 'de' MMMM 'del' yyyy", new java.util.Locale("es","ES")));

        String fullPhone = safe(r.getCountryCode()) + " " + safe(r.getPhone());

        // Mismo estilo base que tu email de Call For Presentation (ajustado al caso Business)
        String tpl = """
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Solicitud de demo recibida</title>
</head>
<body style="background:#FAFAFA;margin:0;padding:0;font-family:Verdana, Geneva, sans-serif;">
  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#FAFAFA;">
    <tr>
      <td align="center">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="background:#FFFFFF;box-shadow:0 2px 10px rgba(0,0,0,.06);">
          <tr>
            <td style="padding:0;" align="center">
              <img src="https://euqtuhd.stripocdn.email/content/guids/CABINET_d1422edc264bd643c8af51440e8995acef2448ffb48805c1983bece0ea0a568e/images/channels4_banner.jpg" width="600" style="display:block;border:0;max-width:100%%;height:auto;" alt="AECODE">
            </td>
          </tr>

          <tr>
            <td style="padding:22px 24px 8px 24px;" align="center">
              <h1 style="Margin:0;color:#333;font-size:28px;line-height:1.2;">¡Tu solicitud de demo fue recibida!</h1>
              <p style="Margin:10px 0 0 0;color:#333;font-size:14px;line-height:1.6;">
                Gracias, <strong>%s</strong>. Hemos recibido tu solicitud para coordinar una demo de
                <strong>%s</strong>.
              </p>
              <p style="Margin:6px 0 14px 0;color:#333;font-size:13px;">Fecha: %s</p>
            </td>
          </tr>

          <tr>
            <td style="padding:0 24px 0 24px;">
              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="border-top:2px solid #efefef;border-bottom:2px solid #efefef;">
                <tr>
                  <td style="padding:12px 0;color:#333;font-size:14px;">
                    <strong>Empresa:</strong> %s
                  </td>
                  <td style="padding:12px 0;color:#333;font-size:14px;" align="right">
                    <strong>Interés:</strong> %s
                  </td>
                </tr>
                <tr>
                  <td style="padding:0 0 12px 0;color:#333;font-size:14px;">
                    <strong>Correo:</strong> %s
                  </td>
                  <td style="padding:0 0 12px 0;color:#333;font-size:14px;" align="right">
                    <strong>Teléfono:</strong> %s
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <tr>
            <td style="padding:16px 24px 6px 24px;">
              <p style="Margin:0 0 8px 0;color:#333;font-size:14px;"><strong>Mensaje</strong></p>
              <div style="padding:14px 16px;background:#F7F7FF;border-left:4px solid #5C68E2;border-radius:6px;color:#333;font-size:14px;line-height:1.6;">
                %s
              </div>
            </td>
          </tr>

          <tr>
            <td style="padding:18px 24px 24px 24px;color:#333;font-size:13px;line-height:1.6;" align="center">
              Nuestro equipo se pondrá en contacto contigo muy pronto para coordinar la demo.<br>
              Si tienes preguntas, escríbenos a
              <a href="mailto:contacto@aecode.ai" style="color:#5C68E2;text-decoration:underline;">contacto@aecode.ai</a>.
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
""";

        return String.format(
                tpl,
                safe(r.getFullName()),
                safe(r.getInterestLine()),
                safe(niceDate),
                safe(r.getCompanyName()),
                safe(r.getInterestLine()),
                safe(r.getEmail()),
                fullPhone,
                safe(r.getMessage() != null ? r.getMessage() : "(sin mensaje)")
        );
    }
}

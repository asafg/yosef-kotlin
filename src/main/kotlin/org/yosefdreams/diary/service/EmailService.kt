package org.yosefdreams.diary.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    @Value("\${app.email.from}") private val fromEmail: String,
    @Value("\${app.email.reset-password.subject}") private val resetPasswordSubject: String,
    @Value("\${app.frontend.reset-password-url}") private val frontendResetPasswordUrl: String
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendPasswordResetEmail(toEmail: String, resetToken: String) {
        try {
            val resetUrl = "$frontendResetPasswordUrl?token=$resetToken"
            val message = """
                To reset your password, click the link below:
                
                $resetUrl
                
                This link will expire in 15 minutes.
                If you didn't request a password reset, please ignore this email.
            """.trimIndent()

            val mailMessage = SimpleMailMessage().apply {
                setFrom(fromEmail)
                setTo(toEmail)
                subject = resetPasswordSubject
                text = message
            }

            mailSender.send(mailMessage)
            logger.info("Password reset email sent to: {}", toEmail)
        } catch (e: Exception) {
            logger.error("Error sending password reset email to: $toEmail", e)
            throw RuntimeException("Error sending password reset email", e)
        }
    }
}

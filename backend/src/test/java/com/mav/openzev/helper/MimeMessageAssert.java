package com.mav.openzev.helper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.assertj.core.api.AbstractAssert;

public class MimeMessageAssert extends AbstractAssert<MimeMessageAssert, MimeMessage> {

  public static MimeMessageAssert assertThat(final MimeMessage actual) {
    return new MimeMessageAssert(actual);
  }

  protected MimeMessageAssert(final MimeMessage actual) {
    super(actual, MimeMessageAssert.class);
  }

  public MimeMessageAssert hasSubject(final String subject) {
    isNotNull();
    this.objects.assertNotNull(this.info, subject);

    try {
      if (!actual.getSubject().equals(subject)) {
        failWithExpectedToHaveButWas(subject, actual.getSubject());
      }
    } catch (final MessagingException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  private void failWithExpectedToHaveButWas(final String expected, final String actual) {
    failWithMessage("Expected to have '%s' but was '%s'", expected, actual);
  }
}

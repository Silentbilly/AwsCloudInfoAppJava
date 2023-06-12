package com.epam.cloudx.suiteRunners.tests.s3.sns.snsDeploymentValidation;

import com.epam.cloudx.suiteRunners.tests.CloudxImageBaseTest;
import com.epam.cloudx.utils.AwsUtils;
import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The application uses an SNS topic to subscribe and unsubscribe users, list existing subscriptions,
 * and send e-mail messages to subscribers about upload and delete image events, in a readable format (not JSON).
 * The application uses an SQS queue to publish event messages.
 * The application should have access to the SQS queue and the SNS topic via IAM roles. (If previous checks are passed,
 * the user with IAM role already has access to SNS and SQS)
 */

@Log4j
class SnsSqsDeploymentValidationTest extends CloudxImageBaseTest {

  @Test
  @DisplayName("The application uses an SNS topic to subscribe and unsubscribe users")
  @Tag("s3")
  @Tag("smoke")
  void appUsesSnsTopicToSubscribeAndUnsubscribeUsers() {
    Assertions.assertFalse(AwsUtils.getListOfSnsTopicsArn(snsClient).isEmpty(), "The list of SNS topics is empty");
  }

  @Test
  @DisplayName("The application uses an SQS queue to publish event messages")
  @Tag("s3")
  @Tag("smoke")
  void appUsesSqsQueueToPublishEventMessages() {
    Assertions.assertFalse(AwsUtils.getListOfSqsQueues(sqsClient).isEmpty(), "The list of SQS urls is empty");
  }
}

package com.epam.cloudx.utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.epam.cloudx.Exceptions.DuplicationInstanceNameException;
import com.epam.cloudx.Exceptions.ServiceUnavailableFromPublicException;
/*import com.sshtools.client.SshClient;
import com.sshtools.common.publickey.SshKeyUtils;
import com.sshtools.common.ssh.SshException;
import com.sshtools.common.ssh.components.SshKeyPair;*/
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j;

import java.io.File;

@Log4j
@UtilityClass
public class AwsUtils {
    public static AmazonEC2 createEc2Client(String accessKey, String secretKey) {
        var credentials = new BasicAWSCredentials(accessKey, secretKey);
        var region = Regions.EU_CENTRAL_1;
        log.info("Connecting to ec2");
        return AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }

    @SneakyThrows
    public static Instance getInstanceByName(String name, AmazonEC2 ec2) {
        log.info("Getting instance with name: " + name);
        // Filter reservations by name
        var reservations = ec2.describeInstances().getReservations().stream()
                .filter(s -> s.getInstances().toString().contains(name)).toList();

        // Find first. If more than 1, throw exception
        Reservation reservation;
        if (reservations.size() == 1) {
            reservation = reservations.get(0);
        } else {
            throw new DuplicationInstanceNameException("Duplication of names in EC2. Create unique name");
        }
        return reservation.getInstances().get(0);
    }

    public static String getInstanceStateByName(String name, AmazonEC2 ec2) {
        log.info("Getting state of instance " + name);
        return getInstanceByName(name, ec2).getState().getName();
    }

    @SneakyThrows
    public static String getPublicIpAddressByName(String name, AmazonEC2 ec2) {
        log.info("Getting public IP of instance " + name);
        if (getInstanceByName(name, ec2).getPublicIpAddress() != null) {
            return getInstanceByName(name, ec2).getPublicIpAddress();
        } else {
            String msg = "Public IP is empty. Instance is not accessible from internet";
            log.error(msg);
            throw new ServiceUnavailableFromPublicException(msg);
        }
    }

    @SneakyThrows
    public static String getPrivateIpAddressByName(String name, AmazonEC2 ec2) {
        if (getInstanceByName(name, ec2).getPublicIpAddress() != null) {
            return getInstanceByName(name, ec2).getPrivateIpAddress();
        } else {
            String msg = "Public IP is empty. Instance is not accessible from internet";
            log.error(msg);
            throw new ServiceUnavailableFromPublicException(msg);
        }
    }

/*    @SneakyThrows
    public static boolean isSshAccessible(Instance instance, String keyPairFilePath) {
        final String USER = "user_name";
        final String HOST = "ec2_public_address";
        final int PORT = 22;
        final String PRIVATE_KEY = "/home/aleks/Projects/AwsCloudInfoAppJava/src/main/resources/data/keys/cloudxinfo-eu-central-1.pem";
        final SshKeyPair pair = SshKeyUtils.getRSAPrivateKeyWithSHA256Signature(new File(PRIVATE_KEY), null);
        try (SshClient ssh = new SshClient(HOST, PORT, USER, pair)) {
            ssh.executeCommand("ssh -i \"cloudxinfo-eu-central-1.pem\" ec2-user@ec2-35-158-26-49.eu-central-1.compute.amazonaws.com");
            return true;
        } catch (SshException e) {
            return false; // SSH connection failed
        }
    }*/
}
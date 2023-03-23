package com.epam.cloudx.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AppInfo(@JsonProperty("availability_zone") String availabilityZone,
                      @JsonProperty("private_ipv4") String privateIpv4, String region) {
}

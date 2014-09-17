package com.motwin.sample;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.motwin.client.test.instruction.builder.Instructions;
import com.motwin.client.test.network.ClientEndPointContext;
import com.motwin.platform.spi.EndPointContext;

public class IntegrationTestCommon {

    private static final Map<String, String>  USER_INFO                   = ImmutableMap
                                                                                  .<String, String> builder()
                                                                                  .put(EndPointContext.USER_INFOS_APP_NAME,
                                                                                          "salesforce")
                                                                                  .put(EndPointContext.USER_INFOS_APP_VERSION,
                                                                                          "0.0.1")
                                                                                  .put(EndPointContext.USER_INFOS_DEVICE_TYPE,
                                                                                          "iPad")
                                                                                  .put(EndPointContext.USER_INFOS_DEVICE_SYSTEM,
                                                                                          "iPad")
                                                                                  .put(EndPointContext.USER_INFOS_DEVICE_VERSION,
                                                                                          "3.0.0").build();

    // Localhost Server Endpoint for integration test in bnp etude eco
    public static final ClientEndPointContext LOCALHOST_END_POINT_CONTEXT = Instructions.aEndPointContext("localhost",
                                                                                  1247, USER_INFO);

}

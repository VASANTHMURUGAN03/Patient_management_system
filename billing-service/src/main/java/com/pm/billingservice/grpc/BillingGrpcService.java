package com.pm.billingservice.grpc;

import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
public class BillingGrpcService extends BillingServiceGrpc.BillingServiceImplBase {
    @Override
    public void createBillingAccount(billing.BillingRequest request, StreamObserver<BillingResponse> responseObserver) {
      log.info("createBillingAccount request={}", request.toString());
      BillingResponse response=BillingResponse.newBuilder()
              .setAccountId("45678")
              .setStatus("Active")
              .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
}


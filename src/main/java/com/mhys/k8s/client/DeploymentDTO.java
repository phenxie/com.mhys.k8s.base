package com.mhys.k8s.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeploymentDTO {

//    "metadata_name":"test-bs",
//            "metadata_namespace":"test-namespace",
//            "labels_workLayer":"svc",
//            "spec_replicas":2,
//            "containers_name":"test-bsc",
//            "containers_image":"gsa-service-overview:v1.0.0",
//            "containers_imagePullPolicy":"IfNotPresent",
//            "remark":"测试项目"

    private String metadata_name;
    private String metadata_namespace;
    private String labels_workLayer;
    private Integer spec_replicas;
    private String containers_name;
    private String containers_image;
    private String containers_imagePullPolicy;
    private String remark;
}

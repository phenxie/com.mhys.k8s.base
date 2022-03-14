package com.mhys.k8s.client;

import  io.kubernetes.client.custom.IntOrString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceDTO {
//    "metadata_name":"test-bs",
//            "metadata_namespace":"test",
//            "labels_workLayer":"svc",
//            "spec_type":"ClusterIP",
//            "spec_ports_port":8995,
//            "spec_ports_targetPort":8995,
//            "spec_ports_protocol":"TCP"
         private String metadata_name;
         private String metadata_namespace;
         private String labels_workLayer;
         private String spec_type;
         private Integer spec_ports_port;
         private IntOrString spec_ports_targetPort;
         private Integer spec_ports_nodePort;
         private String spec_ports_protocol;
         private String remark;
}

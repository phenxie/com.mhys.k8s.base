package com.mhys.k8s.client;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Example {
    public static void main(String[] args) throws IOException, ApiException{
        ApiClient client ;
        String kubeConfigPath = "/Users/phenxie/103/config"; //8.240
//        kubeConfigPath="/Users/phenxie/103/18.11/config";
        client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();

//        V1PodList list = api.listPodForAllNamespaces(null, null, null, null,
//                null, null, null,
//                null, null,false);
//        for (V1Pod item : list.getItems()) {
//            System.out.println("pod  ->> "+item.getMetadata().getName());
//        }
//        V1Pod pod=new V1Pod();
//        pod.setApiVersion("");
//        //获取所有的节点
//      api.listNode(null,null,null,
//                null,null,null,
//                null,null,null
//        ,null).getItems().forEach(x->{
//          System.out.println("node --->"+x.getMetadata().getName());
//    });
//
//          api.listNamespace(null,null,null,null
//          ,null,null,null
//          ,null,null,null)
//                          .getItems().forEach(x->{
//                      System.out.println("nameSpace -->"+x.getMetadata().getName());
//                      try {
//                          api.listNamespacedService(x.getMetadata().getName(),null,
//                                  null,null,null,
//                                  null,null,null,null,
//                                  null,null).getItems().forEach(x1->{
//                              System.out.println("namespace:"+x.getMetadata().getName()+"  Service -->"+x1.getMetadata().getName());
//                          });
//                          api.listNamespacedResourceQuota(x.getMetadata().getName(),null,null,null,
//                                  null,null, null,null,
//                                  null, null,null).getItems().forEach(x2->{
//                              System.out.println("namespace: "+x.getMetadata().getName()+" NamespacedResource --->"+x2.getMetadata().toString());
//                          });
//                      } catch (ApiException e) {
//                          e.printStackTrace();
//                      }
//                  });
//            api.listConfigMapForAllNamespaces(null,null,null,
//                    null,null,null,null,null
//            ,null,null).getItems().forEach(x->{
//                System.out.println("ConfigMap -->"+x.getMetadata().getName());
//            });
////        api.createNamespacedPod("kube-system",pod,"","","");
//        V1Namespace namespace=new V1Namespace();
//        namespace.setApiVersion("v1");
//        namespace.setKind("Namespace");
//        V1ObjectMeta meta=new V1ObjectMeta();
//
//        meta.setName("test-namespace");
//        HashMap<String,String> map=new HashMap<>();
//        map.put("name","test-namespace");
//        meta.setLabels(map);
//        namespace.setMetadata(meta);
//        Map<String,String> reult=  creatNamespace(namespace);
//        System.out.println("---------------");
//        reult.keySet().forEach(x->{
//                System.out.println(x+"  -->"+ reult.get(x));
//        });
//
//        api.listEndpointsForAllNamespaces(null,null,null,
//                null,null,null,null,null,
//                null,null).getItems().forEach(x->{
//                 //   System.out.println("Endpoint --->"+x.getMetadata().toString());
//        });
//
//
//        api.listConfigMapForAllNamespaces(null,null,null,
//                null,null,null,null,null,null,
//                null).getItems().forEach(x->{
//                    System.out.println("configmap :"+x.getMetadata().getName() );
//        });



//        for(int i=0;i<20;i++){
//            createHadoop(api,"mhys-ai-"+i,false,"192.168.18.9:30050/library/hadoop:v1");
//        }
        api.listNode(null,null, null,null,null,
                null,null,null,null,null).getItems().forEach(n->{

                    String name= n.getStatus().getAddresses().get(0).getAddress();
                    System.out.println("----------------------"+name+"-----------------------------");
                    System.out.println(n.toString());
                   // 可用的资源
                  String acpu=  n.getStatus().getAllocatable().get("cpu").getNumber().toString();
                  String amem=  n.getStatus().getAllocatable().get("memory").getNumber().toString();
                  String apods=  n.getStatus().getAllocatable().get("pods").getNumber().toString();
                    //总资源

                    String tcpu=  n.getStatus().getCapacity().get("cpu").getNumber().toString();
                    String tmem=  n.getStatus().getCapacity().get("memory").getNumber().toString();
                    String tpods=  n.getStatus().getCapacity().get("pods").getNumber().toString();
                    System.out.println(name +"总计 CPU:"+tcpu +" 内存："+tmem+ " pods:"+tpods);
                    System.out.println(name +"可用 CPU:"+acpu +" 内存："+amem+ " pods:"+apods);
        });
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        api.listPersistentVolume(null,null,null,null
        ,null,null,null,null,null,null)
                .getItems().forEach(v->{
                    System.out.println(v.toString());
        });

//        test8_240(api);

    }
    public static void test8_240(CoreV1Api api)
    {
        for(int i=0;i<10;i++) {
            createHadoop(api,"name-space-"+i,false,"192.168.8.99:5000/mhys/hadoop:latest");
        }
        String namespace="name-space-1";
        System.out.println("-------------------pods---------------------------------");
        getPodsByNamespace(api,namespace).forEach(x->{
            System.out.println("===================pod================================");
            System.out.println(x.toString());
        });
        System.out.println("--------------------services--------------------------------");
        getServiceByNamespace(api,namespace).forEach(s->{
            System.out.println("*******************svc*************************************");
            System.out.println(s.toString());
        });
        System.out.println("----------------------namespace------------------------------");
//        System.out.println(getNamespaceByName(api,namespace).toString());
    }
    public static List<V1Service> getServiceByNamespace(CoreV1Api api, String namespace){
        try {
       V1ServiceList list=api.listNamespacedService(namespace, null,
                    null, null, null,
                    null, null, null, null, null, null);
        return list.getItems();
        }catch (ApiException e){
            System.out.println("获取Service信息失败");
            System.out.println("Status code: {}"+e.getCode());
            System.out.println("Reason: {}"+ e.getResponseBody());
            System.out.println("Response headers: {}"+ e.getResponseHeaders());
        }
        return null;
    }

    /****
     * 获取namepace下的所有pod  containerStatuses 是判断pod是否正常
     * @param api
     * @param namespace
     * @return
     */
    public static List<V1Pod> getPodsByNamespace(CoreV1Api api, String namespace){
        try {
            V1PodList list=api.listNamespacedPod(namespace, null,
                    null, null, null,
                    null, null, null, null, null,null);
            return list.getItems();
        }catch (ApiException e){
            System.out.println("获取 pods 信息失败");
            System.out.println("Status code: {}"+e.getCode());
            System.out.println("Reason: {}"+ e.getResponseBody());
            System.out.println("Response headers: {}"+ e.getResponseHeaders());
        }
        return null;
    }
    public static List<V1Namespace> getNamespaceByName(CoreV1Api api, String namespace){
        try {
            V1NamespaceList list=api.listNamespace(namespace, null,
                    null, null, null,
                    null, null, null, null, null);
            return list.getItems();
        }catch (ApiException e){
            System.out.println("获取 pods 信息失败");
            System.out.println("Status code: {}"+e.getCode());
            System.out.println("Reason: {}"+ e.getResponseBody());
            System.out.println("Response headers: {}"+ e.getResponseHeaders());
        }
        return null;
    }
    public static  void createWebIDE(CoreV1Api api, int index, boolean isdelete,String imageURL){
        String nameSpace="test-namespace";
        String serviceName="hadoop-hdfs-master2-form-api"+"-"+index;
        String configMapName="kube-hadoop-conf-form-api"+"-"+index;

        String masterPodName="hdfs-master"+"-"+index;
        //创建 configmap
        V1ConfigMap configMap=new V1ConfigMap();
        configMap.setKind("ConfigMap");
        configMap.setApiVersion("v1");
        V1ObjectMeta configMapDataMeta=new V1ObjectMeta();
        configMapDataMeta.setName(configMapName);
        configMapDataMeta.setNamespace(nameSpace);
        HashMap<String,String> configMapData=new HashMap<>();
        configMapData.put("HDFS_MASTER_SERVICE",serviceName);
        configMap.setMetadata(configMapDataMeta);
        configMap.setData(configMapData);
//        try {
//
////            if(api.readNamespacedConfigMap(configMapName,nameSpace,null).getImmutable()) {
//                api.createNamespacedConfigMap(nameSpace, configMap, null, null, null);
//                System.out.println("success" + "应用Configmap创建成功！");
////            }
//        }catch (ApiException e){
//            System.out.println("Exception when calling CoreV1Api#createNamespace");
//            System.out.println("Status code: {}"+e.getCode());
//            System.out.println("Reason: {}"+ e.getResponseBody());
//            System.out.println("Response headers: {}"+ e.getResponseHeaders());
//        }
//        if(isdelete) {
//            try {
//                api.deleteNamespacedConfigMap(configMapName, nameSpace, null, null, null, null, null, null);
//                System.out.println("success" + "Configmap 删除成功成功！");
//            } catch (ApiException e) {
//                System.out.println("Configmap 删除成功失败！");
//                System.out.println("Status code: {}" + e.getCode());
//                System.out.println("Reason: {}" + e.getResponseBody());
//                System.out.println("Response headers: {}" + e.getResponseHeaders());
//            }
//        }

        //创建serivce
        V1Service service=new V1Service();

        service.setApiVersion("v1");
        service.setKind("Service");
        V1ObjectMeta serviceMeta=new V1ObjectMeta();
        serviceMeta.setName(serviceName);
        service.setMetadata(serviceMeta);
        V1ServiceSpec serviceSpec=new V1ServiceSpec();
        serviceSpec.setType("NodePort");
        HashMap<String,String> serviceSpecMap=new HashMap<>();
        serviceSpecMap.put("app",masterPodName);
        serviceSpec.setSelector(serviceSpecMap);
        List<V1ServicePort> ports=new ArrayList<>();

        //nodeport 是外部访问的链接 The range of valid ports is 30000-32767
        int startPort=30200;

        ports.add(new V1ServicePort().name("web").port(3000).targetPort(new IntOrString(3000)).nodePort(startPort+4*index+1));
        ports.add(new V1ServicePort().name("mysql").port(3306).targetPort(new IntOrString(3306)).nodePort(startPort+4*index+2));
        ports.add(new V1ServicePort().name("liveserver").port(5500).targetPort(new IntOrString(5500)).nodePort(startPort+4*index+3));
        ports.add(new V1ServicePort().name("ide").port(8080).targetPort(new IntOrString(8080)).nodePort(startPort+4*index+4));

        //web_ide 3000 3306 5500 8080
        //hadoop 22 8080 9000 18808 50070



        serviceSpec.setPorts(ports);

        service.setSpec(serviceSpec);

        try {
//            if(api.readNamespacedService(serviceName,nameSpace,null).equals(null)) {
                api.createNamespacedService(nameSpace, service, null, null, null);
                System.out.println("创建Service成功");
//            }
        }catch (ApiException e){
            System.out.println("创建Service失败");
            System.out.println("Status code: {}"+e.getCode());
            System.out.println("Reason: {}"+ e.getResponseBody());
            System.out.println("Response headers: {}"+ e.getResponseHeaders());
        }
        if(isdelete) {
            try {
                api.deleteNamespacedService(serviceName, nameSpace, null, null, null, null, null, null);
                System.out.println("删除Service成功");
            } catch (ApiException e) {
                System.out.println("删除Service失败");
                System.out.println("Status code: {}" + e.getCode());
                System.out.println("Reason: {}" + e.getResponseBody());
                System.out.println("Response headers: {}" + e.getResponseHeaders());
            }
        }

        V1Pod masterPod=new V1Pod();
        masterPod.setApiVersion("v1");
        masterPod.setKind("Pod");
        V1ObjectMeta masterPodMeta=new V1ObjectMeta();
        masterPodMeta.setName(masterPodName);
        HashMap<String,String> masterPodMap=new HashMap<>();
        masterPodMap.put("app",masterPodName);
        masterPodMeta.setLabels(masterPodMap);
        masterPod.setMetadata(masterPodMeta);

        V1PodSpec v1PodSpec=new V1PodSpec();
        List<V1Container> masterContainer=new ArrayList<>();
        V1Container master=new V1Container();
        master.setImage(imageURL);
//        master.setImage("192.168.8.99:5000/mhys/hadoop:latest");
        master.setName("hdfs-master");
        master.setImagePullPolicy("IfNotPresent");
        List<V1ContainerPort> v1ContainerPorts=new ArrayList<>();
        v1ContainerPorts.add(new V1ContainerPort().containerPort(3000));
        v1ContainerPorts.add(new V1ContainerPort().containerPort(3306));
        v1ContainerPorts.add(new V1ContainerPort().containerPort(5500));
        v1ContainerPorts.add(new V1ContainerPort().containerPort(8080));
        master.setPorts(v1ContainerPorts);
        V1ResourceRequirements resourceRequirements=new V1ResourceRequirements();
        //设置pod的核心和内存大小
//        resourceRequirements.putRequestsItem("cpu", Quantity.fromString("1")).putRequestsItem("memory",Quantity.fromString("1G"));
        //设置pod的限制的核心和内存大小
//        resourceRequirements.putLimitsItem("cpu",Quantity.fromString("2")).putLimitsItem("memory",Quantity.fromString("2G"));
        master.setResources(resourceRequirements);
        //去掉configmap的耦合
//        List<V1EnvVar> v1EnvVars=new ArrayList<>();
//        v1EnvVars.add(new V1EnvVar().name("HADOOP_NODE_TYPE").value("namenode"));
//        v1EnvVars.add(new V1EnvVar().name("HDFS_MASTER_SERVICE").valueFrom(new V1EnvVarSource().configMapKeyRef(new V1ConfigMapKeySelector().name(configMapName).key("HDFS_MASTER_SERVICE"))));
////        v1EnvVars.add(new V1EnvVar().name("HDOOP_YARN_MASTER").valueFrom(new V1EnvVarSource().configMapKeyRef(new V1ConfigMapKeySelector().name(configMapName).key("HDOOP_YARN_MASTER"))));
//
//
//        master.setEnv(v1EnvVars);

        masterContainer.add(master);
        v1PodSpec.setContainers(masterContainer);
        v1PodSpec.setRestartPolicy("Always");

        masterPod.setSpec(v1PodSpec);

        try {
//            if(api.readNamespacedPod(masterPodName,nameSpace,null).equals(null)) {
                api.createNamespacedPod(nameSpace, masterPod, null, null, null);
                System.out.println("创建masterPod成功");
//            }
        }catch (ApiException e){
            System.out.println("创建Service失败");
            System.out.println("Status code: {}"+e.getCode());
            System.out.println("Reason: {}"+ e.getResponseBody());
            System.out.println("Response headers: {}"+ e.getResponseHeaders());
        }
        if(isdelete) {

            try {
                api.deleteNamespacedPod(masterPodName, nameSpace, null, null, null, null, null, null);
                System.out.println("删除masterPod成功");
            } catch (ApiException e) {
                System.out.println("删除Service失败");
                System.out.println("Status code: {}" + e.getCode());
                System.out.println("Reason: {}" + e.getResponseBody());
                System.out.println("Response headers: {}" + e.getResponseHeaders());
            }
        }


    }
    public static  void createHadoop(CoreV1Api api, String nameSpace, boolean isdelete,String imageURL){
//        String nameSpace="test-namespace-"+index;
        String serviceName="master";
        String configMapName="kube-hadoop-conf-form-api";

        String masterPodName="master";
        //创建 configmap
//        V1ConfigMap configMap=new V1ConfigMap();
//        configMap.setKind("ConfigMap");
//        configMap.setApiVersion("v1");
//        V1ObjectMeta configMapDataMeta=new V1ObjectMeta();
//        configMapDataMeta.setName(configMapName);
//        configMapDataMeta.setNamespace(nameSpace);
//        HashMap<String,String> configMapData=new HashMap<>();
//        configMapData.put("HDFS_MASTER_SERVICE",serviceName);
//        configMap.setMetadata(configMapDataMeta);
//        configMap.setData(configMapData);
//        try {
//
////            if(api.readNamespacedConfigMap(configMapName,nameSpace,null).getImmutable()) {
//                api.createNamespacedConfigMap(nameSpace, configMap, null, null, null);
//                System.out.println("success" + "应用Configmap创建成功！");
////            }
//        }catch (ApiException e){
//            System.out.println("Exception when calling CoreV1Api#createNamespace");
//            System.out.println("Status code: {}"+e.getCode());
//            System.out.println("Reason: {}"+ e.getResponseBody());
//            System.out.println("Response headers: {}"+ e.getResponseHeaders());
//        }
//        if(isdelete) {
//            try {
//                api.deleteNamespacedConfigMap(configMapName, nameSpace, null, null, null, null, null, null);
//                System.out.println("success" + "Configmap 删除成功成功！");
//            } catch (ApiException e) {
//                System.out.println("Configmap 删除成功失败！");
//                System.out.println("Status code: {}" + e.getCode());
//                System.out.println("Reason: {}" + e.getResponseBody());
//                System.out.println("Response headers: {}" + e.getResponseHeaders());
//            }
//        }


        V1Namespace namespace=new V1Namespace();
        namespace.setApiVersion("v1");
        namespace.setKind("Namespace");
        V1ObjectMeta meta=new V1ObjectMeta();

        meta.setName(nameSpace);
        HashMap<String,String> map=new HashMap<>();
        map.put("name",nameSpace);
        meta.setLabels(map);
        namespace.setMetadata(meta);


        try{
        api.createNamespace(namespace,null,null,null);
            System.out.println("创建namespace成功");
//            }
        }catch (ApiException e){
            System.out.println("创建 namespace 失败");
            System.out.println("Status code: {}"+e.getCode());
            System.out.println("Reason: {}"+ e.getResponseBody());
            System.out.println("Response headers: {}"+ e.getResponseHeaders());
        }
        if(isdelete){
            try{
                api.deleteNamespace(nameSpace,null,null,null,null,null,null);
                System.out.println("删除namespace成功");
//            }
            }catch (ApiException e){
                System.out.println("删除 namespace 失败");
                System.out.println("Status code: {}"+e.getCode());
                System.out.println("Reason: {}"+ e.getResponseBody());
                System.out.println("Response headers: {}"+ e.getResponseHeaders());
            }
        }
        createHadoopNode(api,isdelete,nameSpace,"master",imageURL);
        createHadoopNode(api,isdelete,nameSpace,"slave1",imageURL);
        createHadoopNode(api,isdelete,nameSpace,"slave2",imageURL);


//        V1Pod slave1Pod=new V1Pod();
//        slave1Pod.setApiVersion("v1");
//        slave1Pod.setKind("Pod");
//        V1ObjectMeta slave1PodMeta=new V1ObjectMeta();
//        slave1PodMeta.setName(masterPodName+"slave1");
//
//        HashMap<String,String> slave1PodMap=new HashMap<>();
//        slave1PodMap.put("app",masterPodName+"slave1");
//        slave1PodMeta.setLabels(slave1PodMap);
//        slave1Pod.setMetadata(slave1PodMeta);
//
//        V1PodSpec v1slave1PodSpec=new V1PodSpec();
//        List<V1Container> slave1Container=new ArrayList<>();
//        V1Container slave1=new V1Container();
////        master.setImage("192.168.8.99:5000/mhys_webide:v1");
//        slave1.setImage("192.168.8.99:5000/mhys/hadoop:latest");
//        //hadoop 22 8080 9000 18808 50070
//        slave1.setName("hdfs-slave1");
//
//        slave1.setImagePullPolicy("IfNotPresent");
//        List<V1ContainerPort> v1slave1ContainerPorts=new ArrayList<>();
//        v1slave1ContainerPorts.add(new V1ContainerPort().containerPort(22));
//        v1slave1ContainerPorts.add(new V1ContainerPort().containerPort(9000));
//        v1slave1ContainerPorts.add(new V1ContainerPort().containerPort(18808));
//        v1slave1ContainerPorts.add(new V1ContainerPort().containerPort(8080));
//        v1slave1ContainerPorts.add(new V1ContainerPort().containerPort(50070));
//        slave1.setPorts(v1slave1ContainerPorts);
//        V1ResourceRequirements slave1resourceRequirements=new V1ResourceRequirements();
//        //设置pod的核心和内存大小
////        resourceRequirements.putRequestsItem("cpu", Quantity.fromString("1")).putRequestsItem("memory",Quantity.fromString("1G"));
//        //设置pod的限制的核心和内存大小
////        resourceRequirements.putLimitsItem("cpu",Quantity.fromString("2")).putLimitsItem("memory",Quantity.fromString("2G"));
//        slave1.setResources(slave1resourceRequirements);
//        //去掉configmap的耦合
////        List<V1EnvVar> v1EnvVars=new ArrayList<>();
////        v1EnvVars.add(new V1EnvVar().name("HADOOP_NODE_TYPE").value("namenode"));
////        v1EnvVars.add(new V1EnvVar().name("HDFS_MASTER_SERVICE").valueFrom(new V1EnvVarSource().configMapKeyRef(new V1ConfigMapKeySelector().name(configMapName).key("HDFS_MASTER_SERVICE"))));
//////        v1EnvVars.add(new V1EnvVar().name("HDOOP_YARN_MASTER").valueFrom(new V1EnvVarSource().configMapKeyRef(new V1ConfigMapKeySelector().name(configMapName).key("HDOOP_YARN_MASTER"))));
////
////
////        master.setEnv(v1EnvVars);
//
//        slave1Container.add(slave1);
//        v1slave1PodSpec.setContainers(slave1Container);
//        v1slave1PodSpec.setRestartPolicy("Always");
//        //设置pod的主机名
//        v1slave1PodSpec.setHostname("slave1");
//
//        slave1Pod.setSpec(v1slave1PodSpec);
//
//        try {
////            if(api.readNamespacedPod(masterPodName,nameSpace,null).equals(null)) {
//            api.createNamespacedPod(nameSpace, slave1Pod, null, null, null);
//            System.out.println("创建slave1Pod成功");
////            }
//        }catch (ApiException e){
//            System.out.println("创建slave1Pod失败");
//            System.out.println("Status code: {}"+e.getCode());
//            System.out.println("Reason: {}"+ e.getResponseBody());
//            System.out.println("Response headers: {}"+ e.getResponseHeaders());
//        }
//        if(isdelete) {
//
//            try {
//                api.deleteNamespacedPod(masterPodName+"slave1", nameSpace, null, null, null, null, null, null);
//                System.out.println("删除masterPod成功");
//            } catch (ApiException e) {
//                System.out.println("删除Service失败");
//                System.out.println("Status code: {}" + e.getCode());
//                System.out.println("Reason: {}" + e.getResponseBody());
//                System.out.println("Response headers: {}" + e.getResponseHeaders());
//            }
//        }

    }

    public static void createHadoopNode(CoreV1Api api,boolean isdelete,String nameSpace,String serviceName,String imageURL){
        //创建serivce
        V1Service service=new V1Service();

        service.setApiVersion("v1");
        service.setKind("Service");
        V1ObjectMeta serviceMeta=new V1ObjectMeta();
        serviceMeta.setName(serviceName);

        service.setMetadata(serviceMeta);
        V1ServiceSpec serviceSpec=new V1ServiceSpec();
//        serviceSpec.setType("NodePort");
        HashMap<String,String> serviceSpecMap=new HashMap<>();
        serviceSpecMap.put("app",serviceName);
        serviceSpec.setSelector(serviceSpecMap);
        List<V1ServicePort> ports=new ArrayList<>();

        //nodeport 是外部访问的链接 The range of valid ports is 30000-32767
        // 设置了targetPort 会自动分配端口
        // nodePort 使用指定的外部端口

        int startPort=30200;
        int size=5;
        if(serviceName.toLowerCase().equals("master")) {
//            ports.add(new V1ServicePort().name("web").port(22).targetPort(new IntOrString(22)).nodePort(startPort + size * index + 1));
//            ports.add(new V1ServicePort().name("mysql").port(9000).targetPort(new IntOrString(9000)).nodePort(startPort + size * index + 2));
//            ports.add(new V1ServicePort().name("liveserver").port(18808).targetPort(new IntOrString(18808)).nodePort(startPort + size * index + 3));
//            ports.add(new V1ServicePort().name("ide").port(8080).targetPort(new IntOrString(8080)).nodePort(startPort + size * index + 4));
//            ports.add(new V1ServicePort().name("hadoop").port(50070).targetPort(new IntOrString(50070)).nodePort(startPort + size * index + 5));
            serviceSpec.setType("NodePort");
            ports.add(new V1ServicePort().name("web").port(22).targetPort(new IntOrString(22)));
            ports.add(new V1ServicePort().name("mysql").port(9000).targetPort(new IntOrString(9000)));
            ports.add(new V1ServicePort().name("liveserver").port(18808).targetPort(new IntOrString(18808)));
            ports.add(new V1ServicePort().name("ide").port(8080).targetPort(new IntOrString(8080)));
            ports.add(new V1ServicePort().name("hadoop").port(50070).targetPort(new IntOrString(50070)));
        }else{
            serviceSpec.setClusterIP("None");
            ports.add(new V1ServicePort().name("web").port(22));
            ports.add(new V1ServicePort().name("mysql").port(9000));
            ports.add(new V1ServicePort().name("liveserver").port(18808));
            ports.add(new V1ServicePort().name("ide").port(8080));
            ports.add(new V1ServicePort().name("hadoop").port(50070));

        }
        //web_ide 3000 3306 5500 8080
        //hadoop 22 8080 9000 18808 50070



        serviceSpec.setPorts(ports);

        service.setSpec(serviceSpec);

        try {
//            if(api.readNamespacedService(serviceName,nameSpace,null).equals(null)) {
            api.createNamespacedService(nameSpace, service, null, null, null);
            System.out.println("创建Service成功");
//            }
        }catch (ApiException e){
            System.out.println("创建Service失败");
            System.out.println("Status code: {}"+e.getCode());
            System.out.println("Reason: {}"+ e.getResponseBody());
            System.out.println("Response headers: {}"+ e.getResponseHeaders());
        }
        if(isdelete) {
            try {
                api.deleteNamespacedService(serviceName, nameSpace, null, null, null, null, null, null);
                System.out.println("删除Service成功");
            } catch (ApiException e) {
                System.out.println("删除Service失败");
                System.out.println("Status code: {}" + e.getCode());
                System.out.println("Reason: {}" + e.getResponseBody());
                System.out.println("Response headers: {}" + e.getResponseHeaders());
            }
        }

        //master 节点
        V1Pod masterPod=new V1Pod();
        masterPod.setApiVersion("v1");
        masterPod.setKind("Pod");
        V1ObjectMeta masterPodMeta=new V1ObjectMeta();
        masterPodMeta.setName(serviceName);

        HashMap<String,String> masterPodMap=new HashMap<>();
        masterPodMap.put("app",serviceName);
        masterPodMeta.setLabels(masterPodMap);
        masterPod.setMetadata(masterPodMeta);

        V1PodSpec v1PodSpec=new V1PodSpec();
        List<V1Container> masterContainer=new ArrayList<>();
        V1Container master=new V1Container();
//        master.setImage("192.168.8.99:5000/mhys_webide:v1");
//        master.setImage("192.168.8.99:5000/mhys/hadoop:latest");
        master.setImage(imageURL);
        //hadoop 22 8080 9000 18808 50070
        master.setName(serviceName);

        master.setImagePullPolicy("IfNotPresent");
        List<V1ContainerPort> v1ContainerPorts=new ArrayList<>();
        v1ContainerPorts.add(new V1ContainerPort().containerPort(22));
        v1ContainerPorts.add(new V1ContainerPort().containerPort(9000));
        v1ContainerPorts.add(new V1ContainerPort().containerPort(18808));
        v1ContainerPorts.add(new V1ContainerPort().containerPort(8080));
        v1ContainerPorts.add(new V1ContainerPort().containerPort(50070));
        master.setPorts(v1ContainerPorts);
        V1ResourceRequirements resourceRequirements=new V1ResourceRequirements();
        //设置pod的核心和内存大小
        resourceRequirements.putRequestsItem("cpu", Quantity.fromString("1")).putRequestsItem("memory",Quantity.fromString("1G"));
        //设置pod的限制的核心和内存大小
        resourceRequirements.putLimitsItem("cpu",Quantity.fromString("2")).putLimitsItem("memory",Quantity.fromString("2G"));
        master.setResources(resourceRequirements);
        //去掉configmap的耦合
//        List<V1EnvVar> v1EnvVars=new ArrayList<>();
//        v1EnvVars.add(new V1EnvVar().name("HADOOP_NODE_TYPE").value("namenode"));
//        v1EnvVars.add(new V1EnvVar().name("HDFS_MASTER_SERVICE").valueFrom(new V1EnvVarSource().configMapKeyRef(new V1ConfigMapKeySelector().name(configMapName).key("HDFS_MASTER_SERVICE"))));
////        v1EnvVars.add(new V1EnvVar().name("HDOOP_YARN_MASTER").valueFrom(new V1EnvVarSource().configMapKeyRef(new V1ConfigMapKeySelector().name(configMapName).key("HDOOP_YARN_MASTER"))));
//
//
//        master.setEnv(v1EnvVars);


        masterContainer.add(master);
        v1PodSpec.setContainers(masterContainer);
        v1PodSpec.setRestartPolicy("Always");
        //设置pod的主机名
        v1PodSpec.setHostname(serviceName);

        masterPod.setSpec(v1PodSpec);

        try {
//            if(api.readNamespacedPod(masterPodName,nameSpace,null).equals(null)) {
            api.createNamespacedPod(nameSpace, masterPod, null, null, null);
            System.out.println("创建masterPod成功");
//            }
        }catch (ApiException e){
            System.out.println("创建Service失败");
            System.out.println("Status code: {}"+e.getCode());
            System.out.println("Reason: {}"+ e.getResponseBody());
            System.out.println("Response headers: {}"+ e.getResponseHeaders());
        }
        if(isdelete) {

            try {
                api.deleteNamespacedPod(serviceName, nameSpace, null, null, null, null, null, null);
                System.out.println("删除masterPod成功");
            } catch (ApiException e) {
                System.out.println("删除Service失败");
                System.out.println("Status code: {}" + e.getCode());
                System.out.println("Reason: {}" + e.getResponseBody());
                System.out.println("Response headers: {}" + e.getResponseHeaders());
            }
        }

    }
    public static Map<String, String> creatNamespace(V1Namespace body) throws IOException {
        Map<String, String> message = new HashMap<>();
        // k8s初始化
        ApiClient client ;
        String kubeConfigPath = "/Users/phenxie/103/config";
        client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        Configuration.setDefaultApiClient(client);
        CoreV1Api apiInstance = new CoreV1Api();
        try {
            V1Namespace result = apiInstance.createNamespace(body, null, null, null);
            message.put("success", "应用命名空间创建成功！");
        } catch (ApiException e) {
            System.out.println("Exception when calling CoreV1Api#createNamespace");
            System.out.println("Status code: {}"+e.getCode());
            System.out.println("Reason: {}"+ e.getResponseBody());
            System.out.println("Response headers: {}"+ e.getResponseHeaders());
            if (e.getCode() == 409) {
                message.put("error", "命名空间已重复！");
            }
            if (e.getCode() == 200) {
                message.put("success", "应用命名空间创建成功！");
            }
            if (e.getCode() == 201) {
                message.put("error", "命名空间已重复！");
            }
            if (e.getCode() == 401) {
                message.put("error", "无权限操作！");
            }
            message.put("error", "应用命名空间创建失败！");
        }
        return message;
    }
    public Map<String, String> createService(ServiceDTO serviceDTO) throws IOException {
        Map<String, String> message = new HashMap<>();
        String nameStr = serviceDTO.getLabels_workLayer() + "-" + serviceDTO.getMetadata_name();
        // k8s初始化
        ApiClient client ;
        String kubeConfigPath = "/Users/phenxie/103/config";
        client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        Configuration.setDefaultApiClient(client);
        CoreV1Api apiInstance = new CoreV1Api(client);

        V1Service body = new V1Service();
        body.setApiVersion("v1");
        body.setKind("Service");

        V1ObjectMeta objectMeta = new V1ObjectMeta();
        objectMeta.setName(nameStr);
        objectMeta.setNamespace(serviceDTO.getMetadata_namespace());
        Map<String, String> annotation = new HashMap<>();
        annotation.put("k8s.eip.work/displayName", serviceDTO.getRemark());
        annotation.put("k8s.eip.work/workload", nameStr);
        objectMeta.setAnnotations(annotation);

        Map<String, String> Labels = new HashMap();
        Labels.put("k8s.eip.work/layer", serviceDTO.getLabels_workLayer());
        Labels.put("k8s.eip.work/name", nameStr);
        objectMeta.setLabels(Labels);

        V1ServiceSpec serviceSpec = new V1ServiceSpec();
        List<V1ServicePort> servicePorts = new ArrayList<>();
        serviceSpec.setType(serviceDTO.getSpec_type());
        V1ServicePort servicePort = new V1ServicePort();
        //servicePort.setName(serviceDTO.getSpec_ports_name());
        servicePort.setPort(serviceDTO.getSpec_ports_port());
        servicePort.setNodePort(serviceDTO.getSpec_ports_nodePort());
        servicePort.setProtocol(serviceDTO.getSpec_ports_protocol());
        servicePort.setTargetPort(serviceDTO.getSpec_ports_targetPort());
        servicePorts.add(servicePort);
        // selector
        Map<String, String> selector = new HashMap<>();
        selector.put("k8s.eip.work/layer", serviceDTO.getLabels_workLayer());
        selector.put("k8s.eip.work/name", nameStr);

        serviceSpec.setPorts(servicePorts);
        serviceSpec.setSelector(selector);
        body.setMetadata(objectMeta);
        body.setSpec(serviceSpec);

        try {
            V1Service result = apiInstance.createNamespacedService(serviceDTO.getMetadata_namespace(), body, null, null, null);
            message.put("success", "工作负载服务创建成功！");
        } catch (ApiException e) {
            if (e.getCode() == 409) {
                message.put("error", "工作负载服务创建已重复！");
            } else if (e.getCode() == 200) {
                message.put("success", "工作负载服务创建成功！");
            } else if (e.getCode() == 201) {
                message.put("error", "工作负载服务创建已重复！");
            } else if (e.getCode() == 401) {
                message.put("error", "无权限操作！");
            } else if (e.getCode() == 400) {
                message.put("error", "后台参数错误！");
            } else if (e.getCode() == 400) {
                message.put("error", "没有命名空间或没有Deployment！");
            } else {
                message.put("error", "工作负载服务创建失败！");
            }
//            log.error("Exception when calling AppsV1Api#createNamespacedDeployment");
//            log.error("Status code: {}", e.getCode());
//            log.error("Reason: {}", e.getResponseBody());
//            log.error("Response headers: {}", e.getResponseHeaders());
        }
        return message;
    }
    public Map<String, String> createDeployments(DeploymentDTO bodyDto) throws IOException {
        // 为了kuboard分层操作，不用kuboard可以不做操作，当然参数bodyDto.getLabels_workLayer()传递的是空的化默认就是不分层，加不加影响不大，都可以打进k8s部署
        String nameStr = bodyDto.getLabels_workLayer() + "-" + bodyDto.getMetadata_name();
        Map<String, String> messages = new HashMap<>();
        // 赋值操作
        V1Deployment body = new V1Deployment();
        body.setApiVersion("apps/v1");
        body.kind("Deployment");
        // 赋值metadata
        V1ObjectMeta objectMeta = new V1ObjectMeta();
        if (bodyDto.getLabels_workLayer() != null &&
                !bodyDto.getLabels_workLayer().equals("")) {
            objectMeta.setName(nameStr);
        }else{
            objectMeta.setName(bodyDto.getMetadata_name());
        }
        objectMeta.setNamespace(bodyDto.getMetadata_namespace());
        Map<String, String> annotation = new HashMap<>();
        annotation.put("k8s.eip.work/displayName", bodyDto.getRemark());
        annotation.put("k8s.eip.work/ingress", "false");
        annotation.put("k8s.eip.work/service", "ClusterIP");
        annotation.put("k8s.eip.work/workload", nameStr);
        // 默认不分层
        if (bodyDto.getLabels_workLayer() != null &&
                !bodyDto.getLabels_workLayer().equals("")) {
            objectMeta.setAnnotations(annotation);
        }

        // 赋值spec
        V1DeploymentSpec deploymentSpec = new V1DeploymentSpec();
        deploymentSpec.setReplicas(bodyDto.getSpec_replicas());

        // templete
        Map Labels = new HashMap();
        //Labels.put("app", "test-bs");
        if (bodyDto.getLabels_workLayer() != null &&
                !bodyDto.getLabels_workLayer().equals("")) {
            Labels.put("k8s.eip.work/layer", bodyDto.getLabels_workLayer());
            Labels.put("k8s.eip.work/name", nameStr);
        } else {
            Labels.put("app", bodyDto.getMetadata_name());
        }
        objectMeta.setLabels(Labels);
        V1PodTemplateSpec templateSpec = new V1PodTemplateSpec();
        templateSpec.setMetadata(objectMeta);

        // spec-Template下的Spec
        V1PodSpec podSpec = new V1PodSpec();
        // spec-Template-spec-container
        List<V1Container> listContainer = new ArrayList<>();
        V1Container container = new V1Container();
        container.setName(bodyDto.getContainers_name());
        container.setImage(bodyDto.getContainers_image());
        container.setImagePullPolicy(bodyDto.getContainers_imagePullPolicy());
        listContainer.add(container);
        podSpec.setContainers(listContainer);
        templateSpec.setSpec(podSpec);

        // spec-selector
        Map<String, String> matchLabels = new HashMap<>();
        if (bodyDto.getLabels_workLayer() != null &&
                !bodyDto.getLabels_workLayer().equals("")) {
            matchLabels.put("k8s.eip.work/layer", bodyDto.getLabels_workLayer());
            matchLabels.put("k8s.eip.work/name", bodyDto.getLabels_workLayer() + "-" + bodyDto.getMetadata_name());
        } else {
            matchLabels.put("app", bodyDto.getMetadata_name());
        }
        V1LabelSelector selector = new V1LabelSelector();
        selector.setMatchLabels(matchLabels);

        deploymentSpec.setTemplate(templateSpec);
        deploymentSpec.setSelector(selector);
        body.setMetadata(objectMeta);
        body.setSpec(deploymentSpec);
        //body.getSpec().getTemplate().getMetadata().setAnnotations(null);
        // k8s初始化
        ApiClient client ;
        String kubeConfigPath = "/Users/phenxie/103/config";
        client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        Configuration.setDefaultApiClient(client);
        CoreV1Api apiInstance = new CoreV1Api();
//        try {
//
//            V1Deployment result = apiInstance.createNamespacedDeployment(objectMeta.getNamespace(), body, null, null, null);
//            messages.put("success", "工作负载创建成功！");
//        } catch (ApiException e) {
//            if (e.getCode() == 409) {
//                messages.put("error", "工作负载创建已重复！");
//            } else if (e.getCode() == 200) {
//                messages.put("success", "工作负载创建成功！");
//            } else if (e.getCode() == 201) {
//                messages.put("error", "工作负载创建已重复！");
//            } else if (e.getCode() == 401) {
//                messages.put("error", "无权限操作！");
//            } else {
//                messages.put("error", "工作负载创建失败！");
//            }
//            log.error("Exception when calling AppsV1Api#createNamespacedDeployment");
//            log.error("Status code: {}", e.getCode());
//            log.error("Reason: {}", e.getResponseBody());
//            log.error("Response headers: {}", e.getResponseHeaders());
//        }
        return messages;
    }
}

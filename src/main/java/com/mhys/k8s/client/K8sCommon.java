package com.mhys.k8s.client;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiCallback;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class K8sCommon {

    public static final String APIVersion="v1";
    public static final String KIND_NAMESPACE="Namespace";
    public static final String KIND_SERVICE="Service";
    public static final String KIND_POD="Pod";
    public static final String SPEC_Type_NodePort="NodePort";
    public static final String Spec_ClusterIP_None="None";

    /***
     * 根据配置文件还构建api对象
     * @return
     */
    static CoreV1Api getAPI(){
        try {
            ApiClient client;
            //通过配置文件来构建apiclient
            String kubeConfigPath = "/Users/phenxie/103/config"; //8.240
//            kubeConfigPath="/Users/phenxie/103/18.11/config";
            client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
            Configuration.setDefaultApiClient(client);
            CoreV1Api api = new CoreV1Api();
            return api;
        }catch (Exception ex){

        }
        return null;
    }

    /***
     * 遍历所有的namespace 查看指定的namespace是否存在
     * @param namespace
     * @return
     */
    public static boolean isNamespaceExists(String namespace){
        CoreV1Api api=getAPI();
        try{
          V1NamespaceList list=  api.listNamespace(null,null,null,null,null,null
            ,null,null,null,null);
          AtomicBoolean isExists= new AtomicBoolean(false);
          list.getItems().forEach(n->{
              if(n.getMetadata().getName().toLowerCase().equals(namespace.toLowerCase())){
                  isExists.set(true);
              }
          });
          return isExists.get();
        }catch (ApiException ex){

            return false;
        }
    }

    /***
     * 删除namespace isDeleteSub标识是否删除namespace下的service和pod
     * @param namespace
     * @param isDeleteSub
     * @return
     */
    public static ApiException deleteNamespace(String namespace,boolean isDeleteSub){
        CoreV1Api api=getAPI();
        //如果namespace不存在，返回true
        if(!isNamespaceExists(namespace)){
            return  null;
        }
        //是否删除namespace下的service和pod
        if (isDeleteSub){
            try{
                //删除service
                api.listNamespacedService(namespace,null,null,null,null,null,null,null,null,null,null)
                        .getItems().forEach(s->{
                            try {
                                api.deleteNamespacedService(s.getMetadata().getName(), namespace.toLowerCase(), null, null, null, null, null, null);
                            }catch (ApiException ex){

                            }
                        });
                //删除pod
                api.listNamespacedPod(namespace,null,null,null,null,null,null,null,null,null,null)
                        .getItems().forEach(p->{
                            try{
                                api.deleteNamespacedPod(p.getMetadata().getName(),namespace.toLowerCase(),null,null,null,null,null,null);
                            }
                            catch (ApiException ex){

                            }
                });

            }catch (ApiException ex){
                return ex;
            }
        }
        try {
            //检查service和pod是否完全删除
            if(api.listNamespacedPod(namespace.toLowerCase(),null,null,null,null,null,null,null,null,null,null).getItems().size()==0
                    &&api.listNamespacedService(namespace.toLowerCase(),null,null,null,null,null,null,null,null,null,null).getItems().size()==0){
                //删除namespace
                api.deleteNamespace(namespace.toLowerCase(),null,null,null,null,null,null);
            }else{
                return null;
            }
        }catch (ApiException ex){
            return  ex;
        }
        return null;
    }

    /***
     * 删除namespace下的Service
     * @param namespace
     * @param serviceName
     * @return
     */
    public static  ApiException deleteNameSpaceService(String namespace,String serviceName){
        try{
            getAPI().deleteNamespacedService(serviceName.toLowerCase(),namespace.toLowerCase(),null,null,null,null,null, null);
            return  null;
        }catch (ApiException ex){
            return ex;
        }
    }

    /***
     * 删除namespace下的pod
     * @param namespace
     * @param podName
     * @return
     */
    public static  ApiException deleteNameSpacePod(String namespace,String podName){
        try{
            getAPI().deleteNamespacedPod(podName.toLowerCase(),namespace.toLowerCase(),null,null,null,null,null, null);
            return  null;
        }catch (ApiException ex){
            return ex;
        }
    }

    /****
     * 创建虚拟机 返回为null表示没有异常，创建成功！
     * @param nameSpace namespace要求全局唯一 会被转换成小写
     * @param imageURL 镜像地址
     * @param hostname 虚拟机的主机名 要求在namespace中是唯一的 作为其他的在同一个namespace中的主机访问的标识 会被转换成小写
     * @param portsMap 开放的端口映射
     * @param isExpose 是否开放给外部访问
     * @param islimit 是否限制内存和cpu，false表示不限制，设置false之后 cpucount和memsize将不起作用 true表示限制内存和cpu
     * @param cpuCount cpu的数量 小于1或者大于10 被设置成1
     * @param memSizeG 内存的大小 单位为G 小于1或者大于10 被设置成1
     * @return
     */
    public static ApiException createEndPoint(String nameSpace, String imageURL, String hostname, Map<String,Integer> portsMap,boolean isExpose,boolean islimit,int cpuCount,int memSizeG){

        CoreV1Api api=getAPI();
        nameSpace=nameSpace.toLowerCase();
        hostname=hostname.toLowerCase();

        //构建namespace对象
        V1Namespace namespace=new V1Namespace();
        namespace.setApiVersion(APIVersion);
        namespace.setKind(KIND_NAMESPACE);
        V1ObjectMeta meta=new V1ObjectMeta();
        meta.setName(nameSpace.toLowerCase());
        HashMap<String,String> map=new HashMap<>();
        //指定namespace的name 这个name需要是全局唯一的
        map.put("name",nameSpace.toLowerCase());
        meta.setLabels(map);
        namespace.setMetadata(meta);

        try{
            //如果namespace 不存在 就创建namespace
            if(!isNamespaceExists(nameSpace)) {
                api.createNamespace(namespace, null, null, null);
            }
        }catch (ApiException e){
//            System.out.println("创建 namespace 失败");
//            System.out.println("Status code: {}"+e.getCode());
//            System.out.println("Reason: {}"+ e.getResponseBody());
//            System.out.println("Response headers: {}"+ e.getResponseHeaders());
            return e;
        }

        //创建serivce
        V1Service service=new V1Service();

        service.setApiVersion(APIVersion);
        service.setKind(KIND_SERVICE);
        V1ObjectMeta serviceMeta=new V1ObjectMeta();
        serviceMeta.setName(hostname);
        service.setMetadata(serviceMeta);
        V1ServiceSpec serviceSpec=new V1ServiceSpec();
        HashMap<String,String> serviceSpecMap=new HashMap<>();
        serviceSpecMap.put("app",hostname);
        serviceSpec.setSelector(serviceSpecMap);
        List<V1ServicePort> ports=new ArrayList<>();

        //nodeport 是外部访问的链接 The range of valid ports is 30000-32767
        // 设置了targetPort 会自动分配端口
        // nodePort 使用指定的外部端口
        //isExpose 表示这个节点是否能够被外部访问
        if(isExpose) {
            serviceSpec.setType("NodePort");
            for (String  port :portsMap.keySet()) {
                ports.add(new V1ServicePort().name(port).port(portsMap.get(port)).targetPort(new IntOrString(portsMap.get(port))));
            }
        }else{
            serviceSpec.setClusterIP("None");
            for (String  port :portsMap.keySet()) {
                ports.add(new V1ServicePort().name(port).port(portsMap.get(port)));
            }
        }
        serviceSpec.setPorts(ports);
        service.setSpec(serviceSpec);

        try {
          api.createNamespacedService(nameSpace, service, null, null, null);
        }catch (ApiException e){
//            System.out.println("创建Service失败");
//            System.out.println("Status code: {}"+e.getCode());
//            System.out.println("Reason: {}"+ e.getResponseBody());
//            System.out.println("Response headers: {}"+ e.getResponseHeaders());
            return e;
        }


        //master 节点
        V1Pod masterPod=new V1Pod();
        masterPod.setApiVersion(APIVersion);
        masterPod.setKind(KIND_POD);
        V1ObjectMeta masterPodMeta=new V1ObjectMeta();
        masterPodMeta.setName(hostname);

        HashMap<String,String> masterPodMap=new HashMap<>();
        masterPodMap.put("app",hostname);
        masterPodMeta.setLabels(masterPodMap);
        masterPod.setMetadata(masterPodMeta);

        V1PodSpec v1PodSpec=new V1PodSpec();
        List<V1Container> masterContainer=new ArrayList<>();
        V1Container master=new V1Container();
        master.setImage(imageURL);
        master.setName(hostname);

        master.setImagePullPolicy("IfNotPresent");
        List<V1ContainerPort> v1ContainerPorts=new ArrayList<>();
        for (String  port :portsMap.keySet()) {
            v1ContainerPorts.add(new V1ContainerPort().containerPort(portsMap.get(port)));
        }
        master.setPorts(v1ContainerPorts);
        V1ResourceRequirements resourceRequirements=new V1ResourceRequirements();
        //设置pod的核心和内存大小
//        resourceRequirements.putRequestsItem("cpu", Quantity.fromString("1")).putRequestsItem("memory",Quantity.fromString("1G"));
        //设置pod的限制的核心和内存大小 如果cpucount 小于1或者大于10 则cpucount为1
        // memSizeG  小于1或者大于10 则memSizeG为1
        if(cpuCount<=1||cpuCount>=10){
            cpuCount=1;
        }
        if(memSizeG<=1 ||memSizeG>=10){
            memSizeG=1;
        }
        if(islimit)
        resourceRequirements.putLimitsItem("cpu",Quantity.fromString(cpuCount+"")).putLimitsItem("memory",Quantity.fromString(memSizeG+"G"));
        master.setResources(resourceRequirements);

        masterContainer.add(master);
        v1PodSpec.setContainers(masterContainer);
        v1PodSpec.setRestartPolicy("Always");
        //设置pod的主机名
        v1PodSpec.setHostname(hostname);

        masterPod.setSpec(v1PodSpec);

        try {
            api.createNamespacedPod(nameSpace, masterPod, null, null, null);
        }catch (ApiException e){
//            System.out.println("创建Service失败");
//            System.out.println("Status code: {}"+e.getCode());
//            System.out.println("Reason: {}"+ e.getResponseBody());
//            System.out.println("Response headers: {}"+ e.getResponseHeaders());
            return e;
        }

        return null;

    }

    /***
     * 找到namespace下的pod  用来判断虚拟机是否正常
     * @param namespace
     * @param podName
     * @return
     */
    public static V1Pod getPod(String namespace,String podName){
        try{
          List<V1Pod> pods=  getAPI().listNamespacedPod(namespace.toLowerCase(),null,null,null,null,null,null, null,null,null,null).getItems();
            AtomicReference<V1Pod> pod= new AtomicReference<>(new V1Pod());
            pods.forEach(p->{
               if( p.getMetadata().getName().toLowerCase().equals(podName.toLowerCase())){
                   pod.set(p);
               }
            });
            return pod.get();
        }catch (ApiException ex){
            return null;
        }
    }

    /****
     * 找到namepspace下的service 用来获取虚拟机的访问方式
     * @param namespace
     * @param serviceName
     * @return
     */
    public static V1Service getService(String namespace,String serviceName){
        try{
            List<V1Service> pods=  getAPI().listNamespacedService(namespace.toLowerCase(),null,null,null,null,null,null, null,null,null,null).getItems();
            AtomicReference<V1Service> svc= new AtomicReference<>(new V1Service());
            pods.forEach(s->{
                if( s.getMetadata().getName().toLowerCase().equals(serviceName.toLowerCase())){
                    svc.set(s);
                }
            });
            return svc.get();
        }catch (ApiException ex){
            return null;
        }
    }

    /****
     * 删除namespace下的hostname 如果hostname被删除之后namespace下没有service和pod  namespace也会被删除
     * @param nameSpace
     * @param hostname
     * @return
     */

    public static ApiException deleteEndPoint(String nameSpace,String hostname){
       ApiException svcEx=deleteNameSpaceService(nameSpace,hostname);
       ApiException podEx=deleteNameSpacePod(nameSpace,hostname);
       ApiException nsEx= deleteNamespace(nameSpace,false);
       if(svcEx==null && podEx==null){
           return nsEx;
       }else{
           return svcEx==null?podEx:svcEx;
       }
    }

    /***
     * 判断pod是否是正常运行
     * @param pod
     * @return
     */
    public static boolean isPodRunning(V1Pod pod){
        if(pod==null) return false;
        if(pod.getStatus()==null) return false;
        //如果ContainerStatuses 字段是null 表示pod不是正常运行状态
        if(pod.getStatus().getContainerStatuses()==null){
            return false;
        }
        return true;
    }

    public static void getNodeInfo(){
        CoreV1Api api=getAPI();
        try {
            api.listLimitRangeForAllNamespaces(null, null, null, null, null, null, null, null, null, null)
                    .getItems().forEach(l -> {
//                        System.out.println("--------");
//                        System.out.println(l.toString());
            });

//            api.listNodeWithHttpInfo(null,null,null,null,null,null,null,null,null,null)
//                    .getData().getItems().forEach(n->{
//                        System.out.println("===========");
//                        System.out.println(n.toString());
//            });
            api.listNode(null,null,null,null,null,null,null,null,null,null)
                    .getItems().forEach(n->{
                        System.out.println("==================");

                        System.out.println(n.toString());
            });
//            try {
//              String res=  api.listNodeAsync(null,null,null,null,null,null,null,null,null,null,null).execute().toString();
//              System.out.println(res);

//                api.listNodeCall(null, null, null, null, null, null, null, null, null, null,null).execute()
//                        .toString();
//            }catch (IOException ioe){
//                System.out.println(ioe.toString());
//            }



        }catch (ApiException ex){

        }
    }

    public static void main(String[] args) {
        String imgeurl="192.168.8.99:5000/mhys/hadoop:latest";
        //192.168.18.9:30050/library/hadoop:v1
        HashMap<String,Integer> ports=new HashMap<>();
        ports.put("ssh",22);
        ports.put("ide",8080);
        ports.put("hdfs",9000);
        ports.put("yarn",18808);
        ports.put("hadoop",50070);
        String user="User-";
//        for(int i=0;i<10;i++) {
//
//            createEndPoint(user+i,imgeurl,"master",ports,true,true,1,2);
//            createEndPoint(user+i,imgeurl,"slave1",ports,false,false,1,2);
//            createEndPoint(user+i,imgeurl,"slave2",ports,false,false,1,2);
////            deleteEndPoint(user+i,"master");
////            deleteEndPoint(user+i,"slave1");
////            deleteEndPoint(user+i,"slave2");
////            System.out.println(isPodRunning( getPod(user+i,"master"))?"Running":"NoRunning");
////            System.out.println(getService(user+i,"master"));
//
//
//        }
        getNodeInfo();

    }
}

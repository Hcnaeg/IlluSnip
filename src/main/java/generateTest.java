import PCSG.PATHS;
import beans.OPTTriple;
import beans.ResultBean;
//import example.illusnipTest;
import util.*;
import beans.OPTTriple;
import beans.ResultBean;

import java.sql.*;
import java.util.*;

public class generateTest {
    private final static int TIMEOUT = 360000;
    private final static int MAX_SIZE = 171; //20;
    Connection dashConn = JdbcUtil.getConnection(GlobalVariances.LOCAL);

//    private void getResult(int start, int end, String sizeFile, String resultFolder){
//        List<List<Integer>> dataset2Size = ReadFile.readInteger(sizeFile, "\t");
//        for (List<Integer> ds: dataset2Size){
//            int dataset = ds.get(0);
//            if (dataset < start || dataset > end) {
//                continue;
//            }
//            int MAX_SIZE = ds.get(1);
//            System.out.println("========" + dataset + ": " + MAX_SIZE  + "========");
//            OPTRank finder = new OPTRank(dataset, MAX_SIZE); // ======== MAX_SIZE ========
//            List<ResultBean> runningInfos = new ArrayList<>();
//            long runTime = timoutService(finder, dataset, runningInfos, TIMEOUT);//finder
//            boolean timeout = (runTime == Long.MAX_VALUE);
//            if (timeout) {
//                System.out.println("Time out");
//                Set<Integer> ids = new HashSet<>();
//                StringBuilder triplestr = new StringBuilder();
//                Set<OPTTriple> result = finder.result;
//                if (result.isEmpty()) {
//                    result = finder.currentSnippet;
//                }
//                for (OPTTriple iter: result){
//                    int sid = iter.getSid();
//                    int oid = iter.getOid();
//                    int pid = iter.getPid();
//                    ids.add(sid);
//                    ids.add(oid);
//                    triplestr.append(sid).append(" ").append(oid).append(" ").append(pid).append(",");
//                }
//                StringBuilder idstr = new StringBuilder();
//                for (int iter: ids){
//                    idstr.append(iter).append(",");
//                }
//                String snippetstr = "";
//                if (!idstr.toString().equals("")) {
//                    snippetstr = idstr.substring(0, idstr.length() - 1) + ";" + triplestr.substring(0, triplestr.length() - 1);
//                }
//                ResultBean bean = new ResultBean(dataset, snippetstr, TIMEOUT);
//                saveResult(bean, resultFolder);
//                continue;
//            }
//            System.out.println("Finish in: " + runTime + " ms. ");
//            ResultBean middleRuntimeBean = runningInfos.get(0);
//            saveResult(middleRuntimeBean, resultFolder);
//        }
//    }

    private void getResultBase(String timeFilePath){
        Set<Integer> dones = new HashSet<>();
//        List<String> lists = ReadFile.readString("/home/ttlin/IlluSnip-time-dataset.txt");
        List<String> lists = ReadFile.readString(timeFilePath);
        for(String s : lists){
            int dataset_id = Integer.parseInt(s.split("\t")[0]);
            dones.add(dataset_id);
        }
        try{
            Connection conn = JdbcUtil.getConnection(GlobalVariances.REMOTE);
            Connection dashConn = JdbcUtil.getConnection(GlobalVariances.LOCAL);
            dashConn.setAutoCommit(false);
//            String pidStr = "select * from pid where dataset_id in (select dataset_id from pid group by dataset_id having count(*)=1)";
            String pidStr = "select dataset_id from pid group by dataset_id having count(dataset_id)>1";
            Statement pidStmt = conn.createStatement();
            ResultSet pidRst = pidStmt.executeQuery(pidStr);

            int cnt=0;
            while(pidRst.next()){
                try{
                    int dataset_id = pidRst.getInt("dataset_id");
//                    int file_id = pidRst.getInt("file_id");
                    if(dones.contains(dataset_id)) continue;


//                    OPTRank finder = new OPTRank(file_id, MAX_SIZE); // ======== MAX_SIZE ========
                    OPTRank finder = new OPTRank(dataset_id, MAX_SIZE); // ======== MAX_SIZE ========
                    List<ResultBean> runningInfos = new ArrayList<>();
                    long runTime = timoutService(finder, dataset_id, runningInfos, TIMEOUT);//finder
                    boolean timeout = (runTime == Long.MAX_VALUE);
                    if (timeout) {
                        System.out.println("Time out: " + dataset_id);
                        Set<Integer> ids = new HashSet<>();
                        StringBuilder triplestr = new StringBuilder();
//                        Set<OPTTriple> result = finder.result;
                        ArrayList<OPTTriple> result = finder.result;
                        if (result.isEmpty()) {
                            result = finder.currentSnippet;
                        }

                        String snippetstr = result.toString().replace("[","").replace("]","");
                        ResultBean bean = new ResultBean(dataset_id, snippetstr, TIMEOUT);
                        bean.setDataset(dataset_id);
                        saveResult(bean, timeFilePath);
                        continue;
                    }

                    ResultBean middleRuntimeBean = runningInfos.get(0);
                    middleRuntimeBean.setDataset(dataset_id);
                    saveResult(middleRuntimeBean, timeFilePath);


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            System.out.println(cnt);


            /** file **/
//            String pidStr = "select distinct file_id from EDP where file_id not in (select distinct file_id from IlluSnip)";
////        String folder = "D:\\workplace\\calForRankDashboard\\Data-and-Code\\code\\example\\"; // change to your local folder where there are graph.txt and label.txt files.
//            Connection conn = JdbcUtil.getConnection(GlobalVariances.LOCAL);
//            Statement pidStmt = conn.createStatement();
//            ResultSet pidRst = pidStmt.executeQuery(pidStr);
//            while(pidRst.next()){
//                int resource_id = pidRst.getInt("file_id");
//
//                OPTRank finder = new OPTRank(resource_id, MAX_SIZE); // ======== MAX_SIZE ========
//                List<beans.ResultBean> runningInfos = new ArrayList<>();
//                long runTime = timoutService(finder, resource_id, runningInfos, TIMEOUT);//finder
//                boolean timeout = (runTime == Long.MAX_VALUE);
//                if (timeout) {
////                System.out.println("Time out");
//                    Set<Integer> ids = new HashSet<>();
//                    StringBuilder triplestr = new StringBuilder();
//                    Set<beans.OPTTriple> result = finder.result;
//                    if (result.isEmpty()) {
//                        result = finder.currentSnippet;
//                    }
//
//                    String snippetstr = result.toString().replace("[","").replace("]","");
//                    beans.ResultBean bean = new beans.ResultBean(resource_id, snippetstr, TIMEOUT);
//                    bean.setDataset(resource_id);
//                    saveResult(bean, timeFilePath);
//                    continue;
//                }
//
//                beans.ResultBean middleRuntimeBean = runningInfos.get(0);
//                middleRuntimeBean.setDataset(resource_id);
//                saveResult(middleRuntimeBean, timeFilePath);
//
//            }
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    private void saveResult(ResultBean bean, String timeFilePath) {
        try {
            String sql = String.format("INSERT INTO IlluSnip_3(dataset_id,snippet) values(%d,?)",bean.getDataset());

            PreparedStatement pst = dashConn.prepareStatement(sql);
            pst.setString(1, bean.getSnippet());
            pst.executeUpdate();
            pst.close();

//            System.out.println(bean.getSnippet());
            FileUtil.write(timeFilePath,bean.getDataset()+"\t"+ bean.runningTime + "\t" + bean.snippet);

//            PrintWriter writer = new PrintWriter(timeFilePath);
//            writer.println(bean.dataset);
//            writer.println(bean.runningTime);
//            writer.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long  timoutService(OPTRank finder, int datasetId, List<ResultBean> runningInfos, long timeout){
        long time = Long.MAX_VALUE;
        CustomedThread subThread = new CustomedThread(finder, datasetId, runningInfos);
        subThread.start();
        try {
            subThread.join(timeout);

            if(!subThread.isAlive())
                time = subThread.lastTime;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        subThread.interrupt();
        return time;
    }

    private static class CustomedThread extends Thread{
        public long lastTime = Long.MAX_VALUE;
        OPTRank finder;
        int datasetId;
//        Set<OPTTriple> result;
        ArrayList<OPTTriple> result;
        List<ResultBean> runningInfos;
        public CustomedThread(OPTRank finder, int datasetId, List<ResultBean> runningInfos) {
            super();
            this.finder = finder;
            this.datasetId = datasetId;
            this.runningInfos = runningInfos;
        }
        @Override
        public void run(){
            long start = System.currentTimeMillis();
            try {
                finder.findSnippet();
                result = finder.result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            lastTime = System.currentTimeMillis() - start;
            /**?????????snippetString*/
//            Set<Integer> ids = new HashSet<>();
//            StringBuilder triplestr = new StringBuilder();
//            for (beans.OPTTriple iter: result){
//                int sid = iter.getSid();
//                int oid = iter.getOid();
//                int pid = iter.getPid();
//                ids.add(sid);
//                ids.add(oid);
//                triplestr.append(sid).append(" ").append(oid).append(" ").append(pid).append(",");
//            }
//            StringBuilder idstr = new StringBuilder();
//            for (int iter: ids){
//                idstr.append(iter).append(",");
//            }
//            String snippetstr = idstr.substring(0, idstr.length() - 1) + ";" + triplestr.substring(0, triplestr.length() - 1);
            String snippetstr = result.toString().replace("[","").replace("]","");
            ResultBean bean = new ResultBean(datasetId, snippetstr, lastTime);
            runningInfos.add(bean);
        }
    }

    public static void main(String[] args){
        generateTest test = new generateTest();
        test.getResultBase("C:\\Users\\17223\\Desktop\\websoft\\code\\IlluSnip\\src\\main\\resources\\tmp4.txt");
//        test.getResultBase("/home/wqluo/file/opt_max_result_deduplicate.txt");
//        test.getResultBase("/home/wqluo/file/opt_max_result.tsv");
//        test.getResultBase("IlluSnip-file-1088.txt");

//        test.getResult(Integer.parseInt(args[0]), Integer.parseInt(args[1]), PATHS.FileBase + "file/SnippetResultCount90.txt", PATHS.ProjectData + "IlluSnipResult90/");
//        test.getResult(Integer.parseInt(args[0]), Integer.parseInt(args[1]), PATHS.FileBase + "file/SnippetResultCount80.txt", PATHS.ProjectData + "IlluSnipResult80/");
//        test.getResultBase(Integer.parseInt(args[0]), Integer.parseInt(args[1]), PATHS.ProjectData + "IlluSnipResult20/");
    }
}

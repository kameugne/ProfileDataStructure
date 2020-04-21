import java.io.BufferedWriter;
import java.io.FileWriter;

public class Main
{
    public static void main(String[] args)throws Exception
    {


        /*for(int l = 1; l<= 1; l++) {
            for (int k = 1; k <= 1; k++) {
                //fileName = dir + "j30_" + l + "_" + k + ".rcp";
                RCPSPInstance data = new RCPSPInstance("Data/BL/sample.txt");
                System.out.println("nbTask :" +data.numberOfTasks + "  nbRes :"+ data.numberOfResources);
                for(int res = 0; res < data.numberOfResources; res++){
                    System.out.println("capacity_ :" + res + " :" + data.capacities[res]);
                }
                for(int i = 0; i < data.numberOfTasks; i++){
                    System.out.println("duration_ :" + i + " :" + data.processingTimes[i]);
                    for(int r = 0; r < data.numberOfResources; r++)
                        System.out.println("res_ :" + i + " :" + data.heights[r][i]);

                }
            }
        }*/



    	/*String fileName;
    	runRCPSP sample1;System.out.println("Inst |" + "old_time |" + "old_backt |" + "makespan |" + "old_prop |" + "new_time |" + "new_backt |" + "makespan |" + "new_prop|");
        fileName = "Data/BL/sample.txt";
        for (int prop = 0; prop < 2; prop++) {
            for (int branch = 0; branch < 1; branch++) {
                sample1 = new runRCPSP(fileName, prop, branch);
                System.out.print(+sample1.howMuchTime() + " | " + sample1.howManyBacktracks() + " | " + sample1.makeSpanSolution() + " | " + sample1.howManyAdjustments() + " | ");
            }
        }*/





        /*
         * Configuration for the pack instances
         *
         */
        runRCPSP sample1;
        String fileName;

    	String dir =  "Data/BL/";
        //BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/kameugne/Downloads/CARI 2020/HorizEdgeFinderFetgoTayou/Results/newBl20results.txt"));
        //writer.write("Inst |" + "old_time |" + "old_backt |" + "makespan |" + "old_prop |" + "new_time |" + "new_backt |" + "makespan |" + "new_prop|");
        System.out.println("Inst |" + "old_time_s |" + "old_backt_s |" + "makespan_s |" + "old_prop_s |"
                + "old_time_d |" + "old_backt_d |" + "makespan_d |" + "old_prop_d |" + "new_time_s |" + "new_backt_s |" +
                "makespan_s |" + "new_prop_s|" + "new_time_d |" + "new_backt_d |" + "makespan_d |" + "new_prop_d|");
        //writer.newLine();
        for(int j = 1; j<= 20; j++) {
            fileName = dir + "bl20_" + j + ".rcp";
            String name = "bl20_" + j;
            System.out.print(name + ".rcp" + " | ");
            //writer.write(name + ".rcp" + " | ");
            for (int prop = 0; prop < 3; prop++) {
                for (int branch = 0; branch < 1; branch++) {
                    sample1 = new runRCPSP(fileName, prop, branch);
                    //writer.write(+sample1.howMuchTime() + " | " + sample1.howManyBacktracks() + " | " + sample1.makeSpanSolution() + " | " + sample1.howManyAdjustments() + " | ");
                    System.out.print(+sample1.howMuchTime() + " | " + sample1.howManyBacktracks() + " | " + sample1.makeSpanSolution() + " | " + sample1.howManyAdjustments() + " | ");
                }
            }
            //writer.newLine();
            System.out.println(" ");
        }
        //writer.close();

        /*String dir =  "Data/BL/";
        //BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/kameugne/Downloads/CARI 2020/HorizEdgeFinderFetgoTayou/Results/newBl25results.txt"));
        //writer.write("Inst |" + "old_time |" + "old_backt |" + "makespan |" + "old_prop |" + "new_time |" + "new_backt |" + "makespan |" + "new_prop|");
        System.out.println("Inst |" + "old_time_s |" + "old_backt_s |" + "makespan_s |" + "old_prop_s |"
                + "old_time_d |" + "old_backt_d |" + "makespan_d |" + "old_prop_d |" + "new_time_s |" + "new_backt_s |" +
                "makespan_s |" + "new_prop_s|" + "new_time_d |" + "new_backt_d |" + "makespan_d |" + "new_prop_d|");
        //writer.newLine();
        for(int j = 14; j<= 20; j++) {
            fileName = dir + "bl25_" + j + ".rcp";
            String name = "bl25_" + j;
            System.out.print(name + ".rcp" + " | ");
            //writer.write(name + ".rcp" + " | ");
            for (int prop = 0; prop < 2; prop++) {
                for (int branch = 0; branch < 1; branch++) {
                    sample1 = new runRCPSP(fileName, prop, branch);
                    //writer.write(+sample1.howMuchTime() + " | " + sample1.howManyBacktracks() + " | " + sample1.makeSpanSolution() + " | " + sample1.howManyAdjustments() + " | ");
                    System.out.print(+sample1.howMuchTime() + " | " + sample1.howManyBacktracks() + " | " + sample1.makeSpanSolution() + " | " + sample1.howManyAdjustments() + " | ");
                }
            }
            //writer.newLine();
            System.out.println(" ");
        }
        //writer.close();*/


    	/*
    	* Configuration for the pack instances
    	*
    	*/

        /*String dir =  "Data/Pack/";
        //BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/kameugne/Downloads/CARI 2020/HorizEdgeFinderFetgoTayou/Results/newPackresults.txt"));
        //writer.write("Inst |" + "old_time |" + "old_backt |" + "makespan |" + "old_prop |" + "new_time |" + "new_backt |" + "makespan |" + "new_prop|");
        System.out.println("Inst |" + "old_time_s |" + "old_backt_s |" + "makespan_s |" + "old_prop_s |"
                + "old_time_d |" + "old_backt_d |" + "makespan_d |" + "old_prop_d |" + "new_time_s |" + "new_backt_s |" +
                "makespan_s |" + "new_prop_s|" + "new_time_d |" + "new_backt_d |" + "makespan_d |" + "new_prop_d|");
        //writer.newLine();
        for(int j = 1; j<= 55; j++) {
            String name ="";
            if(j < 10) {
                fileName = dir + "pack00" + j + ".rcp";
                 name += "pack00" + j;
            }else {
                fileName = dir + "pack0" + j + ".rcp";
                 name += "pack0" + j;
            }
            System.out.print(name + ".rcp" + " | ");
            //writer.write(name + ".rcp" + " | ");
            for (int prop = 0; prop < 2; prop++) {
                for (int branch = 0; branch < 1; branch++) {
                    sample1 = new runRCPSP(fileName, prop, branch);
                    //writer.write(+sample1.howMuchTime() + " | " + sample1.howManyBacktracks() + " | " + sample1.makeSpanSolution() + " | " + sample1.howManyAdjustments() + " | ");
                    System.out.print(+sample1.howMuchTime() + " | " + sample1.howManyBacktracks() + " | " + sample1.makeSpanSolution() + " | " + sample1.howManyAdjustments() + " | ");
                }
            }
            //writer.newLine();
            System.out.println(" ");
        }
        //writer.close();*/



        /*
         * Configuration for the pack_d instances
         *
         */

        /*String dir =  "Data/data_Pack_d/";
        BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/kameugne/Downloads/CARI 2020/HorizEdgeFinderFetgoTayou/Results/newPack_dresults.txt"));
        writer.write("Inst |" + "old_time |" + "old_backt |" + "makespan |" + "old_prop |" + "new_time |" + "new_backt |" + "makespan |" + "new_prop|");
        System.out.println("Inst |" + "old_time_s |" + "old_backt_s |" + "makespan_s |" + "old_prop_s |"
                + "old_time_d |" + "old_backt_d |" + "makespan_d |" + "old_prop_d |" + "new_time_s |" + "new_backt_s |" +
                "makespan_s |" + "new_prop_s|" + "new_time_d |" + "new_backt_d |" + "makespan_d |" + "new_prop_d|");
        writer.newLine();
        for(int j = 1; j<= 55; j++) {
            String name ="";
            if(j < 10) {
                fileName = dir + "pack00" + j + ".rcp";
                 name += "pack00" + j;
            }else {
                fileName = dir + "pack0" + j + ".rcp";
                 name += "pack0" + j;
            }
            writer.write(name + ".rcp" + " | ");
            System.out.print(name + ".rcp" + " | ");
            for (int prop = 0; prop < 2; prop++) {
                for (int branch = 0; branch < 2; branch++) {
                    sample1 = new runRCPSP(fileName, prop, branch);
                    writer.write(+sample1.howMuchTime() + " | " + sample1.howManyBacktracks() + " | " + sample1.makeSpanSolution() + " | " + sample1.howManyAdjustments() + " | ");
                    System.out.print(+sample1.howMuchTime() + " | " + sample1.howManyBacktracks() + " | " + sample1.makeSpanSolution() + " | " + sample1.howManyAdjustments() + " | ");
                }
            }
            writer.newLine();
            System.out.println(" ");
        }
        writer.close();*/






        /*
         * Configuration for the KSD15 instances
         *
         */

    	/*String dir =  "Data/KSD15/";
        //BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/kameugne/Downloads/CARI 2020/HorizEdgeFinderFetgoTayou/Results/newKSD15results.txt"));
        //writer.write("Inst |" + "old_time |" + "old_backt |" + "makespan |" + "old_prop |" + "new_time |" + "new_backt |" + "makespan |" + "new_prop|");
        System.out.println("Inst |" + "old_time_s |" + "old_backt_s |" + "makespan_s |" + "old_prop_s |"
                + "old_time_d |" + "old_backt_d |" + "makespan_d |" + "old_prop_d |" + "new_time_s |" + "new_backt_s |" +
                "makespan_s |" + "new_prop_s|" + "new_time_d |" + "new_backt_d |" + "makespan_d |" + "new_prop_d|");
        //writer.newLine();
        for(int l = 1; l<= 48; l++) {
            for(int k = 1; k <= 10; k++){
                fileName = dir + "j30_" + l + "_" + k + ".rcp";
                String name = "j30_" + l+ "_" + k;
                //writer.write(name + ".rcp" + " | ");
                System.out.print(name + ".rcp" + " | ");
                for (int prop = 0; prop < 2; prop++) {
                    for (int branch = 0; branch < 1; branch++) {
                        sample1 = new runRCPSP(fileName, prop, branch);
                        //writer.write(+sample1.howMuchTime() + " | " + sample1.howManyBacktracks() + " | " + sample1.makeSpanSolution() + " | " + sample1.howManyAdjustments() + " | ");
                        System.out.print(+sample1.howMuchTime() + " | " + sample1.howManyBacktracks() + " | " + sample1.makeSpanSolution() + " | " + sample1.howManyAdjustments() + " | ");
                    }
                }
                //writer.newLine();
                System.out.println(" ");
            }
        }
        //writer.close();*/
    }
}

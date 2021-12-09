import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.Runnable;

public class Runner1 extends Thread {
    int dataset;
    String filePilihanInput;
    String namadataset;
    boolean tampil;
    int itr;

    int[][] conflictMatrix, sortedCourse, weightedClashMatrix, sortedWeightedCourse;
    int timeslot[];

    public Runner1(int dataset, String filePilihanInput, String namadataset, boolean tampil, int itr) {
        this.dataset = dataset;
        this.filePilihanInput = filePilihanInput;
        this.namadataset = namadataset;
        this.tampil = tampil;
        this.itr = itr;
    }

    @Override
    public void run() {
        if (tampil) {
            System.out.println("\n================================================\n");
        }
        CourseData course = new CourseData(filePilihanInput);

        // Mendapatkan conflict_matrix:
        conflictMatrix = course.getConflictMatrix();
        course.tampil = tampil;
        course.showConflictMatrix(50);
        System.out.println(" ");

        // Mendapatkan hasil sorting largest degree:
        sortedCourse = course.sortByDegree();
        System.out.println("\n================================================\n");

        // Melakukan scheduling (Largest Degree)
        ExamScheduling sch = new ExamScheduling(conflictMatrix, sortedCourse);
        sch.tampil = tampil;

        timeslot = sch.scheduleByDegree();

        // Mengecek apakah ditemukan konflik pada schedule
        System.out.println("Dataset yang dipilih : " + namadataset);
        System.out.println("Jumlah Iterasi : " + itr);
        System.out.println("Ada konflik? : " + (sch.isConflicted() ? "Ya" : "Tidak"));

        int minimumTimeslot = sch.getTimeslot();
        System.out.println("Minimal Timeslots: " + minimumTimeslot);
        double initialPenalty = Penalty.countPenalty(course.getStudentData(), timeslot);

        // random metaheuristic

        SimulatedAnnealing SA = new SimulatedAnnealing(conflictMatrix, timeslot, itr, minimumTimeslot,
                course.getStudentData());
        SA.tampil = tampil;
        long startTimeSA = System.nanoTime();
        System.out.println(namadataset+" Optimized Timeslots with Simulated Annealing Algorithm : " + SA.optimizeTimeslot());
        long endTimeSA = System.nanoTime();
        double penaltySA = SA.finalPenalty;
        long timeElapsedSA = endTimeSA - startTimeSA;

        double delta = ((initialPenalty - penaltySA) / initialPenalty) * 100;

        System.out.println(namadataset+" Initial penalty = " + initialPenalty);
        System.out.println(namadataset+" Final penalty = " + penaltySA);
        System.out.println(namadataset+" Delta = " + delta + "%");
        System.out.println(namadataset+" Simulated Annealing execution time in miliseconds : " +
                timeElapsedSA / 1000000);
    }
}
package unsw.blackout.algorithms;

import java.util.ArrayList;
import java.util.List;
import unsw.blackout.files.File;

public class KnapsackSolver {
    // Returns the files that can be included to maximize the total transferred bytes
    public static List<File> solveKnapsack(List<File> files, int capacity) {
        int n = files.size();
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            File file = files.get(i - 1);
            int weight = file.getSize();
            int value = file.getTransferredBytes();

            for (int j = 1; j <= capacity; j++) {
                if (weight <= j) {
                    dp[i][j] = Math.max(dp[i - 1][j], value + dp[i - 1][j - weight]);
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }

        // Backtrack to find the files to keep
        List<File> filesToKeep = new ArrayList<>();
        int w = capacity;
        for (int i = n; i > 0 && w > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                File file = files.get(i - 1);
                filesToKeep.add(file);
                w -= file.getSize();
            }
        }

        return filesToKeep;
    }
}

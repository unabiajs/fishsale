package main;

import config.config;
import java.util.*;

public class SaleDetailManager {

    private config conf;
    private Scanner sc;

    public SaleDetailManager(config conf, Scanner sc) {
        this.conf = conf;
        this.sc = sc;
    }

    // View ALL sale details for a given sale ID
    public void viewDetailsBySaleId() {
        System.out.print("Enter Sale ID: ");
        int sid = readIntSafe();

        String sql = "SELECT sd.sd_id, sd.s_id, f.f_name, sd.quantity_kg, sd.subtotal " +
                     "FROM tbl_saledetail sd " +
                     "LEFT JOIN tbl_fish f ON sd.f_id = f.f_id " +
                     "WHERE sd.s_id=?";

        List<Map<String, Object>> rows = conf.fetchRecords(sql, sid);
        if (rows.isEmpty()) {
            System.out.println("No sale details found for this Sale ID.");
            return;
        }

        System.out.println("-----------------------------------------------------");
        System.out.printf("| %-6s | %-6s | %-15s | %-10s | %-10s |\n",
                "SD_ID", "S_ID", "Fish", "Qty (kg)", "Subtotal");
        System.out.println("-----------------------------------------------------");
        for (Map<String, Object> r : rows) {
            System.out.printf("| %-6s | %-6s | %-15s | %-10s | %-10s |\n",
                    r.get("sd_id"), r.get("s_id"), r.get("f_name"), 
                    r.get("quantity_kg"), r.get("subtotal"));
        }
        System.out.println("-----------------------------------------------------");
    }

    // Utility to safely read integers
    private int readIntSafe() {
        while (true) {
            String line = sc.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }
}

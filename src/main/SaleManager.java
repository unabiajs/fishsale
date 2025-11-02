package main;

import config.config;
import java.util.*;

public class SaleManager {

    private config conf;
    private Scanner sc;
    private FishManager fishManager;

    public SaleManager(config conf, Scanner sc, FishManager fm) {
        this.conf = conf;
        this.sc = sc;
        this.fishManager = fm;
    }

    // show summary of all sales
    public void viewAllSales() {
        String sql = "SELECT * FROM tbl_sale";
        String[] headers = {"SaleID", "UserID", "Status", "Date", "Total"};
        String[] cols = {"s_id", "u_id", "status", "date", "total"};
        conf.viewRecords(sql, headers, cols);
    }

    public void createSale(int userId) {
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sqlCreate = "INSERT INTO tbl_sale (u_id, status, date, total) VALUES (?,?,?,?)";
        conf.addRecord(sqlCreate, userId, "Open", date, 0.0);

        Object o = conf.querySingleValue("SELECT s_id FROM tbl_sale WHERE u_id=? ORDER BY s_id DESC LIMIT 1", userId);
        if (o == null) {
            System.out.println("Failed to create sale header.");
            return;
        }
        int saleId = Integer.parseInt(o.toString());
        System.out.println("Created Sale ID: " + saleId);

        double total = 0.0;
        while (true) {
            fishManager.listFish();
            System.out.print("Enter Fish ID to add (0 to finish): ");
            int fid = readIntSafe();
            if (fid == 0) break;

            Map<String, Object> fish = fishManager.getFishById(fid);
            if (fish == null) {
                System.out.println("Fish not found.");
                continue;
            }
            System.out.print("Enter quantity (kg): ");
            int qty = readIntSafe();

            int stock = Integer.parseInt(fish.get("f_stockkg").toString());
            if (qty > stock) {
                System.out.println("Not enough stock. Available: " + stock + " kg");
                continue;
            }

            double price = Double.parseDouble(fish.get("f_price").toString());
            double subtotal = price * qty;
            total += subtotal;

            String sqlDetail = "INSERT INTO tbl_saledetail (s_id, f_id, quantity_kg, subtotal) VALUES (?,?,?,?)";
            conf.addRecord(sqlDetail, saleId, fid, qty, subtotal);

            fishManager.decreaseStock(fid, qty);

            System.out.println("Added " + qty + "kg of " + fish.get("f_name") + " - subtotal: " + subtotal);
        }

        conf.updateRecord("UPDATE tbl_sale SET total=?, status=? WHERE s_id=?", total, "Closed", saleId);
        System.out.println("Sale completed. Total: " + total);

        showSaleReceipt(saleId);
    }

    public void showSaleReceipt(int saleId) {
        System.out.println("\n=== SALE RECEIPT ===");
        List<Map<String, Object>> sale = conf.fetchRecords("SELECT * FROM tbl_sale WHERE s_id=?", saleId);
        if (sale.isEmpty()) {
            System.out.println("Sale not found.");
            return;
        }
        Map<String, Object> s = sale.get(0);
        System.out.println("Sale ID: " + s.get("s_id"));
        System.out.println("Date: " + s.get("date"));
        System.out.println("Total: " + s.get("total"));

        System.out.println("\nItems:");
        List<Map<String, Object>> items = conf.fetchRecords(
            "SELECT sd.*, f.f_name FROM tbl_saledetail sd LEFT JOIN tbl_fish f ON sd.f_id=f.f_id WHERE sd.s_id=?", saleId);
        for (Map<String, Object> it : items) {
            System.out.printf("- %s | %s kg | subtotal: %s\n", it.get("f_name"), it.get("quantity_kg"), it.get("subtotal"));
        }

        Object paidObj = conf.querySingleValue("SELECT IFNULL(SUM(amount),0) FROM tbl_payment WHERE s_id=?", saleId);
        double paid = paidObj == null ? 0.0 : Double.parseDouble(paidObj.toString());
        double total = Double.parseDouble(s.get("total").toString());
        System.out.println("\nPaid: " + paid);
        System.out.println("Remaining: " + (total - paid));
    }

    private int readIntSafe() {
        while (true) {
            String line = sc.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}
package main;

import config.config;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FishManager {

    private config conf;
    private Scanner sc;

    public FishManager(config conf, Scanner sc) {
        this.conf = conf;
        this.sc = sc;
    }

    // show menu - adminFull true -> allow add/edit/delete
    public void showMenu(int userId, boolean adminFull) {
        int choice = -1;
        do {
            System.out.println("\n--- Fish Management ---");
            System.out.println("1. List Fish");
            if (adminFull) {
                System.out.println("2. Add Fish");
                System.out.println("3. Edit Fish");
                System.out.println("4. Delete Fish");
                System.out.println("5. Back");
            } else {
                System.out.println("2. Back");
            }
            System.out.print("Choice: ");
            choice = readIntSafe();

            if (adminFull) {
                switch (choice) {
                    case 1:
                        listFish();
                        break;
                    case 2:
                        addFish(userId);
                        break;
                    case 3:
                        editFish();
                        break;
                    case 4:
                        deleteFish();
                        break;
                    case 5:
                        break;
                    default:
                        System.out.println("Invalid choice!");
                }
            } else {
                switch (choice) {
                    case 1:
                        listFish();
                        break;
                    case 2:
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } while ((adminFull && choice != 5) || (!adminFull && choice != 2));
    }

    public void listFish() {
        String sql = "SELECT * FROM tbl_fish";
        String[] headers = {"ID", "Name", "Price", "Stock(kg)", "Added By"};
        String[] cols = {"f_id", "f_name", "f_price", "f_stockkg", "u_id"};
        conf.viewRecords(sql, headers, cols);
    }

    public void addFish(int userId) {
        System.out.print("Fish name: ");
        String name = sc.nextLine();
        System.out.print("Price (per kg): ");
        double price = readDoubleSafe();
        System.out.print("Stock (kg): ");
        int stock = readIntSafe();

        String sql = "INSERT INTO tbl_fish (f_name, f_price, f_stockkg, u_id) VALUES (?,?,?,?)";
        conf.addRecord(sql, name, price, stock, userId);
    }

    public void editFish() {
        System.out.print("Enter Fish ID to edit: ");
        int id = readIntSafe();

        System.out.print("New Name: ");
        String name = sc.nextLine();
        System.out.print("New Price: ");
        double price = readDoubleSafe();
        System.out.print("New Stock (kg): ");
        int stock = readIntSafe();

        String sql = "UPDATE tbl_fish SET f_name=?, f_price=?, f_stockkg=? WHERE f_id=?";
        conf.updateRecord(sql, name, price, stock, id);
    }

    public void deleteFish() {
        System.out.print("Enter Fish ID to delete: ");
        int id = readIntSafe();

        String sql = "DELETE FROM tbl_fish WHERE f_id=?";
        conf.deleteRecord(sql, id);
    }

    public Map<String, Object> getFishById(int fid) {
        List<Map<String, Object>> rows = conf.fetchRecords("SELECT * FROM tbl_fish WHERE f_id=?", fid);
        if (rows.isEmpty()) return null;
        return rows.get(0);
    }

    public void decreaseStock(int fid, int qty) {
        Object o = conf.querySingleValue("SELECT f_stockkg FROM tbl_fish WHERE f_id=?", fid);
        if (o == null) {
            System.out.println("Fish not found.");
            return;
        }
        int current = Integer.parseInt(o.toString());
        int updated = current - qty;
        if (updated < 0) updated = 0;
        conf.updateRecord("UPDATE tbl_fish SET f_stockkg=? WHERE f_id=?", updated, fid);
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

    private double readDoubleSafe() {
        while (true) {
            String line = sc.nextLine();
            try {
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number (decimal allowed): ");
            }
        }
    }
}
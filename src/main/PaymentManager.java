package main;

import config.config;
import java.util.*;

public class PaymentManager {

    private config conf;
    private Scanner sc;

    public PaymentManager(config conf, Scanner sc) {
        this.conf = conf;
        this.sc = sc;
    }

    public void showMenu() {
        int ch;
        do {
            System.out.println("\n--- Payment Menu ---");
            System.out.println("1. Make Payment");
            System.out.println("2. View Payments by Sale ID");
            System.out.println("3. Back");
            System.out.print("Choice: ");
            ch = readIntSafe();

            switch (ch) {
                case 1:
                    makePayment();
                    break;
                case 2:
                    viewPayments();
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Invalid");
            }
        } while (ch != 3);
    }

    public void makePayment() {
        System.out.print("Enter Sale ID: ");
        int sid = readIntSafe();

        Object totalObj = conf.querySingleValue("SELECT total FROM tbl_sale WHERE s_id=?", sid);
        if (totalObj == null) {
            System.out.println("Sale not found.");
            return;
        }
        double total = Double.parseDouble(totalObj.toString());
        Object paidObj = conf.querySingleValue("SELECT IFNULL(SUM(amount),0) FROM tbl_payment WHERE s_id=?", sid);
        double paid = paidObj == null ? 0.0 : Double.parseDouble(paidObj.toString());

        double remaining = total - paid;
        System.out.println("Total: " + total + " | Already Paid: " + paid + " | Remaining: " + remaining);

        System.out.print("Enter amount to pay: ");
        double amt = readDoubleSafe();

        if (amt <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        conf.addRecord("INSERT INTO tbl_payment (s_id, payment_date, amount) VALUES (?,?,?)", sid, date, amt);
        System.out.println("Payment recorded.");

        Object newPaidObj = conf.querySingleValue("SELECT IFNULL(SUM(amount),0) FROM tbl_payment WHERE s_id=?", sid);
        double newPaid = newPaidObj == null ? 0.0 : Double.parseDouble(newPaidObj.toString());
        System.out.println("New paid total: " + newPaid + " | Remaining: " + (total - newPaid));
    }

    public void viewPayments() {
        System.out.print("Enter Sale ID: ");
        int sid = readIntSafe();

        String sql = "SELECT * FROM tbl_payment WHERE s_id=?";
        List<Map<String, Object>> rows = conf.fetchRecords(sql, sid);
        if (rows.isEmpty()) {
            System.out.println("No payments found.");
            return;
        }
        System.out.println("-----------------------------------------------------");
        System.out.printf("| %-10s | %-6s | %-20s | %-8s |\n", "PaymentID", "SaleID", "Date", "Amount");
        System.out.println("-----------------------------------------------------");
        for (Map<String, Object> r : rows) {
            System.out.printf("| %-10s | %-6s | %-20s | %-8s |\n",
                    r.get("p_id"), r.get("s_id"), r.get("payment_date"), r.get("amount"));
        }
        System.out.println("-----------------------------------------------------");
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

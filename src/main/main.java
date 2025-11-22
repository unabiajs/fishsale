package main;

import java.util.Scanner;
import java.util.List;
import java.util.Map;
import config.config;

public class main {

    Scanner sc = new Scanner(System.in);
    config conf = new config();
    FishManager fishManager;
    SaleManager saleManager;
    SaleDetailManager saleDetailManager;
    PaymentManager paymentManager;

    public static void main(String[] args) {
        main system = new main();
        system.system();
    }

    public main() {
        fishManager = new FishManager(conf, sc);
        saleManager = new SaleManager(conf, sc, fishManager);
        paymentManager = new PaymentManager(conf, sc);
        saleDetailManager = new SaleDetailManager(conf, sc);

    }

    public void system() {
        int choice;

        do {
            System.out.println("\n===== FISHSale Tracker =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            choice = readIntSafe();

            switch (choice) {
                case 1:
                    loginFlow();
                    break;

                case 2:
                    registerFlow();
                    break;

                case 3:
                    System.out.println(" Exiting... Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 3);

        sc.close();
    }

    private void loginFlow() {
        System.out.print("Enter email: ");
        String email = sc.nextLine();
        System.out.print("Enter password: ");
        String pass = sc.nextLine();

        String hashpass = conf.hashPassword(pass);

        List<Map<String, Object>> result = conf.fetchRecords("SELECT * FROM tbl_user WHERE u_email=? AND u_pass=?", email, hashpass);

        if (result.isEmpty()) {
            System.out.println("❌ Invalid Credentials!");
            return;
        }

        Map<String, Object> user = result.get(0);
        String status = user.get("u_status").toString();
        String type = user.get("u_type").toString();
        String uname = user.get("u_name").toString();
        int uid = Integer.parseInt(user.get("u_id").toString());

        if (status.equalsIgnoreCase("Pending")) {
            System.out.println(" Account is still pending approval. Contact Admin.");
        } else {
            System.out.println(" Login Successful! Welcome, " + uname);

            if (type.equalsIgnoreCase("Admin")) {
                adminDash(uid);
            } else {
<<<<<<< HEAD
                costumerDash(uid);
=======
                costumersDash(uid);
>>>>>>> 56c11089707bdcbaa719fc0d6f5b560c72eacc05
            }
        }
    }

    private void registerFlow() {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Email: ");
        String regEmail = sc.nextLine();

        
        List<Map<String, Object>> chkResult = conf.fetchRecords("SELECT * FROM tbl_user WHERE u_email=?", regEmail);
        while (!chkResult.isEmpty()) {
            System.out.print(" Email already exists. Enter another: ");
            regEmail = sc.nextLine();
            chkResult = conf.fetchRecords("SELECT * FROM tbl_user WHERE u_email=?", regEmail);
        }

        System.out.print("Enter Password: ");
        String regPass = sc.nextLine();
        String hashedpass = conf.hashPassword(regPass);

        int tp;
        while (true) {
<<<<<<< HEAD
            System.out.print("Enter User Type (1 - Admin / 2 - Costumer): ");
=======
            System.out.print("Enter User Type (1 - Admin / 2 - Costumers): ");
>>>>>>> 56c11089707bdcbaa719fc0d6f5b560c72eacc05
            tp = readIntSafe();
            if (tp == 1 || tp == 2) break;
            System.out.println("Invalid input. Enter 1 or 2.");
        }

        String status, utype = "";
        if (tp == 1) {
            utype = "Admin";
             String checkAdmin = "SELECT * FROM tbl_user WHERE u_type = ?";
            java.util.List<java.util.Map<String, Object>> adminCheck = conf.fetchRecords(checkAdmin, "Admin");

            if (adminCheck.isEmpty()) {
                status = "Approved"; 
            } else {
                status = "Pending";   
            }

        String sql = "INSERT INTO tbl_user (u_name, u_email, u_type, u_status, u_pass) VALUES (?,?,?,?,?)";
        conf.addRecord(sql, name, regEmail, utype, status, hashedpass);
    }    
    else if (tp == 2) {  
<<<<<<< HEAD
        utype = "Costumer";
=======
        utype = "Costumers";
>>>>>>> 56c11089707bdcbaa719fc0d6f5b560c72eacc05
        status = "Pending";

        String sql = "INSERT INTO tbl_user (u_name, u_email, u_type, u_status, u_pass) VALUES (?,?,?,?,?)";
        conf.addRecord(sql, name, regEmail, utype, status, hashedpass);
    }
}
    public void adminDash(int adminId) {
        int resp;
        do {
            System.out.println("\n🔹 Admin Dashboard");
            System.out.println("1. View Accounts");
            System.out.println("2. Approve an Account");
            System.out.println("3. Delete Account");
            System.out.println("4. Manage Fish");
            System.out.println("5. View Sales");
            System.out.println("6. View Sale Details");
            System.out.println("7. Logout Account");
            
    
            resp = readIntSafe();

            switch (resp) {
                case 1:
                    viewAcc();
                    break;
                case 2:
                    viewAcc();
                    appAcc();
                    break;
                case 3:
                    delAcc();
                    break;
                case 4:
                    fishManager.showMenu(adminId, true); 
                    break;
                case 5:
                    saleManager.viewAllSales();
                    break;
                case 6:
                    saleDetailManager.viewDetailsBySaleId();
                    break;
                case 7:
                    System.out.println("Logging out your account!");
                    break;

            }
        } while (resp != 7);
    }

<<<<<<< HEAD
    public void costumerDash(int uid) {
=======
    public void costumersDash(int uid) {
>>>>>>> 56c11089707bdcbaa719fc0d6f5b560c72eacc05
        int resp;
        do {
            System.out.println("\n🔹 Costumer Dashboard");
            System.out.println("1. View Fish");
            System.out.println("2. Create Sale");
            System.out.println("3. Make Payment");
            System.out.println("4. Logout");
            System.out.print("Enter Choice: ");
            resp = readIntSafe();

            switch (resp) {
                case 1:
                    fishManager.showMenu(uid, false); 
                    break;
                case 2:
                    saleManager.createSale(uid);
                    break;
                case 3:
                    paymentManager.showMenu();
                    break;
                case 4:
                    System.out.println("Logging out!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while (resp != 4);
    }

    public void viewAcc() {
        String sql = "SELECT * FROM tbl_user";
        String[] userHeaders = {"ID", "Name", "Email", "Role", "Status"};
        String[] userColumns = {"u_id", "u_name", "u_email", "u_type", "u_status"};
        conf.viewRecords(sql, userHeaders, userColumns);
    }

    public void appAcc() {
        System.out.print("Enter Email to Approve: ");
        String approveEmail = sc.nextLine();
        String sql = "UPDATE tbl_user SET u_status=? WHERE u_email=?";
        conf.updateRecord(sql, "Active", approveEmail);
        System.out.println(" User approved successfully!");
    }

    public void delAcc() {
        System.out.print("Enter ID to delete: ");
        int id = readIntSafe();

        String sql = "DELETE FROM tbl_user WHERE u_id = ?";
        conf.deleteRecord(sql, id);
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
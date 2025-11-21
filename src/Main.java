

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        int choice;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println("\n=== RENTAL KENDARAAN ===");
            System.out.println("1. Lihat kendaraan");
            System.out.println("2. Sewa kendaraan");
            System.out.println("3. Kembalikan kendaraan");
            System.out.println("4. Keluar");
            System.out.print("Pilih: ");

            choice = sc.nextInt();

            switch (choice) {
                case 1:
                   System.out.println("blablablbla");
                    break;

                case 2:
                    System.out.println("blabsuebfes");
                    break;

                case 3:
                    System.out.println("yoho");
                    break;

                case 4:
                    System.out.println("Keluar...");
                    break;
            }

        } while (choice != 4);
    }
}

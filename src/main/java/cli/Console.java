package cli;

import java.util.*;

public class Console {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Client client = new Client(3);

        System.out.println("Simple Distributed File System Started.");
        System.out.println("Commands: open read write close exit");

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            String[] parts = line.split(" ", 3);
            String cmd = parts[0];

            if (cmd.equals("exit")) {
                client.exit();
                break;
            }

            switch (cmd) {
                case "open":
                    if (parts.length < 2) {
                        System.out.println("Usage: open <filename>");
                        break;
                    }
                    client.open(parts[1]);
                    break;

                case "read":
                    if (parts.length < 2) {
                        System.out.println("Usage: read <filename> (<length>)");
                        break;
                    }
                    byte[] content = client.read(parts[1], parts.length == 3 ? Integer.parseInt(parts[2]): -1);
                    if (content != null) {
                        System.out.println(new String(content));
                    }
                    break;

                case "write":
                    if (parts.length < 2) {
                        System.out.println("Usage: write <filename>");
                        break;
                    }
                    System.out.println("Enter data to write:");
                    String data = scanner.nextLine();
                    client.write(parts[1], data.getBytes());
                    break;

                case "close":
                    if (parts.length < 2) {
                        System.out.println("Usage: close <filename>");
                        break;
                    }
                    client.close(parts[1]);
                    break;

                default:
                    System.out.println("Unknown command.");
                    break;
            }
        }

        scanner.close();
        System.out.println("System exited.");
    }
}

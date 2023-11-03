import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class FileClient {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1"; // Dirección IP del servidor
        int serverPort = 12345; // Puerto del servidor

        try (Socket socket = new Socket(serverAddress, serverPort);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            Scanner scanner = new Scanner(System.in);

            System.out.println("1. Cargar archivo");
            System.out.println("2. Descargar archivo");
            System.out.print("Ingrese su elección (1/2): ");
            int choice = scanner.nextInt();

            if (choice == 1) {
                // Subir archivo
                dos.writeUTF("UPLOAD");

                System.out.print("Ingrese el nombre del archivo a cargar: ");
                String fileName = scanner.next();

                File file = new File(fileName);
                long fileSize = file.length();

                dos.writeUTF(fileName);
                dos.writeLong(fileSize);

                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
                fis.close();
                System.out.println("Archivo cargado: " + fileName);
            } else if (choice == 2) {
                // Descargar archivo
                dos.writeUTF("DOWNLOAD");

                System.out.print("Ingrese el nombre del archivo a descargar: ");
                String fileName = scanner.next();
                dos.writeUTF(fileName);

                long fileSize = dis.readLong();

                if (fileSize > 0) {
                    File file = new File(fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = dis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    fos.close();
                    System.out.println("Archivo descargado: " + fileName);
                } else {
                    System.out.println("El archivo no existe en el servidor.");
                }
            } else {
                System.out.println("Elección no válida.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

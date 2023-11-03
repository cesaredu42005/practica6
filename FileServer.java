import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    public static void main(String[] args) {
        int port = 12345; // Puerto del servidor

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("El servidor está escuchando en el puerto " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde " + clientSocket.getInetAddress().getHostAddress());

                try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                     DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

                    // Leer la solicitud del cliente
                    String request = dis.readUTF();

                    if (request.equals("UPLOAD")) {
                        // Si el cliente solicita subir un archivo, maneja la carga aquí
                        handleUpload(dis);
                    } else if (request.equals("DOWNLOAD")) {
                        // Si el cliente solicita descargar un archivo, maneja la descarga aquí
                        handleDownload(dis, dos);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para manejar la carga de archivos
    private static void handleUpload(DataInputStream dis) throws IOException {
        String fileName = dis.readUTF();
        long fileSize = dis.readLong();

        if (fileSize > 0) {
            FileOutputStream fos = new FileOutputStream(fileName);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = dis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            System.out.println("Archivo cargado: " + fileName);
            fos.close();
        } else {
            System.out.println("Error al cargar el archivo: " + fileName);
        }
    }

    // Método para manejar la descarga de archivos
    private static void handleDownload(DataInputStream dis, DataOutputStream dos) throws IOException {
        String fileName = dis.readUTF();

        File file = new File(fileName);

        if (file.exists()) {
            dos.writeLong(file.length());
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
            System.out.println("Archivo enviado: " + fileName);
            fis.close();
        } else {
            dos.writeLong(0); // Indica que el archivo no existe
            System.out.println("El archivo no existe en el servidor: " + fileName);
        }
    }
}

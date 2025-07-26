import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private int productId;
    private String name;
    private double price;
    private String description;
    private List<String> imagePaths;
    private List<ImageIcon> imageIcons;

    public Product(int productId, String name, double price, String description, List<String> imagePaths) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imagePaths = imagePaths;
        this.imageIcons = loadImages(imagePaths);
    }

    private List<ImageIcon> loadImages(List<String> paths) {
        List<ImageIcon> icons = new ArrayList<>();
        for (String path : paths) {
            ImageIcon icon = loadSingleImage(path);
            icons.add(icon);
        }
        return icons;
    }

    private ImageIcon loadSingleImage(String path) {
        try {
            // Try to load from resources
            URL resource = getClass().getClassLoader().getResource(path);
            if (resource != null) {
                BufferedImage img = ImageIO.read(resource);
                return new ImageIcon(img); // Store native resolution
            }

            // Try to load from project root
            File file = new File(path);
            System.out.println("Checking file: " + file.getAbsolutePath());
            if (file.exists()) {
                BufferedImage img = ImageIO.read(file);
                return new ImageIcon(img); // Store native resolution
            }

            System.out.println("Image not found: " + path);
            return createPlaceholderImage();
        } catch (IOException e) {
            System.out.println("Could not load image: " + path);
            return createPlaceholderImage();
        }
    }

    public ImageIcon createPlaceholderImage() {
        BufferedImage placeholder = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = placeholder.createGraphics();
        g2d.setColor(new Color(220, 220, 220));
        g2d.fillRect(0, 0, 300, 300);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("No Image", 80, 150);
        g2d.dispose();
        return new ImageIcon(placeholder);
    }

    // Getters
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public List<String> getImagePaths() { return imagePaths; }
    public List<ImageIcon> getImageIcons() { return imageIcons; }
    public ImageIcon getImageIcon() { return imageIcons.isEmpty() ? createPlaceholderImage() : imageIcons.get(0); }
}
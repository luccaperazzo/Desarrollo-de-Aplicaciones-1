package ar.edu.uade.recipes.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageHelper {

    /**
     * Convierte una imagen URI a Base64
     * @param context Context de la aplicación
     * @param imageUri URI de la imagen
     * @return String en formato Base64 o null si hay error
     */
    public static String convertImageToBase64(Context context, Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            Bitmap resized = resizeBitmap(bitmap, 800);
            return bitmapToBase64(resized);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Redimensiona un bitmap manteniendo el aspect ratio
     * @param bitmap Bitmap original
     * @param maxDimension Dimensión máxima (ancho o alto)
     * @return Bitmap redimensionado
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, int maxDimension) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxDimension && height <= maxDimension) {
            return bitmap;
        }

        float scale = Math.min((float) maxDimension / width, (float) maxDimension / height);
        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    /**
     * Convierte un Bitmap a String Base64
     * @param bitmap Bitmap a convertir
     * @return String en formato Base64
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    /**
     * Convierte un Bitmap a String Base64 con calidad personalizada
     * @param bitmap Bitmap a convertir
     * @param quality Calidad de compresión (0-100)
     * @return String en formato Base64
     */
    public static String bitmapToBase64(Bitmap bitmap, int quality) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }
}

